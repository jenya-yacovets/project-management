package com.yacovets.projectmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProjectAddToTeamModel {
    @NotBlank
    @Length(min = 3, max = 30)
    private String emailOrUsername;
}
