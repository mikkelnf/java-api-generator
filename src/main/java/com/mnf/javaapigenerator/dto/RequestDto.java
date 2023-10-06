package com.mnf.javaapigenerator.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RequestDto {
    private String apiName;
    private String basePackage;
    private List<ColumnRequestDto> columns;
}
