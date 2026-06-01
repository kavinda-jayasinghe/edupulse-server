package com.info_labs.edupulse.mapper;

import com.info_labs.edupulse.dto.McqAnswerDto;
import com.info_labs.edupulse.dto.QuizResponseDto;
import com.info_labs.edupulse.entity.McqAnswer;
import com.info_labs.edupulse.entity.Quiz;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QuizMapper {

    public QuizResponseDto toDto(Quiz quiz, int answerCount) {
        return new QuizResponseDto(
            quiz.getId(),
            quiz.getTitle(),
            quiz.getInstruction(),
            quiz.getTimeDuration(),
            quiz.getDueDate(),
            quiz.getPaperFileName(),
            quiz.getPaperFileType(),
            quiz.getPaperFileSize(),
            answerCount,
            quiz.getCreatedAt(),
            quiz.getTeacherId(),
            quiz.getClassEntity() != null ? quiz.getClassEntity().getId()   : null,
            quiz.getClassEntity() != null ? quiz.getClassEntity().getName() : null
        );
    }

    public McqAnswerDto toDto(McqAnswer a) {
        return new McqAnswerDto(a.getQuestionNumber(), a.getAnswer());
    }

    public List<McqAnswerDto> toDtoList(List<McqAnswer> answers) {
        return answers.stream().map(this::toDto).toList();
    }
}
