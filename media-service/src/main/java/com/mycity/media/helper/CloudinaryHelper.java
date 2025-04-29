package com.mycity.media.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Component
public class CloudinaryHelper {

    private final Cloudinary cloudinary;

    public CloudinaryHelper(
        @Value("${cloudinary.cloud_name}") String cloudName,
        @Value("${cloudinary.key}") String apiKey,
        @Value("${cloudinary.secret}") String apiSecret
    ) {
        this.cloudinary = new Cloudinary(
            ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
            )
        );
    }

    public String saveImage(MultipartFile image) {
        try {
            Map<String, Object> uploadOptions = new HashMap<>();
            uploadOptions.put("folder", "Events");

            Map<String, Object> uploadResult = cloudinary.uploader().upload(image.getBytes(), uploadOptions);
            return (String) uploadResult.get("url");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to upload image to Cloudinary", e);
        }
    }

    public List<String> saveImages(List<MultipartFile> files) {
        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                Map<String, Object> uploadOptions = new HashMap<>();
                uploadOptions.put("folder", "Events");

                Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadOptions);
                String url = (String) uploadResult.get("url");

                if (url != null) {
                    imageUrls.add(url);
                }

            } catch (IOException e) {
                e.printStackTrace();
                // Optional: continue uploading others or throw exception
            }
        }

        return imageUrls;
    }

//	public void deleteImage(String publicId) {
//		// TODO Auto-generated method stub
//		
//	}
}
