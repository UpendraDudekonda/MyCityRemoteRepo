package com.mycity.user.serviceImpl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mycity.shared.userdto.UserDTO;
import com.mycity.shared.userdto.UserLoginRequest;
import com.mycity.shared.userdto.UserResponseDTO;
import com.mycity.user.entity.User;
import com.mycity.user.exception.UserNotFoundException;
import com.mycity.user.repository.UserProfileRepository;
import com.mycity.user.service.UserProfileInterface;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfileService implements UserProfileInterface {

    private final UserProfileRepository userRepo;

    private  final PasswordEncoder passwordEncoder;
    
    @Override
    public UserResponseDTO getUserById(String userIdStr) {
       
        if (!userIdStr.matches("\\d+")) {
            throw new IllegalArgumentException("Invalid User ID format. Must be a number.");
        }

        Long userId = Long.parseLong(userIdStr);

       
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

       
        return new UserResponseDTO(
                String.valueOf(user.getId()),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                String.valueOf(user.getMobilenumber()),
                user.getUpdatedDate(),
                user.getCreatedDate()
        );
    }
    
    


	@Override
	public Long getUserIdByUserName(String userName)  //to get UserId by using userName while adding review to the place.
	{
		//use userRepo
		Long id=userRepo.getUserIdByName(userName);
		return id;
	}

	@Override
	public String updateUser(String uid, UserDTO dto)
	{
		
		if (!uid.matches("\\d+")) 
		{
	            throw new IllegalArgumentException("Invalid User ID format,it Must be a number.");
	    }
		
		//Parsing String to Long
		Long id=Long.parseLong(uid);
		
	    //use UserRepo
		Optional<User> opt=userRepo.findById(id);
		
		if(opt.isPresent())
		{
			//get User from opt
			User user=opt.get();
			
			user.setUsername(dto.getFirstname()+" "+dto.getLastname());
			user.setEmail(dto.getEmail());
			user.setMobilenumber(dto.getMobilenumber());
			user.setUpdatedDate(LocalDateTime.now());

			
			//use UserRepo to save the updated Object
			Long idVal=userRepo.save(user).getId();
			return "User with ::"+idVal+" is updated successfully..";
		}
		else
		{
			throw new IllegalArgumentException("Invalid id");
		}
	
	}

	@Override
	public String deleteUser(String userId) 
	{
		if (!userId.matches("\\d+")) 
		{
	            throw new IllegalArgumentException("Invalid User ID format,it Must be a number.");
	    }
		
		Long uid=Long.parseLong(userId);
		
		//check whether the user with give ID Exists or Not
		Optional<User> opt=userRepo.findById(uid);
		
		if(opt.isPresent())
		{
			//get User
			User user=opt.get();
			//use UserRepo to Delete User
			userRepo.deleteById(user.getId());
			return "User With Id :"+user.getId()+" Deleted Successfully..";
		}
		else
		{
			throw new IllegalArgumentException("Enter a Valid User Id..");
		}
	}

	@Override
	public String changePassword(UserLoginRequest req) 
	{
		System.out.println("UserProfileService.changePassword()");
	    //use UserRepo
	    Optional<User> opt=userRepo.findByEmail(req.getEmail());
	    if(opt.isPresent())
	    {
	    	User user=opt.get();
	    	//encode the Password Using PassWord Encoder
	    	String newPwd=passwordEncoder.encode(req.getPassword());
	    	//setting newPassword to the Existing User
	    	user.setPassword(newPwd);
	    	//Save the Updated User using UserRepo
	    	String mail=userRepo.save(user).getEmail();
	    	return "User With Mail Id :"+mail+" has changed Password Successfully..";
	    	
	    }
	    else
	    {
	    	throw new IllegalArgumentException("Invalid Maild Id (or) User With MailId "+req.getEmail()+" does not Exists..");
	    }
	}
}
