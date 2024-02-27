package com.boku.integrations.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
	private String mo_message_id;
	private String message;
	private String receiver;
	private String operator;
}
