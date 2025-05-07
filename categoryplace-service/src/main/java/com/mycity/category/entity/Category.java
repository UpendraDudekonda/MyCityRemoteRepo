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

    private Long placeId;    
    
    private String placeName;

    private String name;  

    private String description; 
    

    public Category() {}

    
    public Category(String name, String description) {
        this.name = name;
        this.description = description;
        
        
        
        
    }
}