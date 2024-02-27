package com.boku.integrations.service.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.boku.integrations.entity.Message;

@Service
public class ProviderService {
	@Autowired
	private ProviderClient providerClientImpl;

	private final Logger LOGGER = LoggerFactory.getLogger(ProviderService.class);

	@Async("asyncTaskExecutor")
	public void sendTextToProvider(Message message) {
		LOGGER.info("Sending text response from merchant to provider, id: {}, data: {}", 
			message.getMo_message_id(), message);
		providerClientImpl.forwardSMSFromMerchant(message);
	}
}
