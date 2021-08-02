package com.yacovets.projectmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSetPasswordModel {
    @NotBlank(message = "Required for entering")
    @Length(min = 3, max = 30, message = "The length of the password must be from 3 to 30 characters")
    private String password;

    @NotBlank(message = "Required for entering")
    @Length(min = 3, max = 30, message = "The length of the password confirmation must be from 3 to 30 characters")
    private String passwordConfirmation;
}
