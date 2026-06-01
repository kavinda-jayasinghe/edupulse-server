package com.info_labs.edupulse.dto;

public record QuizResponseDto(
    Integer id,
    String  title,
    String  instruction,
    Integer timeDuration,
    String  dueDate,
    String  paperFileName,
    String  paperFileType,
    Long    paperFileSize,
    Integer answerCount,
    String  createdAt,
    Integer teacherId,
    Integer classId,
    String  className
) {}
