package com.mnf.javaapigenerator.service;

import com.mnf.compos.dto.ResponseDto;
import com.mnf.javaapigenerator.dto.GeneratedApiResponseDto;
import com.mnf.javaapigenerator.dto.RequestDto;

public interface IApiGeneratorService {
    ResponseDto<GeneratedApiResponseDto> generate(RequestDto requestDto);
}
