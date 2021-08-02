package com.yacovets.projectmanagement.model;

import com.yacovets.projectmanagement.entity.TaskPriorityEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskCreateModel {
    @Min(value = 1, message = "Input correct performer")
    private long performer;

    @NotNull(message = "Input correct priority")
    private TaskPriorityEnum priority;

    @NotBlank(message = "Required for entering")
    @Length(min = 3, max = 5000, message = "The length of the task must be from 3 to 5000 characters")
    private String task;
}
