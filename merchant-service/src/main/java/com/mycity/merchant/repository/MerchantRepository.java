package com.mycity.merchant.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mycity.merchant.entity.Merchant;

public interface MerchantRepository extends JpaRepository<Merchant, Long>{

	Optional<Merchant> findByEmail(String email);

}
