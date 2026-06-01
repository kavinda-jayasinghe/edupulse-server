package com.info_labs.edupulse.dto;

public record UpdateQuizRequest(
    String  title,
    String  instruction,
    Integer timeDuration,
    String  dueDate
) {}
