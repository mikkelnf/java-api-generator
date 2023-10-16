package com.mnf.javaapigenerator.util;

import java.io.*;
import java.util.zip.*;

public class ZipGeneratorUtil {
    public static void zipDirectory(String sourceDirectory, String zipDestinationDir, String zipName) {
        String parentFolderPath = sourceDirectory;

        File parentFolder = new File(parentFolderPath);
        if (parentFolder.exists() && parentFolder.isDirectory()) {
            File[] subfolders = parentFolder.listFiles(File::isDirectory);
            if (subfolders != null) {
                for (File subfolder : subfolders) {
                    parentFolderPath = subfolder.getAbsolutePath();
                    break;
                }
            }
        }

        File detinationDir = new File(zipDestinationDir);
        detinationDir.mkdirs();

        try {
            FileOutputStream fos = new FileOutputStream(zipDestinationDir.concat(zipName));
            ZipOutputStream zos = new ZipOutputStream(fos);

            File sourceDir = new File(parentFolderPath);
            addDirectoryToZip(zos, sourceDir, sourceDir.getName());

            zos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addDirectoryToZip(ZipOutputStream zos, File folder, String parentFolderName) throws IOException {
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    addDirectoryToZip(zos, file, parentFolderName + "/" + file.getName());
                } else {
                    FileInputStream fis = new FileInputStream(file);
                    ZipEntry zipEntry = new ZipEntry(parentFolderName + "/" + file.getName());
                    zos.putNextEntry(zipEntry);

                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }

                    fis.close();
                }
            }
        }
    }
}
