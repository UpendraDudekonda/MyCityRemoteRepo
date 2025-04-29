package com.mycity.shared.userdto;

import lombok.Data;

@Data
public class UserLoginRequest 
{
	private String email;
	
	private String password;
}
