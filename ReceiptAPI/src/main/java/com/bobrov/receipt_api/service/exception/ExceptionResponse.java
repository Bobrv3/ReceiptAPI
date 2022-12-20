package com.bobrov.receipt_api.service.exception;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ExceptionResponse {
    private String message;
    private String type;
    private int statusCode;
    private String createdAt;
}
