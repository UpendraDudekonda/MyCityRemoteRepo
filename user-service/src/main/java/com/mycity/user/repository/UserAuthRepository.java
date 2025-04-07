package com.mycity.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mycity.user.entity.User;

@Repository
public interface UserAuthRepository extends JpaRepository<User, Long>{

	boolean existsByUsername(String firstname); // Note: Parameter name is 'firstname' here

	boolean existsByEmail(String email);

	Optional<User> findByUsername(String username); // Corrected return type

	Optional<User> findByEmail(String email);

}