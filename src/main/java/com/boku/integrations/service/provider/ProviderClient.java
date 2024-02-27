package com.boku.integrations.service.provider;

import org.springframework.stereotype.Component;

import com.boku.integrations.entity.Message;

@Component
public interface ProviderClient {
	String forwardSMSFromMerchant(Message message);
}
