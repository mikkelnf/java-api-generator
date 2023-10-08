package com.mnf.javaapigenerator.dto;

import java.util.List;

public class RequestDto {
    private String apiName;
    private String basePackage;
    private List<FieldRequestDto> additionalFields;
    private FieldRequestDto additionalGetOne;
    private String additionalUniqueField;
    private List<String> configuredFields;

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public List<FieldRequestDto> getAdditionalFields() {
        return additionalFields;
    }

    public void setAdditionalFields(List<FieldRequestDto> additionalFields) {
        this.additionalFields = additionalFields;
    }

    public FieldRequestDto getAdditionalGetOne() {
        return additionalGetOne;
    }

    public void setAdditionalGetOne(FieldRequestDto additionalGetOne) {
        this.additionalGetOne = additionalGetOne;
    }

    public String getAdditionalUniqueField() {
        return additionalUniqueField;
    }

    public void setAdditionalUniqueField(String additionalUniqueField) {
        this.additionalUniqueField = additionalUniqueField;
    }

    public List<String> getConfiguredFields() {
        return configuredFields;
    }

    public void setConfiguredFields(List<String> configuredFields) {
        this.configuredFields = configuredFields;
    }
}
