package com.mycity.user.serviceImpl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mycity.shared.updatedto.UpdatePhoneRequest;
import com.mycity.user.entity.User;
import com.mycity.user.repository.UserAccountRepository;
import com.mycity.user.service.UserAccountInterface;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserAccountService implements UserAccountInterface{

	private final UserAccountRepository userAccount;

    
    public UserAccountService(UserAccountRepository userAccount) {
        this.userAccount = userAccount;
    }

    @Override
    public Optional<User> findById(Long userId) {
        return userAccount.findById(userId);
    }

    @Override
    @Transactional
    public User save(User user) {
        return userAccount.save(user);
    }

    public boolean updatePhoneNumber(Long userId, UpdatePhoneRequest request) {
        Optional<User> userOptional = findById(userId);
        if (userOptional.isEmpty()) {
            return false;
        }

        User user = userOptional.get();
        user.setMobilenumber(request.getPhoneNumber());
        save(user);
        return true;
    }

}
