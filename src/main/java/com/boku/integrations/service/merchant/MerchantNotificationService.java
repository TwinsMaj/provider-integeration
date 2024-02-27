package com.boku.integrations.service.merchant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.boku.integrations.entity.MerchantResponse;
import com.boku.integrations.entity.Message;
import com.boku.integrations.entity.Notification;
import com.boku.integrations.service.provider.ProviderService;

@Service
public class MerchantNotificationService {
	@Autowired
	private MerchantClient merchantClientImpl;

	@Autowired
	private ProviderService providerService;

	private final Logger LOGGER = LoggerFactory.getLogger(MerchantNotificationService.class);

	@Async("asyncTaskExecutor")
	public void notifyMerchant(Notification notification) {
		LOGGER.info("forwarding payment notification to merchant, id: {}", 
			notification.getMessage_id());
		
		MerchantResponse response = merchantClientImpl.sendNotification(notification);
		String replyText = response != null ? response.getReply_message() : "something went wrong";

		
		Message message = new Message(notification.getMessage_id(), replyText, notification.getSender(), notification.getOperator());
		
		providerService.sendTextToProvider(message);
	}
}
