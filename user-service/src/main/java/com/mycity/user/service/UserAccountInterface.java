package com.mycity.user.service;

import java.util.Optional;

import com.mycity.shared.updatedto.UpdatePhoneRequest;
import com.mycity.user.entity.User;

public interface UserAccountInterface {

	boolean updatePhoneNumber(Long userId, UpdatePhoneRequest request);

	Optional<User> findById(Long userId);

	User save(User user);

}
