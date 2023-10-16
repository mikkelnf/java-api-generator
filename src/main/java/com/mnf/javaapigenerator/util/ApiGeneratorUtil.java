package com.mnf.javaapigenerator.util;

import com.mnf.javaapigenerator.dto.FieldRequestDto;

import java.io.*;
import java.util.*;

public class ApiGeneratorUtil {
    private static String generateControllerContent(String basePackage, String apiName, String className, String additionalGetOne, String additionalGetOneDataType){
        String apiNameUpperCase = uppercaseFirstLetter(apiName);

        String controllerContentTopSection =
                """
                package %s.controller;
                
                import com.mnf.compos.ABaseController;
                import com.mnf.compos.dto.*;
                import %s.dto.%sRequestDto;
                import %s.dto.%sResponseDto;
                import %s.service.I%sService;
                import org.springframework.beans.factory.annotation.Autowired;
                import org.springframework.http.ResponseEntity;
                import org.springframework.web.bind.annotation.*;
                
                @RestController
                @RequestMapping("api/%s")
                public class %s extends ABaseController {
                    @Autowired
                    I%sService %sService;
                
                """.formatted(
                        basePackage, basePackage, apiNameUpperCase,
                        basePackage, apiNameUpperCase, basePackage, apiNameUpperCase, apiName,
                        className, apiNameUpperCase, apiName);

        String controllerContentFirstEndpointSection =
                """
                    @PostMapping("/add")
                    public ResponseEntity<ResponseStatusOnlyDto> add(@RequestBody %sRequestDto requestDto){
                        return createResponse(%sService.add(requestDto));
                    }
                
                    @GetMapping("/id/{id}")
                    public ResponseEntity<ResponseDto<%sResponseDto>> getOneById(@PathVariable String id){
                        return createResponse(%sService.getOneById(id));
                    }
                    
                """.formatted(apiNameUpperCase, apiName, apiNameUpperCase, apiName);

        if(!additionalGetOne.isEmpty()){
            String newGetOneTemplate =
                """
                    @GetMapping("/%s/{%s}")
                    public ResponseEntity<ResponseDto<%sResponseDto>> getOneBy%s(@PathVariable %s %s){
                        return createResponse(%sService.getOneBy%s(%s));
                    }
                    
                """.formatted(
                        additionalGetOne, additionalGetOne, apiNameUpperCase, uppercaseFirstLetter(additionalGetOne),
                        additionalGetOneDataType, additionalGetOne, apiName, uppercaseFirstLetter(additionalGetOne), additionalGetOne);

            controllerContentFirstEndpointSection = controllerContentFirstEndpointSection + newGetOneTemplate;
        }

        String controllerContentSecondEndpointSection =
                """
                    @GetMapping()
                    public ResponseEntity<ResponseDto<GetPaginationResponseDto<%sResponseDto>>>
                        getPagination(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                      @RequestParam(name = "size", defaultValue = "5") Integer size,
                                      @RequestParam(name = "sortBy", defaultValue = "createdDate") String sortBy,
                                      @RequestParam(name = "sortType", defaultValue = "desc") String sortType,
                                      @RequestParam(name = "isActive", required = false) Integer isActive)
                    {
                        %sRequestDto requestDto = new %sRequestDto();
                        requestDto.setIsActive(isActive);
                
                        return createResponse(%sService.getPagination(new GetPaginationRequestDto<>(page, size, sortBy, sortType, requestDto)));
                    }
                
                    @PatchMapping("/update")
                    public ResponseEntity<ResponseStatusOnlyDto> update(@RequestBody %sRequestDto requestDto){
                        return createResponse(%sService.update(requestDto));
                    }
                
                    @DeleteMapping("/delete")
                    public ResponseEntity<ResponseStatusOnlyDto> delete(@RequestBody %sRequestDto requestDto){
                        return createResponse(%sService.delete(requestDto));
                    }
                }
                """.formatted(
                        apiNameUpperCase, apiNameUpperCase, apiNameUpperCase, apiName,
                        apiNameUpperCase, apiName,
                        apiNameUpperCase, apiName);

        return controllerContentTopSection + controllerContentFirstEndpointSection + controllerContentSecondEndpointSection;
    }

    private static String generateEntityContent(String basePackage, String apiName, String className, List<FieldRequestDto> additionalFields){
        String entityContentTopSection =
                """
                package %s.entity;

                import %s.entity.listener.%sListener;

                import jakarta.persistence.*;
                import java.time.LocalDate;

                @Entity
                @Table(name = "%s")
                @EntityListeners(%sListener.class)
                public class %s {
                """.formatted(basePackage, basePackage, className, apiName.toUpperCase(), className, className);

        String entityColumns =
                """
                    @Id
                    @Column(name = "id")
                    private String id;

                    @Column(name = "is_active")
                    private Integer isActive;

                    @Column(name = "created_date")
                    private LocalDate createdDate;

                    @Column(name = "updated_date")
                    private LocalDate updatedDate;
                """;

        String entityGetterSetter =
                """
                    
                    public String getId() {
                        return id;
                    }
        
                    public void setId(String id) {
                        this.id = id;
                    }
        
                    public Integer getIsActive() {
                        return isActive;
                    }
        
                    public void setIsActive(Integer isActive) {
                        this.isActive = isActive;
                    }
        
                    public LocalDate getCreatedDate() {
                        return createdDate;
                    }
        
                    public void setCreatedDate(LocalDate createdDate) {
                        this.createdDate = createdDate;
                    }
        
                    public LocalDate getUpdatedDate() {
                        return updatedDate;
                    }
        
                    public void setUpdatedDate(LocalDate updatedDate) {
                        this.updatedDate = updatedDate;
                    }
                }
                """;

        for(FieldRequestDto field : additionalFields){
            String fieldName = field.getFieldName();
            String fieldDataType = field.getDataType();
            String regex = "([a-z])([A-Z]+)";
            String replacement = "$1_$2";
            String fieldNameUnderscore = fieldName.replaceAll(regex, replacement).toLowerCase();
            String newColumnTemplate =
                    """
                        
                        @Column(name = "%s")
                        private %s %s;
                    """.formatted(fieldNameUnderscore, fieldDataType, fieldName);
            String newGetterSetterTemplate =
                    """
                        
                        public %s get%s() {
                            return %s;
                        }

                        public void set%s(%s %s) {
                            this.%s = %s;
                        }
                    """.formatted(fieldDataType, uppercaseFirstLetter(fieldName), fieldName, uppercaseFirstLetter(fieldName), fieldDataType, fieldName, fieldName, fieldName);

            entityColumns = entityColumns + newColumnTemplate;
            entityGetterSetter = newGetterSetterTemplate + entityGetterSetter;
        }

        return entityContentTopSection.concat(entityColumns).concat(entityGetterSetter);
    }

    private static String generateEntityListenerContent(String basePackage, String className, String entityName){
        return
                """
                package %s.entity.listener;
                                
                import %s.entity.%s;
                                
                import jakarta.persistence.*;
                import java.time.LocalDate;
                import java.util.UUID;
                                
                public class %s {
                    @PrePersist
                    public void onPrePersist(%s entity){
                        if(entity.getId() == null) entity.setId(UUID.randomUUID().toString());
                                
                        if(entity.getCreatedDate() == null) entity.setCreatedDate(LocalDate.now());
                        
                        if(entity.getIsActive() == null) entity.setIsActive(1);
                    }
                                
                    @PreUpdate
                    public void onPreUpdate(%s entity){
                        entity.setUpdatedDate(LocalDate.now());
                    }
                }
                """.formatted(basePackage, basePackage, entityName, className, entityName, entityName);
    }

    private static String generateDtoContent(String basePackage, String apiName, String fileName, List<FieldRequestDto> additionalFields, String additionalGetOne, String additionalGetOneDataType){
        if(fileName.contains("RequestDto")){
            String requestDtoContent =
                    """
                    package %s.dto;
                    
                    public class %sRequestDto {
                        private String id;
                        private Integer isActive;
                    """.formatted(basePackage, uppercaseFirstLetter(apiName));

            requestDtoContent = requestDtoContent + generateFieldTemplate(additionalFields);

            if(!additionalGetOne.isEmpty()){
                String constructorTemplate =
                        """
                            
                            public %sRequestDto() {
                            }
                        
                            public %sRequestDto(%s %s) {
                                this.%s = %s;
                            }
                        """.formatted(
                                uppercaseFirstLetter(apiName), uppercaseFirstLetter(apiName), additionalGetOneDataType,
                                additionalGetOne, additionalGetOne, additionalGetOne);

                requestDtoContent = requestDtoContent + constructorTemplate;
            }

            String requestDtoGetterSetterContent =
                    """
                        
                        public String getId() {
                            return id;
                        }
                    
                        public void setId(String id) {
                            this.id = id;
                        }
                    
                        public Integer getIsActive() {
                            return isActive;
                        }
                    
                        public void setIsActive(Integer isActive) {
                            this.isActive = isActive;
                        }
                    """;

            requestDtoContent = requestDtoContent + requestDtoGetterSetterContent + generateGetterSetterTemplate(additionalFields);

            return closeWithBracket(requestDtoContent);
        }
        else{
            String responseDtoContent =
                    """
                    package %s.dto;
                    
                    import java.time.LocalDate;
                    
                    public class %sResponseDto {
                        private String id;
                        private LocalDate createdDate;
                        private LocalDate updatedDate;
                        private Integer isActive;
                    """.formatted(basePackage, uppercaseFirstLetter(apiName));

            String responseDtoGetterSetterContent =
                    """
                        
                        public String getId() {
                            return id;
                        }
                    
                        public void setId(String id) {
                            this.id = id;
                        }
                    
                        public LocalDate getCreatedDate() {
                            return createdDate;
                        }
                    
                        public void setCreatedDate(LocalDate createdDate) {
                            this.createdDate = createdDate;
                        }
                    
                        public LocalDate getUpdatedDate() {
                            return updatedDate;
                        }
                    
                        public void setUpdatedDate(LocalDate updatedDate) {
                            this.updatedDate = updatedDate;
                        }
                    
                        public Integer getIsActive() {
                            return isActive;
                        }
                    
                        public void setIsActive(Integer isActive) {
                            this.isActive = isActive;
                        }
                    """;

            responseDtoContent =
                    responseDtoContent + generateFieldTemplate(additionalFields) +
                            responseDtoGetterSetterContent + generateGetterSetterTemplate(additionalFields);

            return closeWithBracket(responseDtoContent);
        }
    }

    private static String generateInterfaceServiceContent(String basePackage, String apiName, String additionalGetOne, String additionalGetOneDataType){
        String interfaceContent1 =
                """
                package %s.service;
                
                import com.mnf.compos.dto.*;
                import %s.dto.%sRequestDto;
                import %s.dto.%sResponseDto;
                
                public interface I%sService {
                    ResponseStatusOnlyDto add(%sRequestDto requestDto);
                    ResponseDto<PostResponseDto> getOneById(String id);
                """.formatted(
                        basePackage, basePackage, uppercaseFirstLetter(apiName), basePackage, uppercaseFirstLetter(apiName),
                        uppercaseFirstLetter(apiName), uppercaseFirstLetter(apiName), uppercaseFirstLetter(apiName));

        if(!additionalGetOne.isEmpty()){
            String contentTemplate =
                """
                    ResponseDto<PostResponseDto> getOneBy%s(%s %s);
                """.formatted(uppercaseFirstLetter(additionalGetOne), additionalGetOneDataType, additionalGetOne);

            interfaceContent1 = interfaceContent1 + contentTemplate;
        }

        String interfaceContent2 =
                """
                    ResponseDto<GetPaginationResponseDto<%sResponseDto>> getPagination(GetPaginationRequestDto<%sRequestDto> requestDto);
                    ResponseStatusOnlyDto update(%sRequestDto requestDto);
                    ResponseStatusOnlyDto delete(%sRequestDto requestDto);
                }
                """.formatted(uppercaseFirstLetter(apiName), uppercaseFirstLetter(apiName), uppercaseFirstLetter(apiName), uppercaseFirstLetter(apiName));

        return interfaceContent1 + interfaceContent2;
    }

    private static String generateServiceImplContent(String basePackage, String apiName, String additionalGetOne, String additionalGetOneDataType, String uniqueField, List<String> configuredFields){
        String upperApiName = uppercaseFirstLetter(apiName);
        String upperAdditionalGetOne = !additionalGetOne.isEmpty() ? uppercaseFirstLetter(additionalGetOne) : "";
        String upperUniqueField = !uniqueField.isEmpty() ? uppercaseFirstLetter(uniqueField) : "";
        String content =
                """
                package %s.service;
                
                import com.mnf.compos.*;
                import com.mnf.compos.dto.*;
                import com.mnf.compos.enumeration.ResponseDtoStatusEnum;
                import %s.dto.*;
                import %s.entity.%sEntity;
                import %s.exception.%sException;
                import %s.repository.I%sRepository;
                import org.modelmapper.ModelMapper;
                import org.springframework.beans.factory.annotation.Autowired;
                import org.springframework.stereotype.Service;
                import jakarta.transaction.Transactional;
                
                import java.util.*;
                import java.util.stream.Collectors;
                
                @Service
                @Transactional
                public class %sServiceImpl extends ABaseService<%sEntity> implements I%sService {
                    @Autowired
                    I%sRepository %sRepository;
                    @Autowired
                    ModelMapper modelMapper;
                """.formatted(
                        basePackage, basePackage, basePackage, upperApiName, basePackage,
                        upperApiName, basePackage, upperApiName, upperApiName,
                        upperApiName, upperApiName, upperApiName, apiName);

        String setFieldsDataContent =
                """
                ///add data
                """;

        for(String field : configuredFields){
            String newSetFieldsDataTemplate =
                    """
                                entity.set%s(requestDto.get%s());
                    """.formatted(uppercaseFirstLetter(field), uppercaseFirstLetter(field));

            setFieldsDataContent = setFieldsDataContent + newSetFieldsDataTemplate;
        }

        String setEntityContent = !uniqueField.isEmpty() ?
                """
                Optional<%sEntity> optionalExistingEntity = findOneBy%sQuery(requestDto).getOne();
                        
                        if(optionalExistingEntity.isPresent()){
                            responseStatusOnlyDto.setStatus(ResponseDtoStatusEnum.ERROR);
                            responseStatusOnlyDto.setMessage(PostException.DATA_EXISTED);
                        }else{
                            %sEntity entity = new %sEntity();
                
                            %s
                            %sRepository.save(entity);
                
                            responseStatusOnlyDto.setStatus(ResponseDtoStatusEnum.SUCCESS);
                        }
                """.formatted(upperApiName, uppercaseFirstLetter(uniqueField), upperApiName, upperApiName, setFieldsDataContent, apiName) :
                """
                %sEntity entity = new %sEntity();
            
                        %s
                        %sRepository.save(entity);
            
                        responseStatusOnlyDto.setStatus(ResponseDtoStatusEnum.SUCCESS);
                """.formatted(upperApiName, upperApiName, setFieldsDataContent, apiName);

        String addServiceContent =
                """
                    
                    @Override
                    public ResponseStatusOnlyDto add(%sRequestDto requestDto) {
                        ResponseStatusOnlyDto responseStatusOnlyDto = new ResponseStatusOnlyDto();
                
                        %s
                        return responseStatusOnlyDto;
                    }
                """.formatted(upperApiName, setEntityContent);

        String getOneContent =
                """
                    @Override
                    public ResponseDto<%sResponseDto> getOneById(String id) {
                        Optional<%sEntity> optionalExistingEntity = %sRepository.findById(id);
                
                        ResponseDto<%sResponseDto> responseDto = new ResponseDto<>();
                
                        if(optionalExistingEntity.isPresent()){
                            responseDto.setStatus(ResponseDtoStatusEnum.SUCCESS);
                            responseDto.setContent(modelMapper.map(optionalExistingEntity.get(), PostResponseDto.class));
                        }else{
                            responseDto.setStatus(ResponseDtoStatusEnum.ERROR);
                            responseDto.setMessage(PostException.NOT_FOUND);
                        }
                
                        return responseDto;
                    }
                """.formatted(upperApiName, upperApiName, apiName, upperApiName);

        if(!additionalGetOne.isEmpty()){
            String getOneByAdditionalServiceContent =
                    """
                        @Override
                        public ResponseDto<%sResponseDto> getOneBy%s(%s %s) {
                            Optional<%sEntity> optionalExistingEntity = findOneBy%sQuery(new %sRequestDto(%s)).getOne();
                    
                            ResponseDto<%sResponseDto> responseDto = new ResponseDto<>();
                    
                            if(optionalExistingEntity.isPresent()){
                                responseDto.setStatus(ResponseDtoStatusEnum.SUCCESS);
                                responseDto.setContent(modelMapper.map(optionalExistingEntity.get(), %sResponseDto.class));
                            }else{
                                responseDto.setStatus(ResponseDtoStatusEnum.ERROR);
                                responseDto.setMessage(%sException.NOT_FOUND);
                            }
                    
                            return responseDto;
                        }
                    """.formatted(
                            upperApiName, upperAdditionalGetOne, additionalGetOneDataType, additionalGetOne, upperApiName, upperAdditionalGetOne,
                            upperApiName, additionalGetOne, upperApiName, upperApiName, upperApiName);

            getOneContent = getOneContent + getOneByAdditionalServiceContent;
        }

        String getPaginationServiceContent =
                """
                    @Override
                    public ResponseDto<GetPaginationResponseDto<%sResponseDto>> getPagination(GetPaginationRequestDto<%sRequestDto> getPaginationRequestDto) {
                        GetPaginationResponseDto<%sEntity> queryResultPagination =
                            findAllPaginationQuery(getPaginationRequestDto.getDto()).getAllWithPagination(getPaginationRequestDto);
                                            
                        List<%sResponseDto> mappedResponseList = queryResultPagination.getResults().stream()
                                .map(entity -> {
                                    return modelMapper.map(entity, %sResponseDto.class);
                                }).collect(Collectors.toList());
                
                        GetPaginationResponseDto<%sResponseDto> getPaginationResponseDto =
                                new GetPaginationResponseDto<>(mappedResponseList, queryResultPagination.getPaginationInfo());
                
                        return new ResponseDto<>(ResponseDtoStatusEnum.SUCCESS, null, getPaginationResponseDto);
                    }
                """.formatted(upperApiName, upperApiName, upperApiName, upperApiName, upperApiName, upperApiName);

        String setFieldsUpdateEntityContent =
                """
                ///update data
                """;

        for(String field : configuredFields){
            String columnsForUpdateContentTemplate =
                    """
                                    entity.set%s(requestDto.get%s());
                    """.formatted(uppercaseFirstLetter(field), uppercaseFirstLetter(field));

            setFieldsUpdateEntityContent = setFieldsUpdateEntityContent + columnsForUpdateContentTemplate;
        }

        String setUpdateEntityContent = !uniqueField.isEmpty() ?
                """
                Optional<%sEntity> optionalExistingEntity2 = findOneBy%sQuery(requestDto).getOne();
                            
                            if(optionalExistingEntity2.isPresent()){
                                responseStatusOnlyDto.setStatus(ResponseDtoStatusEnum.ERROR);
                                responseStatusOnlyDto.setMessage(PostException.DATA_EXISTED);
                            }else{
                                %s
                                %sRepository.save(entity);
                    
                                responseStatusOnlyDto.setStatus(ResponseDtoStatusEnum.SUCCESS);
                            }
                """.formatted(upperApiName, uppercaseFirstLetter(uniqueField), setFieldsUpdateEntityContent, apiName) :
                """
                            %s
                            %sRepository.save(entity);
                
                            responseStatusOnlyDto.setStatus(ResponseDtoStatusEnum.SUCCESS);
                """.formatted(setFieldsUpdateEntityContent, apiName);

        String updateServiceContent =
                """
                    @Override
                    public ResponseStatusOnlyDto update(%sRequestDto requestDto) {
                        Optional<%sEntity> optionalExistingEntity = %sRepository.findById(requestDto.getId());
                
                        ResponseStatusOnlyDto responseStatusOnlyDto = new ResponseStatusOnlyDto();
                
                        if(optionalExistingEntity.isPresent()){
                            %sEntity entity = optionalExistingEntity.get();
                
                            %s
                        }else{
                            responseStatusOnlyDto.setStatus(ResponseDtoStatusEnum.ERROR);
                            responseStatusOnlyDto.setMessage(PostException.NOT_FOUND);
                        }
                
                        return responseStatusOnlyDto;
                    }
                """.formatted(upperApiName, upperApiName, apiName, upperApiName, setUpdateEntityContent);

        String deleteServiceContent =
                """
                    @Override
                    public ResponseStatusOnlyDto delete(%sRequestDto requestDto) {
                        Optional<%sEntity> optionalExistingEntity = %sRepository.findById(requestDto.getId());
                
                        ResponseStatusOnlyDto responseStatusOnlyDto = new ResponseStatusOnlyDto();
                
                        if(optionalExistingEntity.isPresent()){
                            %sEntity entity = optionalExistingEntity.get();
                            
                            entity.setIsActive(0);
                
                            postRepository.save(entity);
                
                            responseStatusOnlyDto.setStatus(ResponseDtoStatusEnum.SUCCESS);
                        }else{
                            responseStatusOnlyDto.setStatus(ResponseDtoStatusEnum.ERROR);
                            responseStatusOnlyDto.setMessage(PostException.NOT_FOUND);
                        }
                
                        return responseStatusOnlyDto;
                    }
                """.formatted(upperApiName, upperApiName, apiName, upperApiName);

        String queryServiceContent =
                """
                    protected Class<%sEntity> get%sEntityClass(){
                        return %sEntity.class;
                    }

                    public CustomQueryBuilder<%sEntity> findAllPaginationQuery(%sRequestDto requestDto) {
                        return getQueryBuilder()
                                .buildQuery(get%sEntityClass())
                                .start()
                                    .equals("isActive", requestDto.getIsActive())
                                .end();
                    }
                """.formatted(
                        upperApiName, upperApiName, upperApiName, upperApiName, upperApiName, upperApiName);

        if(!additionalGetOne.isEmpty() && !uniqueField.isEmpty() && additionalGetOne.equals(uniqueField)){
            String additionalQueryServiceContent =
                    """
                        
                        public CustomQueryBuilder<%sEntity> findOneBy%sQuery(%sRequestDto requestDto) {
                            return getQueryBuilder()
                                    .buildQuery(get%sEntityClass())
                                    .start()
                                        .equals("%s", requestDto.get%s())
                                    .end();
                        }
                    """.formatted(
                            upperApiName, upperAdditionalGetOne, upperApiName,
                            upperApiName, uniqueField, upperAdditionalGetOne);

            queryServiceContent = queryServiceContent + additionalQueryServiceContent;
        }else{
            if(!additionalGetOne.isEmpty()){
                String additionalGetOneQueryServiceContent =
                        """
                            
                            public CustomQueryBuilder<%sEntity> findOneBy%sQuery(%sRequestDto requestDto) {
                                return getQueryBuilder()
                                        .buildQuery(get%sEntityClass())
                                        .start()
                                            .equals("%s", requestDto.get%s())
                                        .end();
                            }
                        """.formatted(
                                upperApiName, upperAdditionalGetOne, upperApiName,
                                upperApiName, additionalGetOne, upperAdditionalGetOne);

                queryServiceContent = queryServiceContent + additionalGetOneQueryServiceContent;
            }
            if(!uniqueField.isEmpty()){
                String additionalUniqueQueryServiceContent =
                        """
                            
                            public CustomQueryBuilder<%sEntity> findOneBy%sQuery(%sRequestDto requestDto) {
                                return getQueryBuilder()
                                        .buildQuery(get%sEntityClass())
                                        .start()
                                            .equals("%s", requestDto.get%s())
                                        .end();
                            }
                        """.formatted(
                                upperApiName, upperUniqueField, upperApiName,
                                upperApiName, uniqueField, upperUniqueField);

                queryServiceContent = queryServiceContent + additionalUniqueQueryServiceContent;
            }
        }

        return closeWithBracket(
                content + addServiceContent + getOneContent + getPaginationServiceContent +
                        updateServiceContent + deleteServiceContent + queryServiceContent);
    }

    private static String generateRepositoryContent(String basePackage, String apiName){
        return
                """
                package %s.repository;
                
                import %s.entity.%sEntity;
                import org.springframework.data.jpa.repository.JpaRepository;
                
                public interface I%sRepository extends JpaRepository<%sEntity, String> {
                }
                """.formatted(basePackage, basePackage, uppercaseFirstLetter(apiName), uppercaseFirstLetter(apiName), uppercaseFirstLetter(apiName));
    }

    private static String generateExceptionContent(String basePackage, String apiName){
        return
                """
                package %s.exception;
                
                public class %sException extends Exception {
                    public static final String NOT_FOUND = "Entity not found";
                    public static final String DATA_EXISTED = "Data existed";
                    public %sException(String message) {
                        super(message);
                    }
                }
                """.formatted(basePackage, uppercaseFirstLetter(apiName), uppercaseFirstLetter(apiName));
    }

    private static String generateConfigContent(String basePackage, String apiName){
        return
                """
                package %s.config;
                
                import %s.service.*;
                import org.modelmapper.ModelMapper;
                import org.springframework.context.annotation.Bean;
                import org.springframework.context.annotation.Configuration;
                
                @Configuration
                public class %sConfig {
                    @Bean
                    public I%sService %sService(){
                        return new %sServiceImpl();
                    }
                
                    @Bean
                    public ModelMapper modelMapper(){
                        return new ModelMapper();
                    }
                }
                """.formatted(basePackage, basePackage, uppercaseFirstLetter(apiName), uppercaseFirstLetter(apiName), apiName, uppercaseFirstLetter(apiName));
    }

    public static void generateFile(String apiNameInput, String basePackagePath,
                                     String basePackageInput, String fileType, List<FieldRequestDto> additionalFields,
                                     FieldRequestDto additionalGetOne, String changeUniqueValidationName,
                                     List<String> columnsForUpdate) {
        String fileName = "";
        String className = "";
        String fileDirectory = "";
        String filePath = "";
        String fileContent = "";
        List<String[]> fileList = new ArrayList<>();
        boolean multipleFiles = false;

        switch (fileType){
            case("controller"):
                fileName = uppercaseFirstLetter(apiNameInput).concat("Controller.java");
                className = uppercaseFirstLetter(apiNameInput).concat("Controller");
                fileDirectory = String.format("src/main/java/com/mnf/javaapigenerator/result/%s/controller", basePackagePath);
                filePath = String.format("%s/%s", fileDirectory, fileName);
                fileContent = generateControllerContent(basePackageInput, apiNameInput, className, additionalGetOne.getFieldName(), additionalGetOne.getDataType());
                break;
            case("entity"):
                fileName = uppercaseFirstLetter(apiNameInput).concat("Entity.java");
                className = uppercaseFirstLetter(apiNameInput).concat("Entity");
                fileDirectory = String.format("src/main/java/com/mnf/javaapigenerator/result/%s/entity", basePackagePath);
                filePath = String.format("%s/%s", fileDirectory, fileName);
                fileContent = generateEntityContent(basePackageInput, apiNameInput, className, additionalFields);
                break;
            case("entityListener"):
                String entityName = uppercaseFirstLetter(apiNameInput).concat("Entity");
                fileName = uppercaseFirstLetter(apiNameInput).concat("EntityListener.java");
                className = uppercaseFirstLetter(apiNameInput).concat("EntityListener");
                fileDirectory = String.format("src/main/java/com/mnf/javaapigenerator/result/%s/entity/listener", basePackagePath);
                filePath = String.format("%s/%s", fileDirectory, fileName);
                fileContent = generateEntityListenerContent(basePackageInput, className, entityName);
                break;
            case("dto"):
                fileDirectory = String.format("src/main/java/com/mnf/javaapigenerator/result/%s/dto", basePackagePath);
                String requestFileName = uppercaseFirstLetter(apiNameInput).concat("RequestDto.java");
                String responseFileName = uppercaseFirstLetter(apiNameInput).concat("ResponseDto.java");
                String requestFilePath = String.format("%s/%s", fileDirectory, requestFileName);
                String responseFilePath = String.format("%s/%s", fileDirectory, responseFileName);
                String requestFileContent = generateDtoContent(basePackageInput, apiNameInput, requestFileName, additionalFields, additionalGetOne.getFieldName(), additionalGetOne.getDataType());
                String responseFileContent = generateDtoContent(basePackageInput, apiNameInput, responseFileName, additionalFields, additionalGetOne.getFieldName(), additionalGetOne.getDataType());
                String[] requestFile = new String[] {requestFilePath, requestFileContent};
                String[] responseFile = new String[] {responseFilePath, responseFileContent};
                multipleFiles = true;
                fileList.add(requestFile);
                fileList.add(responseFile);
                break;
            case("repository"):
                fileName = "I".concat(uppercaseFirstLetter(apiNameInput).concat("Repository.java"));
                fileDirectory = String.format("src/main/java/com/mnf/javaapigenerator/result/%s/repository", basePackagePath);
                filePath = String.format("%s/%s", fileDirectory, fileName);
                fileContent = generateRepositoryContent(basePackageInput, apiNameInput);
                break;
            case("interfaceService"):
                fileName = "I".concat(uppercaseFirstLetter(apiNameInput).concat("Service.java"));
                fileDirectory = String.format("src/main/java/com/mnf/javaapigenerator/result/%s/service", basePackagePath);
                filePath = String.format("%s/%s", fileDirectory, fileName);
                fileContent = generateInterfaceServiceContent(basePackageInput, apiNameInput, additionalGetOne.getFieldName(), additionalGetOne.getDataType());
                break;
            case("serviceImpl"):
                fileName = uppercaseFirstLetter(apiNameInput).concat("ServiceImpl.java");
                fileDirectory = String.format("src/main/java/com/mnf/javaapigenerator/result/%s/service", basePackagePath);
                filePath = String.format("%s/%s", fileDirectory, fileName);
                fileContent = generateServiceImplContent(basePackageInput, apiNameInput, additionalGetOne.getFieldName(), additionalGetOne.getDataType(), changeUniqueValidationName, columnsForUpdate);
                break;
            case("exception"):
                fileName = uppercaseFirstLetter(apiNameInput).concat("Exception.java");
                fileDirectory = String.format("src/main/java/com/mnf/javaapigenerator/result/%s/exception", basePackagePath);
                filePath = String.format("%s/%s", fileDirectory, fileName);
                fileContent = generateExceptionContent(basePackageInput, apiNameInput);
                break;
            case("config"):
                fileName = uppercaseFirstLetter(apiNameInput).concat("Config.java");
                fileDirectory = String.format("src/main/java/com/mnf/javaapigenerator/result/%s/config", basePackagePath);
                filePath = String.format("%s/%s", fileDirectory, fileName);
                fileContent = generateConfigContent(basePackageInput, apiNameInput);
                break;
        }

        try {
            if(multipleFiles){
                for(int i=0; i<fileList.size(); i++){
                    File dir = new File(fileDirectory);
                    dir.mkdirs();
                    FileWriter fileWriter = new FileWriter(fileList.get(i)[0]);
                    PrintWriter printWriter = new PrintWriter(fileWriter);

                    printWriter.print(fileList.get(i)[1]);
                    fileWriter.close();
                    printWriter.close();
                }
            }else{
                File dir = new File(fileDirectory);
                dir.mkdirs();
                FileWriter fileWriter = new FileWriter(filePath);
                PrintWriter printWriter = new PrintWriter(fileWriter);

                printWriter.print(fileContent);
                printWriter.close();
                fileWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String generateFieldTemplate(List<FieldRequestDto> additionalFields){
        String result = "";

        for(FieldRequestDto field : additionalFields){
            String fieldName = field.getFieldName();
            String fieldDataType = field.getDataType();
            String newColumnTemplate =
                    """
                        private %s %s;
                    """.formatted(fieldDataType, fieldName);

            result = result + newColumnTemplate;
        }

        return result;
    }

    private static String generateGetterSetterTemplate(List<FieldRequestDto> additionalFields){
        String result = "";

        for(FieldRequestDto field : additionalFields){
            String fieldName = field.getFieldName();
            String fieldDataType = field.getDataType();
            String newGetterSetterTemplate =
                    """
                        
                        public %s get%s() {
                            return %s;
                        }

                        public void set%s(%s %s) {
                            this.%s = %s;
                        }
                    """.formatted(
                            fieldDataType, uppercaseFirstLetter(fieldName), fieldName, uppercaseFirstLetter(fieldName),
                            fieldDataType, fieldName, fieldName, fieldName);

            result = result + newGetterSetterTemplate;
        }

        return result;
    }

    private static String closeWithBracket(String content){
        String endOfTemplate =
                """
                }
                """;

        return content + endOfTemplate;
    }

    public static String uppercaseFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        char firstChar = Character.toUpperCase(input.charAt(0));

        return firstChar + input.substring(1);
    }
}
