package com.mycity.media.serviceImpl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import com.mycity.media.entity.AdminProfileImg;

import com.mycity.media.helper.CloudinaryHelper;
import com.mycity.media.repository.AdminProfileImgRepository;
import com.mycity.media.service.AdminProfileImgService;


import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AdminProfileImgServiceImpl implements AdminProfileImgService{
	
	private final CloudinaryHelper cloudinaryHelper;
	
	private final AdminProfileImgRepository adminProfileImgRepository;
	
//	
//	 @Override
//	    public void uploadUserImage(MultipartFile file, String adminId) {
//	    	
//	        String url = cloudinaryHelper.saveImage(file);
//
//	        AdminProfileImg image = new AdminProfileImg();
//	      
//			image.setAdminId(adminId);
//	        image.setImageUrl(url);
//	       
//
//	        adminProfileImgRepository.save(image);
//	    }
	
	@Override
	public void uploadUserImage(MultipartFile file, String adminId) {
	    // 1. Check if admin already has a profile image
	    Optional<AdminProfileImg> existingImageOpt = adminProfileImgRepository.findByAdminId(adminId);

	    // 2. If exists, delete from Cloudinary & DB
	    existingImageOpt.ifPresent(existingImage -> {
	        cloudinaryHelper.deleteImage(existingImage.getImageUrl()); // You'll need to implement this
	        adminProfileImgRepository.delete(existingImage);
	    });

	    // 3. Upload new image to Cloudinary
	    String url = cloudinaryHelper.saveImage(file);

	    // 4. Save new image record
	    AdminProfileImg image = new AdminProfileImg();
	    image.setAdminId(adminId);
	    image.setImageUrl(url);

	    adminProfileImgRepository.save(image);
	}

	 
	 
	 
	 
	  @Override
	    public String getImageUrlByAdminId(String adminId) {
		  
		  
	        Optional<AdminProfileImg> image = adminProfileImgRepository.findByAdminId(adminId);

	      
	        return image.map(AdminProfileImg::getImageUrl)
	                    .orElseThrow(() -> new RuntimeException("Image not found for User ID: " + adminId));
	    }
	 


}
