package com.mnf.javaapigenerator.service;

import com.mnf.javaapigenerator.ApiGenerator;
import com.mnf.javaapigenerator.dto.GeneratedApiResponseDto;
import com.mnf.javaapigenerator.dto.RequestDto;
import com.mnf.javaapigenerator.result.component.dto.ResponseDto;

public class ApiGeneratorServiceImpl implements IApiGeneratorService {
    @Override
    public ResponseDto<GeneratedApiResponseDto> generate(RequestDto requestDto) {
        ResponseDto<GeneratedApiResponseDto> generatedApiResponseDto = new ResponseDto<>();
        String generatedZipFile = ApiGenerator.generate(requestDto);
        generatedApiResponseDto.setContent(new GeneratedApiResponseDto(generatedZipFile));

        return generatedApiResponseDto;
    }
}
