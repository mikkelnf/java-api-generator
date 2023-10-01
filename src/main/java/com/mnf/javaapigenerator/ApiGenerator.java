package com.mnf.javaapigenerator;

import com.mnf.javaapigenerator.dto.RequestDto;

import java.io.*;
import java.util.Scanner;

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

        System.out.println("Add additional column? (Y/y)");
        String additionalColumnCheck = scanner.nextLine();

        String[] columns = {""};
        String anotherGetOneName = "";
        if(additionalColumnCheck.matches("[Yy]")){
            System.out.println("Additional column name and dataType (e.g., title:String, stock:int) : ");
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
        }

        scanner.close();

        generateFile(apiName, basePackagePath, basePackage, 0, columns, anotherGetOneName);
        generateFile(apiName, basePackagePath, basePackage, 1, columns, anotherGetOneName);
        generateFile(apiName, basePackagePath, basePackage, 2, columns, anotherGetOneName);
    }

    private static String generateControllerContent(String basePackage, String apiName, String className, String addAnotherGetOneName){
        String apiNameUpperCase = uppercaseFirstLetter(apiName);

        String controllerContentTopSection =
                """
                package %s.controller;
                
                import %s.component.ABaseController;
                import %s.component.dto.*;
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
                        basePackage, basePackage, basePackage, basePackage, apiNameUpperCase,
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

    private static void generateFile(String apiNameInput, String basePackagePath, String basePackageInput, int fileType, String[] columns, String anotherGetOneName) {
        String fileName = "";
        String className = "";
        String fileDirectory = "";
        String filePath = "";
        String fileContent = "";

        switch (fileType){
            case(0):
//              CONTROLLER
                fileName = uppercaseFirstLetter(apiNameInput).concat("Controller.java");
                className = uppercaseFirstLetter(apiNameInput).concat("Controller");
                fileDirectory = String.format("src/main/java/%s/result/controller", basePackagePath);
                filePath = String.format("%s/%s", fileDirectory, fileName);
                fileContent = generateControllerContent(basePackageInput, apiNameInput, className, anotherGetOneName);
                break;
            case(1):
//              ENTITY
                fileName = uppercaseFirstLetter(apiNameInput).concat("Entity.java");
                className = uppercaseFirstLetter(apiNameInput).concat("Entity");
                fileDirectory = String.format("src/main/java/%s/result/entity", basePackagePath);
                filePath = String.format("%s/%s", fileDirectory, fileName);
                fileContent = generateEntityContent(basePackageInput, apiNameInput, className, columns);
                break;
            case(2):
//              ENTITY-LISTENER
                String entityName = uppercaseFirstLetter(apiNameInput).concat("Entity");
                fileName = uppercaseFirstLetter(apiNameInput).concat("EntityListener.java");
                className = uppercaseFirstLetter(apiNameInput).concat("EntityListener");
                fileDirectory = String.format("src/main/java/%s/result/entity/listener", basePackagePath);
                filePath = String.format("%s/%s", fileDirectory, fileName);
                fileContent = generateEntityListenerContent(basePackageInput, apiNameInput, className, entityName);
                break;
            case(3):

            case(4):

            case(5):

            case(6):

        }

        try {
            File dir = new File(fileDirectory);
            dir.mkdirs();
            FileWriter fileWriter = new FileWriter(filePath);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            printWriter.print(fileContent);
            printWriter.close();
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
