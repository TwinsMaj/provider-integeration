package com.boku.integrations.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boku.integrations.entity.Notification;
import com.boku.integrations.service.merchant.MerchantNotificationService;

import jakarta.validation.Valid;


@ControllerAdvice
@RestController
public class PaymentNotificationController {
	@Autowired
	private MerchantNotificationService merchantNotificationService;

	private static final Logger LOGGER = LoggerFactory.getLogger(PaymentNotificationController.class);
	
	@RequestMapping("/api/v1/sms")
	public ResponseEntity<Notification> onPaymentSMSReceived(@Valid Notification notification) {
		LOGGER.info("recevied SMS notification from payment provider, id: {}, body: {}",
			notification.getMessage_id(), notification);
			
		merchantNotificationService.notifyMerchant(notification);
		return new ResponseEntity<Notification>(notification, HttpStatus.OK);
	}
}
