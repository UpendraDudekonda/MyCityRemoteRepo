package com.mycity.place;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@EntityScan(basePackages = "com.mycity.place.entity")
public class PlaceServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(PlaceServiceApplication.class, args);
	}

}
