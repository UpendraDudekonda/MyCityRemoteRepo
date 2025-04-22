package com.mycity.admin.service;

import com.mycity.admin.entity.Admin;


public interface AdminAuthInterface {

	Admin loginUser(String email, String password);

	
}
