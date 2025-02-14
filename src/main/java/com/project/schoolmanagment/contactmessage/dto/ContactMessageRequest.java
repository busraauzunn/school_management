package com.project.schoolmanagment.contactmessage.dto;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ContactMessageRequest implements Serializable {

	@NotNull(message = "Please enter name")
	@Size(min = 4,max = 16,message = "Your name should be at least 4 characters")
	@Pattern(regexp = "\\A(?!\\s*\\Z).+",message = "Your message must consist of the character .")
	private  String name;

	@Email(message = "Please enter valid email")
	@Size(min = 5,max = 20,message = "Your email should be at least 5 characters")
	@NotNull(message = "Please enter your email")
	//column annotation should be in entity class for DB validation
	@Column(nullable = false,unique = true,length = 20)
	private String email;

	@NotNull(message = "Please enter subject")
	@Size(min = 4, max = 50, message = "Your subject should be at least 4 characters")
	@Pattern(regexp = "\\A(?!\\s*\\Z).+" ,message="Your message must consist of the characters .")
	private String subject;

	@NotNull(message = "Please enter message ")
	@Size(min = 4, max = 50, message = "Your subject should be at least 16 characters")
	@Pattern(regexp = "\\A(?!\\s*\\Z).+" ,message="Your message must consist of the characters .")
	private String message;


}
