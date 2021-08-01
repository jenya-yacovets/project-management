package com.yacovets.projectmanagement.entity;

public enum TaskPriorityEnum {
    LITTLE("Little"),
    MEDIUM("Medium"),
    LARGE("Large");

    private final String displayValue;

    private TaskPriorityEnum(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
