package com.mycity.rating.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycity.rating.service.RatingServiceInterface;
import com.mycity.shared.ratingdto.RatingDTO;

import reactor.core.publisher.Mono;
 

@RestController
@RequestMapping("/rating")
public class RatingController {

    @Autowired
    private RatingServiceInterface service;

    @PostMapping("/add")
    public ResponseEntity<String> addRating(@RequestBody RatingDTO dto) 
    {
    	System.out.println("RatingController.addRating()");
        return new ResponseEntity<String>(service.addRating(dto),HttpStatus.OK);
    }
}

