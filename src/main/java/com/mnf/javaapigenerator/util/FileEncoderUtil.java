package com.mnf.javaapigenerator.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class FileEncoderUtil {
    public static String encodeFileToBase64(String filePath) {
        try {
            // Read the file as binary data
            byte[] fileContent = Files.readAllBytes(Paths.get(filePath));

            // Encode the binary data to Base64
            String base64Content = Base64.getEncoder().encodeToString(fileContent);

            return base64Content;
        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }
    }
}
