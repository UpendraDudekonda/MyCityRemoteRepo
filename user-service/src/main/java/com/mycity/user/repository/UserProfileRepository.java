package com.mycity.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mycity.user.entity.User;

@Repository
public interface UserProfileRepository extends JpaRepository<User, Long>
{		
	@Query("SELECT id FROM User WHERE LOWER(username) = LOWER(:userName)")
	public Long getUserIdByName(String userName);
	
	Optional<User> findByEmail(String email);
}
