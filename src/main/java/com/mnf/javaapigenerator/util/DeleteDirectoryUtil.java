package com.mnf.javaapigenerator.util;

import java.io.File;

public class DeleteDirectoryUtil {
    public static void deleteDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file.getAbsolutePath());
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete(); // Delete the empty directory
        }
    }
}
