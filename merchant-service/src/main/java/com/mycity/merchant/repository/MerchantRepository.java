package com.mycity.merchant.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mycity.merchant.entity.Merchant;

public interface MerchantRepository extends JpaRepository<Merchant, Long>{

}
