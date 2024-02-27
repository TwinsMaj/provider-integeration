package com.boku.integrations;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.boku.integrations.entity.MerchantResponse;
import com.boku.integrations.entity.Notification;
import com.boku.integrations.service.merchant.MerchantClientImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@Import({TestConfig.class, JacksonAutoConfiguration.class})
@ExtendWith(SpringExtension.class)
class MerchantClientTests {
	@Autowired
	ObjectMapper mapper;

	@Autowired
	MockWebServer server;

	@Autowired
	private MerchantClientImpl merchantClientImpl;

	@Test
	@DisplayName("Should get text content from merchant service")
	void send_payment_notification_to_merchant() {
		Notification notification = new Notification("e39ce00e-f8b5-4b0b-96ce-d68f94525704", 
			"37255555555", "TXT COINS", "13011", "Etisalat", "2019-09-03 12:32:13");

		server.enqueue(
			new MockResponse()
				.setResponseCode(200)
				.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            	.setBody(successBody())
		);

		MerchantResponse resp = merchantClientImpl.sendNotification(notification);

		assertEquals(resp.getReply_message(), "this is the reply message content");
	}

	private String successBody() {
		try {
			return mapper.writeValueAsString(new MerchantResponse("this is the reply message content"));
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

}
