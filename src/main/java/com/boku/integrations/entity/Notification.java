package com.boku.integrations.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
	@NotBlank(message = "message_id cannot be blank")
	private String message_id; 

	@NotBlank(message = "sender cannot be blank")
	private String sender; 

	@NotBlank(message = "text cannot be blank")
	@Pattern(regexp = "(^TXT |FOR).*", message = "invalid message format")
	private String text; 

	@NotBlank(message = "message cannot be blank")
	private String receiver; 

	@NotBlank(message = "operator cannot be blank")
	private String operator; 

	@NotBlank(message = "timeStamp cannot be blank")
	private String timestamp;
}