package com.mycity.category.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mycity.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>
{

	Category findByName(String name);

	boolean existsByNameIgnoreCase(String categoryName);	
	
	Optional<Category> findByNameIgnoreCase(String categoryName);
	
	@Query("SELECT c.description FROM Category c WHERE LOWER(c.name) = LOWER(:categoryName)")
	List<String> findDescriptionsByNameIgnoreCase(@Param("categoryName") String categoryName);	

	Optional<Category> findByName(String name);

	boolean existsByNameIgnoreCase(String categoryName);	
	
	Optional<Category> findByNameIgnoreCase(String categoryName);
	
	@Query("SELECT c.description FROM Category c WHERE LOWER(c.name) = LOWER(:categoryName)")
	List<String> findDescriptionsByNameIgnoreCase(@Param("categoryName") String categoryName);

	List<Category> findAllByName(String categoryName);


}
