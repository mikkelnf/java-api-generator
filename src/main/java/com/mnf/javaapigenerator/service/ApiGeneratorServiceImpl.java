package com.mnf.javaapigenerator.service;

import com.mnf.compos.dto.ResponseDto;
import com.mnf.javaapigenerator.util.ApiGeneratorUtil;
import com.mnf.javaapigenerator.dto.GeneratedApiResponseDto;
import com.mnf.javaapigenerator.dto.RequestDto;

public class ApiGeneratorServiceImpl implements IApiGeneratorService {
    @Override
    public ResponseDto<GeneratedApiResponseDto> generate(RequestDto requestDto) {
        ResponseDto<GeneratedApiResponseDto> generatedApiResponseDto = new ResponseDto<>();

        return generatedApiResponseDto;
    }
}
