package com.boku.integrations;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.boku.integrations.entity.Message;
import com.boku.integrations.service.provider.ProviderClientImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@Import({TestConfig.class, JacksonAutoConfiguration.class})
@ExtendWith(SpringExtension.class)
public class ProviderClientTests {
	@Autowired
	ObjectMapper mapper;

	@Autowired
	MockWebServer server;

	@Autowired
	ProviderClientImpl providerClientImpl;

	@Test
	@DisplayName("Should send text from merchant to provider")
	void send_sms_to_provider() {
		Message message = new Message("e39ce00e-f8b5-4b0b-96ce-d68f94525704", "this is the reply message content", "37255555555", "Etisalat");
		server.enqueue(
			new MockResponse()
				.setResponseCode(200)
				.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
            	.setBody("OK")
		);
		String resp = providerClientImpl.forwardSMSFromMerchant(message);
		String expected = "OK";

		assertTrue(resp.equals(expected));
	}

}
