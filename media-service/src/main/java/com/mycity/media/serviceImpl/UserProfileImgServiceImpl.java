package com.mycity.media.serviceImpl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import com.mycity.media.entity.UserProfileImg;
import com.mycity.media.helper.CloudinaryHelper;
import com.mycity.media.repository.UserProfileImgRepository;
import com.mycity.media.service.UserProfileImgService;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UserProfileImgServiceImpl implements UserProfileImgService {
	
	private final CloudinaryHelper cloudinaryHelper;
    private final UserProfileImgRepository userProfileImgRepository;

    @Override
    public void uploadUserImage(MultipartFile file, String userId) {
    	
    	//check if user had already profile image
    	Optional<UserProfileImg> existUserPic = userProfileImgRepository.findByUserId(userId);
    	
    	//if the user already exist deleteit from cloudinary and db 
    	
    	existUserPic.ifPresent(thatImg -> {
    		
    		cloudinaryHelper.deleteImage(thatImg.getImageUrl());
    		userProfileImgRepository.delete(thatImg);
    	});
    	
    	
        String url = cloudinaryHelper.saveImage(file);

        UserProfileImg image = new UserProfileImg();
        image.setUserId(userId);
        image.setImageUrl(url);
       

        userProfileImgRepository.save(image);
    }
    


    @Override
    public String getImageUrlByUserId(String userId) {
        Optional<UserProfileImg> image = userProfileImgRepository.findByUserId(userId);

      
        return image.map(UserProfileImg::getImageUrl)
                    .orElseThrow(() -> new RuntimeException("Image not found for User ID: " + userId));
    }

}
