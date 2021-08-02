package com.yacovets.projectmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardCreateModel {
    @NotBlank(message = "Required for entering")
    @Length(min = 3, max = 30, message = "The length of the name must be from 3 to 30 characters")
    private String name;

    @NotBlank(message = "Required for entering")
    @Length(min = 3, max = 5000, message = "The length of the description must be from 3 to 5000 characters")
    private String description;
}
