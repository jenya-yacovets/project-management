package com.yacovets.projectmanagement.model;

import com.yacovets.projectmanagement.entity.Board;
import com.yacovets.projectmanagement.entity.Project;
import com.yacovets.projectmanagement.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class HomePageDataModel {
    private List<Project> projects;
    private List<Board> boards;
    private List<Task> tasks;
    private int indexCompletedStatusTasks;
    private int indexInProcessingStatusTasks;
    private int indexLittlePriorityTasks;
    private int indexMediumPriorityTasks;
    private int indexLargePriorityTasks;
}
