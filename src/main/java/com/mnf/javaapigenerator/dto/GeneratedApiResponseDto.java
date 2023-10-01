package com.mnf.javaapigenerator.dto;

public class GeneratedApiResponseDto {
    public GeneratedApiResponseDto(String zipFile) {
        this.zipFile = zipFile;
    }

    private String zipFile;

    public String getZipFile() {
        return zipFile;
    }

    public void setZipFile(String zipFile) {
        this.zipFile = zipFile;
    }
}
