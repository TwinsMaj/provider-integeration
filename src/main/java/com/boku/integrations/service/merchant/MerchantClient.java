package com.boku.integrations.service.merchant;

import org.springframework.stereotype.Component;

import com.boku.integrations.entity.MerchantResponse;
import com.boku.integrations.entity.Notification;

@Component
public interface MerchantClient {

	public MerchantResponse sendNotification(Notification notification);
}