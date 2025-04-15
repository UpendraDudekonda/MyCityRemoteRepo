package com.mycity.shared.merchantdto;




import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantDetailsResponse {
    private Long id;
    private String email;
    private String role;
    
}