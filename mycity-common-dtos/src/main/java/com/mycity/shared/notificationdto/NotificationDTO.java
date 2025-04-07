package com.mycity.shared.notificationdto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    private Long id;
    private Long userId;
    private String title;
    private String message;
    private String notificationType;
    private LocalDateTime createdAt;
}

