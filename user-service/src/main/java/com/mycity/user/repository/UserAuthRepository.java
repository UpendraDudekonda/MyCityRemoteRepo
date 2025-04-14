package com.mycity.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mycity.user.entity.User;

@Repository
public interface UserAuthRepository extends JpaRepository<User, Long>{

	boolean existsByUsername(String firstname); 

	boolean existsByEmail(String email);

	Optional<User> findByUsername(String username);

	User findByEmail(String email);

}