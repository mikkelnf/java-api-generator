package com.mnf.javaapigenerator.component.dto;

import com.mnf.javaapigenerator.component.enumeration.ResponseDtoStatusEnum;

public class ResponseStatusOnlyDto extends AResponseDto {
    public ResponseStatusOnlyDto() {
    }
    public ResponseStatusOnlyDto(ResponseDtoStatusEnum status) {
        super.setStatus(status);
    }
    public ResponseStatusOnlyDto(ResponseDtoStatusEnum status, String message) {
        super.setStatus(status);
        super.setMessage(message);
    }
}
