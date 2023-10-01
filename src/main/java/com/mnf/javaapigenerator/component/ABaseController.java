package com.mnf.javaapigenerator.component;

import com.mnf.javaapigenerator.component.dto.ResponseDto;
import com.mnf.javaapigenerator.component.dto.ResponseStatusOnlyDto;
import com.mnf.javaapigenerator.enumeration.ResponseDtoStatusEnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

public abstract class ABaseController extends ResponseEntityExceptionHandler {
    public <T> ResponseEntity<ResponseDto<T>> createResponse(ResponseDto<T> serviceResponse){
        HttpStatus status;

        if(serviceResponse.getStatus() == ResponseDtoStatusEnum.SUCCESS){
            if(serviceResponse.getMessage() == null){
                serviceResponse.setMessage(HttpStatus.OK.getReasonPhrase());
            }

            status = HttpStatus.OK;
        }else{
            status = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(serviceResponse, status);
    }

    public ResponseEntity<ResponseStatusOnlyDto> createResponse(ResponseStatusOnlyDto serviceResponse){
        HttpStatus status;

        if(serviceResponse.getStatus() == ResponseDtoStatusEnum.SUCCESS){
            if(serviceResponse.getMessage() == null){
                serviceResponse.setMessage(HttpStatus.OK.getReasonPhrase());
            }

            status = HttpStatus.OK;
        }else{
            status = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(serviceResponse, status);
    }
}
