package com.espe.ms_carenotifier.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequestDTO {
    private String recipient;
    private String subject;
    private String body;
}