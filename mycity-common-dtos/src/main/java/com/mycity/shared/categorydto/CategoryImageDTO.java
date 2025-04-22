package com.mycity.shared.categorydto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryImageDTO { 
    private String categoryName;
    private String imageUrl;

   
}
