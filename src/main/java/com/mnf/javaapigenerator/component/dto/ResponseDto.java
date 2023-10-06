package com.mnf.javaapigenerator.component.dto;

import com.mnf.javaapigenerator.component.enumeration.ResponseDtoStatusEnum;

public class ResponseDto<T> extends AResponseDto {
    private T content;

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public ResponseDto() {
    }

    public ResponseDto(ResponseDtoStatusEnum status, String message, T content) {
        super.setStatus(status);
        super.setMessage(message);
        this.content = content;
    }
}
