package com.mycity.categoryplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mycity.categoryplace.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>{

}
