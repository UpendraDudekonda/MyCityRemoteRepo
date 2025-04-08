package com.mycity.shared.paymentdto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {

    private Long paymentId;
    private Long userId;
    private Long bookingId;
    private Double amount;
    private String currency;
    private String paymentMethod;
    private String transactionId;
    private String paymentStatus;
    private LocalDateTime paymentDate;
    private String failureReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    
  
}

