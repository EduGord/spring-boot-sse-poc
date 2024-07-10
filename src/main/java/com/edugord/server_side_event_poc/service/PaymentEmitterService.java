package com.edugord.server_side_event_poc.service;

import com.edugord.server_side_event_poc.model.entity.Payment;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class PaymentEmitterService {

    private final ConcurrentHashMap<UUID, CopyOnWriteArraySet<SseEmitter>> sseEmitters = new ConcurrentHashMap<>();

    public void addPaymentEmitter(UUID paymentId) {
        sseEmitters.putIfAbsent(paymentId, new CopyOnWriteArraySet<>());
    }

    public SseEmitter subscribeToPaymentStatus(UUID paymentId) {
        var emitter = new SseEmitter();
        var emitters = sseEmitters.get(paymentId);
        if (emitters != null) {
            emitters.add(emitter);
        }
        return emitter;
    }

    public void notifyPaymentStatus(Payment payment) {
        var emitters = sseEmitters.get(payment.getId());
        if (emitters != null) {
            for (var emitter : emitters) {
                try {
                    emitter.send(payment);
                    emitter.complete();
                } catch (Exception e) {
                    Optional.ofNullable(emitter).ifPresent(emitters::remove);
                }
            }
        }
    }

    public void notifySingleEmitter(Payment payment, SseEmitter emitter) {
        try {
            emitter.send(payment);
            emitter.complete();
        } catch (Exception e) {
            Optional.ofNullable(sseEmitters.get(payment.getId())).ifPresent(emitters -> emitters.remove(emitter));
        }
    }
}