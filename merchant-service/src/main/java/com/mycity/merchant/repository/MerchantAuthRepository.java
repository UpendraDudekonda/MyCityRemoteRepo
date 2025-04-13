package com.mycity.merchant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mycity.merchant.entity.Merchant;

@Repository
public interface MerchantAuthRepository extends JpaRepository<Merchant, Long>{

	Merchant findByEmail(String email);

	boolean existsByEmail(String email);

	boolean existsByGstNumber(String gstNumber);

}
