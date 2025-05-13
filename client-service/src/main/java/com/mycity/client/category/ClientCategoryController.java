package com.mycity.client.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mycity.client.config.CookieTokenExtractor;
import com.mycity.client.serviceImpl.ClientCategoryService;
import com.mycity.shared.categorydto.CategoryImageDTO;

import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/client")  
public class ClientCategoryController {

    private final CookieTokenExtractor cookieTokenExtractor;

    @Autowired
    private ClientCategoryService clientCategoryService;


    ClientCategoryController(CookieTokenExtractor cookieTokenExtractor) {
        this.cookieTokenExtractor = cookieTokenExtractor;
    }  

    
    @GetMapping("/bycategory/unique/images")
    public Mono<ResponseEntity<List<CategoryImageDTO>>> getCategoriesWithImages() {
    	System.out.println("iam in clientservicecategory");
        return clientCategoryService.fetchCategoriesWithImages()  
            .map(ResponseEntity::ok);  
    }
}
