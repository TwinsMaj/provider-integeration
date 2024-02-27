package com.boku.integrations.service.merchant;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.boku.integrations.config.AppProperties;
import com.boku.integrations.entity.MerchantResponse;
import com.boku.integrations.entity.Notification;
import com.boku.integrations.error.ServiceException;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
@AllArgsConstructor
public class MerchantClientImpl implements MerchantClient{
	@Autowired
	private AppProperties appProperties;

	@Autowired
	private WebClient webClient;

	private static final String KEYWORD_TXT = "TXT";
	private static final String KEYWORD_FOR = "FOR";
	private static final int MAX_ATTEMPTS = 3;
	private static final int DURATION = 2;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MerchantClientImpl.class);

	public MerchantResponse sendNotification(Notification notification) {
		String endpoint = getEndpoint(notification);
		
		MerchantResponse responseBody = new MerchantResponse();

		try {
			ResponseEntity<MerchantResponse> response =  webClient.post()
				.uri(endpoint)
				.bodyValue(BodyInserters.fromValue(notification))
				.retrieve()
				.onStatus(HttpStatusCode::is5xxServerError,
						resp -> Mono.error(new ServiceException(resp.statusCode().toString() + " Merchant service call failed.")))
				.toEntity(MerchantResponse.class)
				.retryWhen(Retry.backoff(MAX_ATTEMPTS, Duration.ofSeconds(DURATION))
						.onRetryExhaustedThrow((spec, signal) -> {
							throw new ServiceException("Service call failed even after retrying " + signal.totalRetries() + " times.");
						})
						.filter(ex -> ex instanceof ServiceException)
						.doBeforeRetry(x -> LOGGER.info("Merchant not responding. Cannot forward message with id: {}. Retrying... {} of {}",
							 notification.getMessage_id(), x.totalRetries() + 1, MAX_ATTEMPTS)))
				.block();

			responseBody = response.getBody();
		} catch (ServiceException ex) {
			logException(notification.getMessage_id(), ex, " cannot send notification now, id: {}");
		} catch (RuntimeException ex) {
			logException(notification.getMessage_id(), ex, "Merchant returned error for id: {}");
		}

		return responseBody;
	}

	private String getEndpoint(Notification notification) {
		String keyword = notification.getText().split(" ")[0];
		String endpoint = "";

		switch (keyword) {
			case KEYWORD_FOR:
				endpoint = getForEndpoint();
				break;
			case KEYWORD_TXT:
				endpoint = getTxtEndpoint();
				break;
			default:
				break;
		}

		return endpoint;
	}

	private String getTxtEndpoint() {
		return appProperties.getMerchant().get(0).getEndpoint();
	}

	private String getForEndpoint() {
		return appProperties.getMerchant().get(1).getEndpoint();
	}

	private void logException(String id, RuntimeException ex, String message) {
		LOGGER.atInfo()
			.setMessage(ex.getMessage() + message)
			.addArgument(id)
			.setCause(ex.getCause())
			.log();
	}
}