package com.edugord.server_side_event_poc.controller;

import com.edugord.server_side_event_poc.model.request.PaymentRequest;
import com.edugord.server_side_event_poc.model.response.PaymentResponse;
import com.edugord.server_side_event_poc.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;


@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/pay")
    public ResponseEntity<PaymentResponse> pay(@RequestBody PaymentRequest paymentRequest) {
        return ResponseEntity.ok(paymentService.processPayment(paymentRequest));
    }

    @GetMapping("/status/{paymentId}")
    public SseEmitter subscribeToPaymentStatus(@PathVariable UUID paymentId) {
        return paymentService.subscribeToPaymentStatus(paymentId);
    }
}