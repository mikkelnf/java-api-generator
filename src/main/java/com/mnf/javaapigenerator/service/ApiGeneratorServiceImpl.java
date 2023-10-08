package com.mnf.javaapigenerator.service;

import com.mnf.compos.dto.ResponseDto;
import com.mnf.compos.enumeration.ResponseDtoStatusEnum;
import com.mnf.javaapigenerator.util.*;
import com.mnf.javaapigenerator.dto.GeneratedApiResponseDto;
import com.mnf.javaapigenerator.dto.RequestDto;

public class ApiGeneratorServiceImpl implements IApiGeneratorService {
    @Override
    public ResponseDto<GeneratedApiResponseDto> generate(RequestDto requestDto) {
        ResponseDto<GeneratedApiResponseDto> response = new ResponseDto<>();

        String apiName = requestDto.getApiName().toLowerCase();
        String basePackage = requestDto.getBasePackage().toLowerCase();
        String basePackagePath = basePackage.replace(".", "/");

//      generate controller
        ApiGeneratorUtil.generateFile(apiName, basePackagePath, basePackage, "controller", requestDto.getAdditionalFields(), requestDto.getAdditionalGetOne(), requestDto.getAdditionalUniqueField(), requestDto.getConfiguredFields());
//      generate entity
        ApiGeneratorUtil.generateFile(apiName, basePackagePath, basePackage, "entity", requestDto.getAdditionalFields(), requestDto.getAdditionalGetOne(), requestDto.getAdditionalUniqueField(), requestDto.getConfiguredFields());
//      generate entity listener
        ApiGeneratorUtil.generateFile(apiName, basePackagePath, basePackage, "entityListener", requestDto.getAdditionalFields(), requestDto.getAdditionalGetOne(), requestDto.getAdditionalUniqueField(), requestDto.getConfiguredFields());
//      generate dto
        ApiGeneratorUtil.generateFile(apiName, basePackagePath, basePackage, "dto", requestDto.getAdditionalFields(), requestDto.getAdditionalGetOne(), requestDto.getAdditionalUniqueField(), requestDto.getConfiguredFields());
        ApiGeneratorUtil.generateFile(apiName, basePackagePath, basePackage, "dto", requestDto.getAdditionalFields(), requestDto.getAdditionalGetOne(), requestDto.getAdditionalUniqueField(), requestDto.getConfiguredFields());
//      generate repository
        ApiGeneratorUtil.generateFile(apiName, basePackagePath, basePackage, "repository", requestDto.getAdditionalFields(), requestDto.getAdditionalGetOne(), requestDto.getAdditionalUniqueField(), requestDto.getConfiguredFields());
//      generate interface service
        ApiGeneratorUtil.generateFile(apiName, basePackagePath, basePackage, "interfaceService", requestDto.getAdditionalFields(), requestDto.getAdditionalGetOne(), requestDto.getAdditionalUniqueField(), requestDto.getConfiguredFields());
//      generate service impl
        ApiGeneratorUtil.generateFile(apiName, basePackagePath, basePackage, "serviceImpl", requestDto.getAdditionalFields(), requestDto.getAdditionalGetOne(), requestDto.getAdditionalUniqueField(), requestDto.getConfiguredFields());
//      generate exception
        ApiGeneratorUtil.generateFile(apiName, basePackagePath, basePackage, "exception", requestDto.getAdditionalFields(), requestDto.getAdditionalGetOne(), requestDto.getAdditionalUniqueField(), requestDto.getConfiguredFields());
//      generate config
        ApiGeneratorUtil.generateFile(apiName, basePackagePath, basePackage, "config", requestDto.getAdditionalFields(), requestDto.getAdditionalGetOne(), requestDto.getAdditionalUniqueField(), requestDto.getConfiguredFields());

        String sourceZipDir = "src/main/java/com/mnf/javaapigenerator/result/";
        String zipDestinationDir = "src/main/java/com/mnf/javaapigenerator/zip/";
        String zipName = ApiGeneratorUtil.uppercaseFirstLetter(apiName).concat("Api.zip");

        ZipGeneratorUtil.zipDirectory(sourceZipDir, zipDestinationDir, zipName);

        DeleteDirectoryUtil.deleteDirectory(sourceZipDir);

        response.setStatus(ResponseDtoStatusEnum.SUCCESS);
        response.setContent(new GeneratedApiResponseDto(zipName, FileEncoderUtil.encodeFileToBase64(zipDestinationDir.concat(zipName))));

        DeleteDirectoryUtil.deleteDirectory(zipDestinationDir);

        return response;
    }
}
