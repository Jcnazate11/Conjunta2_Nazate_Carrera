package com.espe.ms_carenotifier.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SMSRequestDTO {
    private String phoneNumber;
    private String message;
}