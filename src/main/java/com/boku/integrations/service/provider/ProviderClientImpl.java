package com.boku.integrations.service.provider;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.boku.integrations.config.AppProperties;
import com.boku.integrations.entity.Message;
import com.boku.integrations.error.ServiceException;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
@AllArgsConstructor
public class ProviderClientImpl implements ProviderClient {
	@Autowired
	private AppProperties appProperties;

	@Autowired
	private WebClient webClient;
	
	private static final int MAX_ATTEMPTS = 3;
	private static final int DURATION = 2;
	private static final Logger LOGGER = LoggerFactory.getLogger(ProviderClientImpl.class);

	public String forwardSMSFromMerchant(Message message) {
		String endpoint = appProperties.getProvider().getEndpoint();
		String authUser = appProperties.getProvider().getUsername();
		String authPassword = appProperties.getProvider().getPassword();

		String id = message.getMo_message_id();
		String response = "";

		try {
			response = webClient.get()
				.uri(endpoint, builder -> builder.path("/send")
					.queryParam("message", message.getMessage())
					.queryParam("mo_message_id", id)
					.queryParam("receive", message.getReceiver())
					.queryParam("operator", message.getOperator()).build())
				.headers(headers -> headers.setBasicAuth(authUser, authPassword))
				.retrieve()
				.onStatus(HttpStatusCode::is5xxServerError,
						resp -> Mono.error(new ServiceException(resp.statusCode().toString() + " Call to provider service failed.")))
				.bodyToMono(String.class)
				.retryWhen(Retry.backoff(MAX_ATTEMPTS, Duration.ofSeconds(DURATION))
						.onRetryExhaustedThrow((spec, signal) -> {
							throw new ServiceException("Service call failed even after retrying " + signal.totalRetries() + " times.");
						})
						.filter(ex -> ex instanceof ServiceException)
						.doBeforeRetry(x -> LOGGER.info("Provider not responding. Cannot forward message with id: {}. Retrying... {} of {}",
								id, x.totalRetries() + 1, MAX_ATTEMPTS)))
				.doOnSuccess((x) -> LOGGER.info("SMS sent to provider successfully, id: {}, text: {}", id, message))
				.block();

		} catch (ServiceException ex) {
			logException(id, ex, " cannot send notification now, id: {}");
		} catch (RuntimeException ex) {
			logException(id, ex, "Merchant returned error for id: {}");
		}
		System.out.println(response);
		return response;
	}

	private void logException(String id, RuntimeException ex, String message) {
		LOGGER.atInfo()
			.setMessage(ex.getMessage() + message)
			.addArgument(id)
			.setCause(ex.getCause())
			.log();
	}
}