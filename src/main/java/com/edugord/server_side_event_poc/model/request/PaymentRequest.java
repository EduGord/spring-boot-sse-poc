package com.edugord.server_side_event_poc.model.request;

public record PaymentRequest(String holder,
                             String expiryDate,
                             String cardNumber,
                             String cvv,
                             Double amount) {
}