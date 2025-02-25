package com.qnaverse.QnAverse.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Utility class for handling file uploads (profile pictures and media).
 */
@Component
public class FileStorageUtil {

    private static final String UPLOAD_DIR = "C:\\Users\\Suraj\\OneDrive\\Desktop\\qnaverse\\src\\main\\resources\\uploads\\";

    /**
     * Saves a file to the local storage.
     * Returns the unique filename stored, or null if empty.
     */
    public String saveFile(MultipartFile file, String subDirectory) {
        try {
            if (file == null || file.isEmpty()) {
                return null;
            }

            // Generate unique filename
            String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String filePath = UPLOAD_DIR + subDirectory + "/" + uniqueFileName;

            // Create directories if not exist
            Files.createDirectories(Paths.get(UPLOAD_DIR + subDirectory));

            // Save file
            file.transferTo(new File(filePath));

            return uniqueFileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    /**
     * Deletes a file from storage.
     */
    public boolean deleteFile(String fileName, String subDirectory) {
        String filePath = UPLOAD_DIR + subDirectory + "/" + fileName;
        File file = new File(filePath);
        return file.exists() && file.delete();
    }
}
