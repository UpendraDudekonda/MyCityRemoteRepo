package com.mycity.emergency.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mycity.emergency.entity.Emergency;

public interface EmergencyRepository extends JpaRepository<Emergency, Long>{

}
