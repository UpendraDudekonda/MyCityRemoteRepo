package com.mycity.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mycity.user.entity.User;

@Repository
public interface UserAccountRepository extends JpaRepository<User, Long>{

}
