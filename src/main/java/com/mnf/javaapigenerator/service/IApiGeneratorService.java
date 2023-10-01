package com.mnf.javaapigenerator.service;

import com.mnf.javaapigenerator.component.dto.ResponseDto;
import com.mnf.javaapigenerator.dto.GeneratedApiResponseDto;
import com.mnf.javaapigenerator.dto.RequestDto;

public interface IApiGeneratorService {
    ResponseDto<GeneratedApiResponseDto> generate(RequestDto requestDto);
}
