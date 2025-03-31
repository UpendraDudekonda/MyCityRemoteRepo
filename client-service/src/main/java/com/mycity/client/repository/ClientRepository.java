package com.mycity.client.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mycity.client.entity.Client;

public interface ClientRepository extends JpaRepository<Client, Long>{

}
