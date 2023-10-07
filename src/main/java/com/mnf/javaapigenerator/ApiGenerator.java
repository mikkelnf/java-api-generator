package com.mnf.javaapigenerator;

import com.mnf.javaapigenerator.dto.RequestDto;

import java.io.*;
import java.util.*;

public class ApiGenerator {
    public static String generate(RequestDto requestDto){
        return null;
    }
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);

        System.out.println("Api name (e.g., customer, product, etc) :");
        String apiNameInput = scanner.nextLine();
        String apiName = apiNameInput.toLowerCase();

        System.out.println("Base package name (e.g., com.example.demo) :");
        String basePackageInput = scanner.nextLine();
        String basePackage = basePackageInput.toLowerCase();
        String basePackagePath = basePackage.replace(".", "/");

        String[] columns = {""};
        String anotherGetOneName = "";

        List<String> columnsForUpdate = new ArrayList<>();

        System.out.println("Input additional column name and dataType (e.g., title:String, stock:int) : ");
        String columnsInput = scanner.nextLine();
        columns = columnsInput.replace(" ", "").split(",");

        System.out.println("Add another getOne endpoint from the additional column? (Y/y)");
        String addAnotherGetOneInput = scanner.nextLine();
        if(addAnotherGetOneInput.matches("[Yy]")){
            System.out.println("Choose one from the columns (1-%s) :".formatted(columns.length));
            for(int i=0; i< columns.length; i++){
                System.out.println(i+1 + ". " + columns[i].replaceAll("[:]\\w+", ""));
            }
            String anotherGetOneNameInput = scanner.nextLine();
            anotherGetOneName = columns[Integer.valueOf(anotherGetOneNameInput) - 1].replaceAll("[:]\\w+", "");
        }

        System.out.println("Choose unique validation for add data endpoint (1-%s) :".formatted(columns.length));
        for(int i=0; i< columns.length; i++){
            System.out.println(i+1 + ". " + columns[i].replaceAll("[:]\\w+", ""));
        }
        String changeUniqueValidationNameInput = scanner.nextLine();
        String changeUniqueValidationName = columns[Integer.valueOf(changeUniqueValidationNameInput) - 1].replaceAll("[:]\\w+", "");

        System.out.println("Choose column to be included in add and update endpoint(1-%s) :".formatted(columns.length));
        System.out.println("If choose multiple (e.g. 1,2,3)");
        for(int i=0; i< columns.length; i++){
            System.out.println(i+1 + ". " + columns[i].replaceAll("[:]\\w+", ""));
        }
        String columnsForUpdateInput = scanner.nextLine();
        String[] columnsForUpdateInputArr = columnsForUpdateInput.split(",");
        for(int i=0; i<columnsForUpdateInputArr.length; i++){
            columnsForUpdate.add(columns[Integer.valueOf(columnsForUpdateInputArr[i])-1].replaceAll("[:]\\w+", ""));
        }

        scanner.close();

//      generate controller
        generateFile(apiName, basePackagePath, basePackage, "controller", columns, anotherGetOneName, changeUniqueValidationName, columnsForUpdate);
//      generate entity
        generateFile(apiName, basePackagePath, basePackage, "entity", columns, anotherGetOneName, changeUniqueValidationName, columnsForUpdate);
//      generate entity listener
        generateFile(apiName, basePackagePath, basePackage, "entityListener", columns, anotherGetOneName, changeUniqueValidationName, columnsForUpdate);
//      generate dto
        generateFile(apiName, basePackagePath, basePackage, "dto", columns, anotherGetOneName, changeUniqueValidationName, columnsForUpdate);
        generateFile(apiName, basePackagePath, basePackage, "dto", columns, anotherGetOneName, changeUniqueValidationName, columnsForUpdate);
//      generate repository
        generateFile(apiName, basePackagePath, basePackage, "repository", columns, anotherGetOneName, changeUniqueValidationName, columnsForUpdate);
//      generate interface service
        generateFile(apiName, basePackagePath, basePackage, "interfaceService", columns, anotherGetOneName, changeUniqueValidationName, columnsForUpdate);
//      generate service impl
        generateFile(apiName, basePackagePath, basePackage, "serviceImpl", columns, anotherGetOneName, changeUniqueValidationName, columnsForUpdate);
//      generate exception
        generateFile(apiName, basePackagePath, basePackage, "exception", columns, anotherGetOneName, changeUniqueValidationName, columnsForUpdate);
//      generate config
        generateFile(apiName, basePackagePath, basePackage, "config", columns, anotherGetOneName, changeUniqueValidationName, columnsForUpdate);
    }

    private static String generateControllerContent(String basePackage, String apiName, String className, String addAnotherGetOneName){
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

        if(addAnotherGetOneName != null){
            String newGetOneTemplate =
                """
                    @GetMapping("/%s/{%s}")
                    public ResponseEntity<ResponseDto<%sResponseDto>> getOneBy%s(@PathVariable String %s){
                        return createResponse(%sService.getOneBySlug(%s));
                    }
                    
                """.formatted(
                        addAnotherGetOneName, addAnotherGetOneName, apiNameUpperCase, uppercaseFirstLetter(addAnotherGetOneName),
                        addAnotherGetOneName, apiName, addAnotherGetOneName);

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

    private static String generateEntityContent(String basePackage, String apiName, String className, String[] columns){
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

        for(String column : columns){
            if(!column.isEmpty() || !column.isBlank()){
                String[] splittedColumn = column.split(":");
                String columnName = splittedColumn[0];
                String columnDataType = splittedColumn[1];
                String regex = "([a-z])([A-Z]+)";
                String replacement = "$1_$2";
                String columnNameUnderscore = columnName.replaceAll(regex, replacement).toLowerCase();
                String newColumnTemplate =
                        """
                            
                            @Column(name = "%s")
                            private %s %s;
                        """.formatted(columnNameUnderscore, columnDataType, columnName);
                String newGetterSetterTemplate =
                        """
                            
                            public %s get%s() {
                                return %s;
                            }
    
                            public void set%s(%s %s) {
                                this.%s = %s;
                            }
                        """.formatted(columnDataType, uppercaseFirstLetter(columnName), columnName, uppercaseFirstLetter(columnName), columnDataType, columnName, columnName, columnName);

                entityColumns = entityColumns + newColumnTemplate;
                entityGetterSetter = newGetterSetterTemplate + entityGetterSetter;
            }
        }

        return entityContentTopSection.concat(entityColumns).concat(entityGetterSetter);
    }

    private static String generateEntityListenerContent(String basePackage, String apiName, String className, String entityName){
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

    private static String generateDtoContent(String basePackage, String apiName, String className, String fileName, String[] columns, String anotherGetOneName){
        String endOfTemplate =
                """
                
                }
                """;
        if(fileName.contains("RequestDto")){
            String requestDtoContent =
                    """
                    package %s.dto;
                    
                    import lombok.*;
                    
                    @Getter
                    @Setter
                    public class %sRequestDto {
                        private String id;
                        private Integer isActive;
                    """.formatted(basePackage, uppercaseFirstLetter(apiName));

            for(String column : columns){
                if(!column.isEmpty() || !column.isBlank()){
                    String[] splittedColumn = column.split(":");
                    String columnName = splittedColumn[0];
                    String columnDataType = splittedColumn[1];
                    String newColumnTemplate =
                            """
                                private %s %s;
                            """.formatted(columnDataType, columnName);

                    requestDtoContent = requestDtoContent + newColumnTemplate;
                }
            }

            if(anotherGetOneName != null){
                String constructorTemplate =
                        """
                            
                            public %sRequestDto() {
                            }
                        
                            public %sRequestDto(String %s) {
                                this.%s = %s;
                            }
                        """.formatted(uppercaseFirstLetter(apiName), uppercaseFirstLetter(apiName), anotherGetOneName, anotherGetOneName, anotherGetOneName);

                requestDtoContent = requestDtoContent + constructorTemplate;
            }

            return requestDtoContent + endOfTemplate;
        }
        else{
            String responseDtoContent =
                    """
                    package %s.dto;
                    
                    import lombok.*;
                    import java.time.LocalDate;
                    
                    @Getter
                    @Setter
                    public class %sResponseDto {
                        private String id;
                        private LocalDate createdDate;
                        private LocalDate updatedDate;
                        private Integer isActive;
                    """.formatted(basePackage, uppercaseFirstLetter(apiName));

            for(String column : columns){
                if(!column.isEmpty() || !column.isBlank()){
                    String[] splittedColumn = column.split(":");
                    String columnName = splittedColumn[0];
                    String columnDataType = splittedColumn[1];
                    String newColumnTemplate =
                            """
                                private %s %s;
                            """.formatted(columnDataType, columnName);

                    responseDtoContent = responseDtoContent + newColumnTemplate;
                }
            }

            return responseDtoContent + endOfTemplate;
        }
    }

    private static String generateInterfaceServiceContent(String basePackage, String apiName, String anotherGetOneName){
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

        if(anotherGetOneName != null){
            String contentTemplate =
                """
                    ResponseDto<PostResponseDto> getOneBy%s(String %s);
                """.formatted(uppercaseFirstLetter(anotherGetOneName), anotherGetOneName);

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

    private static String generateServiceImplContent(String basePackage, String apiName, String anotherGetOneName, String changeUniqueValidationName, List<String> columnsForAddAndUpdate){
        String upperApiName = uppercaseFirstLetter(apiName);
        String upperAnotherGetOneName = uppercaseFirstLetter(anotherGetOneName);
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

        String optionalUniqueContent =
                "Optional<%sEntity> optionalExistingEntity = findOneBy%sQuery(requestDto).getOne();"
                        .formatted(upperApiName, uppercaseFirstLetter(changeUniqueValidationName));

        String columnsForAddContent =
                """
                ///add data
                """;

        for(String column : columnsForAddAndUpdate){
            String columnsForUpdateContentTemplate =
                    """
                                entity.set%s(requestDto.get%s());
                    """.formatted(uppercaseFirstLetter(column), uppercaseFirstLetter(column));

            columnsForAddContent = columnsForAddContent + columnsForUpdateContentTemplate;
        }

        String addServiceContent =
                """
                    
                    @Override
                    public ResponseStatusOnlyDto add(%sRequestDto requestDto) {
                        %s
                
                        ResponseStatusOnlyDto responseStatusOnlyDto = new ResponseStatusOnlyDto();
                
                        if(optionalExistingEntity.isPresent()){
                            responseStatusOnlyDto.setStatus(ResponseDtoStatusEnum.ERROR);
                            responseStatusOnlyDto.setMessage(PostException.DATA_EXISTED);
                        }else{
                            %sEntity entity = new %sEntity();
                
                            %s
                            %sRepository.save(entity);
                
                            responseStatusOnlyDto.setStatus(ResponseDtoStatusEnum.SUCCESS);
                        }
                
                        return responseStatusOnlyDto;
                    }
                """.formatted(upperApiName, optionalUniqueContent, upperApiName, upperApiName, columnsForAddContent, apiName);

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

        if(anotherGetOneName != null){
            String getOneByAdditionalServiceContent =
                    """
                        @Override
                        public ResponseDto<%sResponseDto> getOneBy%s(String %s) {
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
                            upperApiName, upperAnotherGetOneName, anotherGetOneName, upperApiName, upperAnotherGetOneName,
                            upperApiName, anotherGetOneName, upperApiName, upperApiName, upperApiName);

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

        String columnsForUpdateContent =
                """
                ///update data
                """;

        for(String column : columnsForAddAndUpdate){
            String columnsForUpdateContentTemplate =
                    """
                                entity.set%s(requestDto.get%s());
                    """.formatted(uppercaseFirstLetter(column), uppercaseFirstLetter(column));

            columnsForUpdateContent = columnsForUpdateContent + columnsForUpdateContentTemplate;
        }

        String updateServiceContent =
                """
                    @Override
                    public ResponseStatusOnlyDto update(%sRequestDto requestDto) {
                        Optional<%sEntity> optionalExistingEntity = %sRepository.findById(requestDto.getId());
                
                        ResponseStatusOnlyDto responseStatusOnlyDto = new ResponseStatusOnlyDto();
                
                        if(optionalExistingEntity.isPresent()){
                            %sEntity entity = optionalExistingEntity.get();
                
                            %s
                            %sRepository.save(entity);
                
                            responseStatusOnlyDto.setStatus(ResponseDtoStatusEnum.SUCCESS);
                        }else{
                            responseStatusOnlyDto.setStatus(ResponseDtoStatusEnum.ERROR);
                            responseStatusOnlyDto.setMessage(PostException.NOT_FOUND);
                        }
                
                        return responseStatusOnlyDto;
                    }
                """.formatted(upperApiName, upperApiName, apiName, upperApiName, columnsForUpdateContent, apiName);

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

        if(anotherGetOneName != null){
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
                    upperApiName, upperAnotherGetOneName, upperApiName, upperApiName, anotherGetOneName, upperAnotherGetOneName);

            queryServiceContent = queryServiceContent + additionalQueryServiceContent;
        }

        String endOfTemplate =
                """
                }
                """;

        return content + addServiceContent + getOneContent + getPaginationServiceContent + updateServiceContent +
                deleteServiceContent + queryServiceContent + endOfTemplate;
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

    private static void generateFile(String apiNameInput, String basePackagePath,
                                     String basePackageInput, String fileType, String[] columns,
                                     String anotherGetOneName, String changeUniqueValidationName,
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
                fileContent = generateControllerContent(basePackageInput, apiNameInput, className, anotherGetOneName);
                break;
            case("entity"):
                fileName = uppercaseFirstLetter(apiNameInput).concat("Entity.java");
                className = uppercaseFirstLetter(apiNameInput).concat("Entity");
                fileDirectory = String.format("src/main/java/com/mnf/javaapigenerator/result/%s/entity", basePackagePath);
                filePath = String.format("%s/%s", fileDirectory, fileName);
                fileContent = generateEntityContent(basePackageInput, apiNameInput, className, columns);
                break;
            case("entityListener"):
                String entityName = uppercaseFirstLetter(apiNameInput).concat("Entity");
                fileName = uppercaseFirstLetter(apiNameInput).concat("EntityListener.java");
                className = uppercaseFirstLetter(apiNameInput).concat("EntityListener");
                fileDirectory = String.format("src/main/java/com/mnf/javaapigenerator/result/%s/entity/listener", basePackagePath);
                filePath = String.format("%s/%s", fileDirectory, fileName);
                fileContent = generateEntityListenerContent(basePackageInput, apiNameInput, className, entityName);
                break;
            case("dto"):
                fileDirectory = String.format("src/main/java/com/mnf/javaapigenerator/result/%s/dto", basePackagePath);
                String requestFileName = uppercaseFirstLetter(apiNameInput).concat("RequestDto.java");
                String responseFileName = uppercaseFirstLetter(apiNameInput).concat("ResponseDto.java");
                String requestFilePath = String.format("%s/%s", fileDirectory, requestFileName);
                String responseFilePath = String.format("%s/%s", fileDirectory, responseFileName);
                String requestFileContent = generateDtoContent(basePackageInput, apiNameInput, className, requestFileName, columns, anotherGetOneName);
                String responseFileContent = generateDtoContent(basePackageInput, apiNameInput, className, responseFileName, columns, anotherGetOneName);
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
                fileContent = generateInterfaceServiceContent(basePackageInput, apiNameInput, anotherGetOneName);
                break;
            case("serviceImpl"):
                fileName = uppercaseFirstLetter(apiNameInput).concat("ServiceImpl.java");
                fileDirectory = String.format("src/main/java/com/mnf/javaapigenerator/result/%s/service", basePackagePath);
                filePath = String.format("%s/%s", fileDirectory, fileName);
                fileContent = generateServiceImplContent(basePackageInput, apiNameInput, anotherGetOneName, changeUniqueValidationName, columnsForUpdate);
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
                    printWriter.close();
                }
            }else{
                File dir = new File(fileDirectory);
                dir.mkdirs();
                FileWriter fileWriter = new FileWriter(filePath);
                PrintWriter printWriter = new PrintWriter(fileWriter);

                printWriter.print(fileContent);
                printWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String uppercaseFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        char firstChar = Character.toUpperCase(input.charAt(0));
        return firstChar + input.substring(1);
    }
}
