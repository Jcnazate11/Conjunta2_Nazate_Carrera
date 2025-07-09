package com.espe.ms_carenotifier.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PushNotificationRequestDTO {
    private String recipient;
    private String message;
}