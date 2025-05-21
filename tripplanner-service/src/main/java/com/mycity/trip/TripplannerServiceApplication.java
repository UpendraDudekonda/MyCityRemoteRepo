package com.mycity.trip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class TripplannerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TripplannerServiceApplication.class, args);
	}

}
