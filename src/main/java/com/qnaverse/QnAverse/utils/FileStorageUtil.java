package com.qnaverse.QnAverse.utils;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Component
public class FileStorageUtil {

    // Cloudinary configuration
    private final Cloudinary cloudinary;

    public FileStorageUtil() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", System.getenv("CLOUDINARY_CLOUD_NAME"),
                "api_key", System.getenv("CLOUDINARY_API_KEY"),
                "api_secret", System.getenv("CLOUDINARY_API_SECRET")
        ));
    }

    /**
     * Saves a file to Cloudinary under the specified subdirectory.
     * Returns the URL of the uploaded file.
     */
    public String saveToCloudinary(MultipartFile file, String subDirectory) {
        try {
            if (file == null || file.isEmpty()) {
                return null;
            }

            // Upload file to Cloudinary
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", subDirectory
            ));

            // Return the URL of the uploaded file
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to Cloudinary", e);
        }
    }

    /**
     * Deletes a file from Cloudinary using its public ID.
     */
    public void deleteFromCloudinary(String mediaUrl) {
        try {
            // Extract the public ID from the URL (this assumes the URL contains the public ID as a part of the URL)
            String publicId = mediaUrl.substring(mediaUrl.lastIndexOf("/") + 1, mediaUrl.lastIndexOf("."));
            
            // Perform the deletion from Cloudinary
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file from Cloudinary", e);
        }
    }
}
