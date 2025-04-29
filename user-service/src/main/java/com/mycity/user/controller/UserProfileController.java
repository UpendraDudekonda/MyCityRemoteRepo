package com.mycity.user.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycity.shared.userdto.UserDTO;
import com.mycity.shared.userdto.UserLoginRequest;
import com.mycity.shared.userdto.UserResponseDTO;
import com.mycity.user.service.UserProfileInterface;

@RestController
@RequestMapping("/user")
public class UserProfileController {

    private final UserProfileInterface userProfile;

    public UserProfileController(UserProfileInterface userProfile) {
        this.userProfile = userProfile;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getUserProfile(@RequestHeader("X-User-Id") String userId) {
        UserResponseDTO user = userProfile.getUserById(userId);
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/getuserId/{userName}") //to get userId using username while adding review to the place 
    public ResponseEntity<Long> getUSerIdByName(@PathVariable String userName)
    {
    	System.out.println("UserProfileController.getUSerIdByName()");
    	//use service
    	Long userID=userProfile.getUserIdByUserName(userName);
    	return new ResponseEntity<Long>(userID,HttpStatus.OK);
    }
    
   //ADMIN 
   @PutMapping("/updateuser/{userId}") 
   public ResponseEntity<String> updateUser(@RequestBody UserDTO dto,@PathVariable String userId)
   {
	  //use service
	  return new ResponseEntity<String>(userProfile.updateUser(userId,dto),HttpStatus.OK);
   }
   
   //ADMIN
   @DeleteMapping("/deleteuser/{userId}")
   public ResponseEntity<String> deleteUser(@PathVariable String userId)
   {
	   //use service
	   return new ResponseEntity<String>(userProfile.deleteUser(userId),HttpStatus.OK);
   }
   
   @PatchMapping("/updatepassword")
   public ResponseEntity<String> updatePassword(@RequestBody UserLoginRequest req)
   {
	   //use service
	   return new ResponseEntity<String>(userProfile.changePassword(req),HttpStatus.ACCEPTED);
   }
    
}
