package com.yacovets.projectmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPasswordRecoveryModel {
    @NotBlank(message = "Required for entering")
    @Length(min = 3, max = 25, message = "The length of the username must be from 3 to 25 characters")
    private String username;

    @NotBlank(message = "Required for entering")
    @Length(min = 3, max = 25, message = "The length of the email must be from 4 to 25 characters")
    @Email(message = "Enter the correct email address")
    private String email;
}