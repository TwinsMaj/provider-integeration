package com.boku.integrations.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {

	private List<Merchant> merchant = new ArrayList<>();
	private Provider provider = new Provider();
	
	@Data
	@AllArgsConstructor
	public static class Merchant {
		private String endpoint;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Provider {
		private String endpoint;
		private String username;
		private String password;
	}
}
