package com.mnf.javaapigenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Scanner;

public class ApiGenerator {
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);

        System.out.println("Api name (e.g., customer, product, etc) : ");
        String apiNameInput = scanner.nextLine();
        apiNameInput.toLowerCase();

        System.out.println("Base package name (e.g., com.example.demo) : ");
        String basePackageInput = scanner.nextLine();
        String basePackagePath = basePackageInput.replace(".", "/");

        scanner.close();

        generateFile(apiNameInput, basePackagePath, basePackageInput, 0);
    }

    private static String generateControllerContent(String basePackage, String apiName, String className){
        return
                """
                package %s.controller;
                        
                import org.springframework.beans.factory.annotation.Autowired;
                import org.springframework.http.MediaType;
                import org.springframework.http.ResponseEntity;
                import org.springframework.web.bind.annotation.*;
                
                @RestController
                @RequestMapping("secured/api/%s")
                public class %s {
                    @GetMapping("/{id}")
                    public String getById(String id){
                        return id;
                    }
                }
                """.formatted(basePackage, apiName, className);
    }

    private static String generateEntityContent(String basePackage, String apiName, String className){
        return
                """
                package %s.entity;

                import %s.entity.listener.UserEntityListener;

                import javax.persistence.*;
                import java.time.LocalDate;

                @Entity
                @Table(name = "user")
                @EntityListeners(UserEntityListener.class)
                public class UserEntity {
                    @Id
                    @Column(name = "id")
                    private String id;

                    @Column(name = "username")
                    private String username;

                    @Column(name = "password")
                    private String password;

                    @Column(name = "is_login")
                    private int isLogin;

                    @Column(name = "created_date")
                    private LocalDate createdDate;

                    @Column(name = "updated_date")
                    private LocalDate updatedDate;

                    public String getId() {
                        return id;
                    }

                    public void setId(String id) {
                        this.id = id;
                    }

                    public String getUsername() {
                        return username;
                    }

                    public void setUsername(String username) {
                        this.username = username;
                    }

                    public int getIsLogin() {
                        return isLogin;
                    }

                    public void setIsLogin(int isLogin) {
                        this.isLogin = isLogin;
                    }

                    public LocalDate getCreatedDate() {
                        return createdDate;
                    }

                    public void setCreatedDate(LocalDate createdDate) {
                        this.createdDate = createdDate;
                    }

                    public String getPassword() {
                        return password;
                    }

                    public void setPassword(String password) {
                        this.password = password;
                    }

                    public LocalDate getUpdatedDate() {
                        return updatedDate;
                    }

                    public void setUpdatedDate(LocalDate updatedDate) {
                        this.updatedDate = updatedDate;
                    }
                }

                """.formatted(basePackage, apiName, className);
    }

    private static String generateEntityListenerContent(String basePackage, String apiName, String className, String entityName){
        return
                """
                package %s.entity.listener;
                                
                import %s.entity.%s;
                                
                import javax.persistence.PrePersist;
                import javax.persistence.PreUpdate;
                import java.time.LocalDate;
                import java.util.UUID;
                                
                public class %s {
                    @PrePersist
                    public void onPrePersist(%s entity){
                        if(entity.getCreatedDate() == null) entity.setCreatedDate(LocalDate.now());
                                
                        if(entity.getId() == null) entity.setId(UUID.randomUUID().toString());
                                
                        entity.setIsLogin(0);
                    }
                                
                    @PreUpdate
                    public void onPreUpdate(%s entity){
                        entity.setUpdatedDate(LocalDate.now());
                    }
                }
                """.formatted(basePackage, basePackage, entityName, className, entityName, entityName);
    }

    private static void generateFile(String apiNameInput, String basePackagePath, String basePackageInput, int fileType) {
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
                fileDirectory = String.format("src/main/java/%s", basePackagePath);
                filePath = String.format("%s/%s", fileDirectory, fileName);
                fileContent = generateControllerContent(basePackageInput, apiNameInput, className);
                break;
            case(1):
//              ENTITY
                fileName = uppercaseFirstLetter(apiNameInput).concat("Entity.java");
                className = uppercaseFirstLetter(apiNameInput).concat("Entity");
                fileDirectory = String.format("src/main/java/%s", basePackagePath);
                filePath = String.format("%s/%s", fileDirectory, fileName);
                fileContent = generateEntityContent(basePackageInput, apiNameInput, className);
                break;
            case(2):
//              ENTITY-LISTENER
                String entityName = uppercaseFirstLetter(apiNameInput).concat("Entity");
                fileName = uppercaseFirstLetter(apiNameInput).concat("EntityListener.java");
                className = uppercaseFirstLetter(apiNameInput).concat("EntityListener");
                fileDirectory = String.format("src/main/java/%s", basePackagePath);
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
