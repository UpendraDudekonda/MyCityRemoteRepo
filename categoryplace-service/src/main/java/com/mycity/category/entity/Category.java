package com.mycity.category.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "categories")
@Data
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    
    private String name;  // Example: "Historical", "Adventure", "Wildlife"

    
    private String description; // Description about the category

    
//    private List<place> places; // List of places under this category

    
}