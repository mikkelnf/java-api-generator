package com.mnf.javaapigenerator.dto;

import java.util.List;

public class RequestDto {
    private String apiName;
    private String basePackage;
    private List<ColumnRequestDto> columns;

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

    public List<ColumnRequestDto> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnRequestDto> columns) {
        this.columns = columns;
    }
}
