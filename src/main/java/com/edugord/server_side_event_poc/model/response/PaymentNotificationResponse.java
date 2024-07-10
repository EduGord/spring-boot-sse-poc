package com.edugord.server_side_event_poc.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentNotificationResponse(UUID paymentId,
                                          LocalDateTime paidAt,
                                          Long uniqueSequenceNumber,
                                          String authorizationCode,
                                          BigDecimal amount) {
}