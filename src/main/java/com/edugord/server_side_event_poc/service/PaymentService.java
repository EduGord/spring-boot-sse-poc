package com.edugord.server_side_event_poc.service;

import com.edugord.server_side_event_poc.exception.EntityNotFoundException;
import com.edugord.server_side_event_poc.model.entity.Payment;
import com.edugord.server_side_event_poc.model.enumerator.PaymentStatusEnum;
import com.edugord.server_side_event_poc.model.request.PaymentRequest;
import com.edugord.server_side_event_poc.model.response.PaymentNotificationResponse;
import com.edugord.server_side_event_poc.model.response.PaymentResponse;
import com.edugord.server_side_event_poc.repository.PaymentRepository;
import com.edugord.server_side_event_poc.util.SecurityUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class PaymentService {

    public static final String PAYMENT_NOT_FOUND_ERROR_MESSAGE = "Payment not found";
    public static final String NOT_AUTHORIZED_ERROR_MESSAGE = "You are not authorized to access this resource";
    public static final int TIME_TO_WAIT_BEFORE_NOTIFYING_IF_EXISTING = 3000;
    public static final int TIME_TO_WAIT_TO_MIMIC_PAYMENT_PROCESSING_DELAY = 5000;
    public static final String ERROR_WHILE_WAITING_TO_NOTIFY_PAYMENT_STATUS_LOG_TEMPLATE = "Error while waiting to notify payment status: ";

    private final Random random;
    private final PaymentEmitterService paymentEmitterService;
    private final PaymentRepository paymentRepository;
    private final Logger log = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    public PaymentService(PaymentEmitterService paymentEmitterService,
                          PaymentRepository paymentRepository) {
        this.paymentEmitterService = paymentEmitterService;
        this.paymentRepository = paymentRepository;
        this.random = new Random();
    }

    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        var payment = new Payment(PaymentStatusEnum.PENDING.name(),
                BigDecimal.valueOf(paymentRequest.amount()),
                SecurityUtil.getUserId());
        paymentRepository.save(payment);
        paymentEmitterService.addPaymentEmitter(payment.getId());
        new Thread(() -> simulateAsynchronousPaymentProcessing(paymentRequest, payment.getId())).start();
        return new PaymentResponse(payment.getId());
    }

    private void simulateAsynchronousPaymentProcessing(PaymentRequest paymentRequest, UUID paymentId) {
        try (ScheduledExecutorService executor = Executors.newScheduledThreadPool(1)) {
            executor.schedule(() -> {
                PaymentNotificationResponse paymentResponse = generateSimulatedPaymentResponse(paymentRequest, paymentId);
                Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new EntityNotFoundException(PAYMENT_NOT_FOUND_ERROR_MESSAGE));

                payment = updatePayment(payment, paymentResponse);
                paymentEmitterService.notifyPaymentStatus(payment);
            }, TIME_TO_WAIT_TO_MIMIC_PAYMENT_PROCESSING_DELAY, TimeUnit.MILLISECONDS);
        }
    }

    private PaymentNotificationResponse generateSimulatedPaymentResponse(PaymentRequest paymentRequest, UUID paymentId) {
        return new PaymentNotificationResponse(paymentId,
                LocalDateTime.now(),
                random.nextLong(1000),
                RandomStringUtils.randomAlphabetic(8),
                BigDecimal.valueOf(paymentRequest.amount()));
    }

    private Payment updatePayment(Payment payment,
                                  PaymentNotificationResponse paymentNotificationResponse) {
        payment.setPaidAt(LocalDateTime.now());
        payment.setStatus(PaymentStatusEnum.PAID.name());
        payment.setAuthorizationCode(paymentNotificationResponse.authorizationCode());
        payment.setUniqueSequenceNumber(paymentNotificationResponse.uniqueSequenceNumber());
        payment.setIsNew(false);
        return paymentRepository.save(payment);
    }

    public SseEmitter subscribeToPaymentStatus(UUID paymentId)
            throws EntityNotFoundException, AccessDeniedException {
        var userId = SecurityUtil.getUserId();
        var payment = paymentRepository.findById(paymentId).orElseThrow(() -> new EntityNotFoundException(PAYMENT_NOT_FOUND_ERROR_MESSAGE));
        if (!payment.getUserId().equals(userId)) {
            throw new AccessDeniedException(NOT_AUTHORIZED_ERROR_MESSAGE);
        }

        var emitter = paymentEmitterService.subscribeToPaymentStatus(paymentId);

        if (!PaymentStatusEnum.PENDING.name().equals(payment.getStatus())) {
            waitThenNotify(payment, emitter);
        }

        return emitter;
    }

    private void waitThenNotify(Payment payment, SseEmitter emitter) {
        Thread.ofVirtual().start(() -> {
            try {
                Thread.sleep(TIME_TO_WAIT_BEFORE_NOTIFYING_IF_EXISTING);
                paymentEmitterService.notifySingleEmitter(payment, emitter);
            } catch (InterruptedException e) {
                log.error(ERROR_WHILE_WAITING_TO_NOTIFY_PAYMENT_STATUS_LOG_TEMPLATE, e);
                Thread.currentThread().interrupt();
            }
        });
    }
}
