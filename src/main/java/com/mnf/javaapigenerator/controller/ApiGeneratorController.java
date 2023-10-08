package com.mnf.javaapigenerator.controller;

import com.mnf.compos.ABaseController;
import com.mnf.compos.dto.ResponseDto;
import com.mnf.javaapigenerator.dto.GeneratedApiResponseDto;
import com.mnf.javaapigenerator.dto.RequestDto;
import com.mnf.javaapigenerator.service.IApiGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/api-generator")
public class ApiGeneratorController extends ABaseController {
    @Autowired
    IApiGeneratorService apiGeneratorService;

    @PostMapping()
    public ResponseEntity<ResponseDto<GeneratedApiResponseDto>> generate(@RequestBody RequestDto requestDto){
        return createResponse(apiGeneratorService.generate(requestDto));
    }
}
