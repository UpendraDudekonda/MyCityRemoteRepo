package com.mycity.navigation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class NavigationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NavigationServiceApplication.class, args);
	}

}
