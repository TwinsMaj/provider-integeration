package com.boku.integrations;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

import com.boku.integrations.config.AppProperties;
import com.boku.integrations.service.merchant.MerchantClientImpl;
import com.boku.integrations.service.provider.ProviderClientImpl;

import okhttp3.mockwebserver.MockWebServer;

@TestConfiguration
public class TestConfig {
	@Bean
	public MockWebServer webServer() {
		return new MockWebServer();
	}

	@Bean
	public AppProperties appProperties() {
		AppProperties appProperties = new AppProperties();
		List<AppProperties.Merchant> endpoints = new ArrayList<>();
		endpoints.add(new AppProperties.Merchant("sometesturl"));
		endpoints.add(new AppProperties.Merchant("sometesturl"));

		AppProperties.Provider provider = new AppProperties.Provider();
		provider.setEndpoint("wsometesturl");
		provider.setUsername("scott");
		provider.setPassword("tiger");
		
		appProperties.setMerchant(endpoints);
		appProperties.setProvider(provider);
		return appProperties;
	}

	@Bean
	public WebClient webClient(MockWebServer webServer) {
		return WebClient.builder().baseUrl(webServer.url("").toString()).build();
	}

	@Bean
	public MerchantClientImpl merchantClientImpl(AppProperties appProperties, WebClient webClient) {
		return new MerchantClientImpl(appProperties, webClient);
	}

	@Bean
	public ProviderClientImpl providerClientImpl(AppProperties appProperties, WebClient webClient) {
		return new ProviderClientImpl(appProperties, webClient);
	}
}
