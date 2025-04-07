package com.mycity.shared.wishlist.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishlistDTO {

    private Long id;
    private Long userId;
    private Long placeId;
}

