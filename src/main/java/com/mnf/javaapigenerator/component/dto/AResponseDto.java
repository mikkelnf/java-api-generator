package com.mnf.javaapigenerator.component.dto;

import com.mnf.javaapigenerator.component.enumeration.ResponseDtoStatusEnum;

public abstract class AResponseDto{
    private ResponseDtoStatusEnum status;
    private String message;

    public ResponseDtoStatusEnum getStatus() {
        return status;
    }

    public void setStatus(ResponseDtoStatusEnum status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "AResponseDto{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
