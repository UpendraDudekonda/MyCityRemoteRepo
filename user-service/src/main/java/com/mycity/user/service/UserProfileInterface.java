package com.mycity.user.service;

import org.springframework.stereotype.Service;

import com.mycity.shared.userdto.UserDTO;
import com.mycity.shared.userdto.UserLoginRequest;
import com.mycity.shared.userdto.UserRegRequest;
import com.mycity.shared.userdto.UserResponseDTO;

@Service
public interface UserProfileInterface {

	UserResponseDTO getUserById(String userId);

	String updateUser(String uid,UserDTO dto);
	
	Long getUserIdByUserName(String userName);
	
	String deleteUser(String userId);
	
	String changePassword(UserLoginRequest req);
	
}
