package com.mycity.category.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mycity.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>{

	Category findByName(String name);

	boolean existsByNameIgnoreCase(String categoryName);	
	
	Optional<Category> findByNameIgnoreCase(String categoryName);

}
