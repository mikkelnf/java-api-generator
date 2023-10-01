package com.mnf.javaapigenerator.service;

import com.mnf.javaapigenerator.dto.GeneratedApiResponseDto;
import com.mnf.javaapigenerator.dto.RequestDto;
import com.mnf.javaapigenerator.result.component.dto.ResponseDto;

public interface IApiGeneratorService {
    ResponseDto<GeneratedApiResponseDto> generate(RequestDto requestDto);
}
