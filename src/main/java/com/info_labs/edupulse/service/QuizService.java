package com.info_labs.edupulse.service;

import com.info_labs.edupulse.dto.McqAnswerDto;
import com.info_labs.edupulse.dto.QuizResponseDto;
import com.info_labs.edupulse.entity.ClassEntity;
import com.info_labs.edupulse.entity.McqAnswer;
import com.info_labs.edupulse.entity.Quiz;
import com.info_labs.edupulse.entity.User;
import com.info_labs.edupulse.mapper.QuizMapper;
import com.info_labs.edupulse.repository.McqAnswerRepository;
import com.info_labs.edupulse.repository.QuizRepository;
import com.info_labs.edupulse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository       quizRepository;
    private final McqAnswerRepository  mcqAnswerRepository;
    private final UserRepository       userRepository;
    private final QuizMapper           quizMapper;

    @Transactional
    public QuizResponseDto createQuiz(Integer teacherId,
                                       Integer classId,
                                       MultipartFile paper,
                                       MultipartFile excel,
                                       String title,
                                       String instruction,
                                       Integer timeDuration,
                                       String dueDate) throws IOException {

        User teacher = userRepository.findById(teacherId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found"));

        ClassEntity cls = teacher.getClasses().stream()
            .filter(c -> c.getId().equals(classId))
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Class not assigned to this teacher"));

        Quiz quiz = new Quiz();
        quiz.setTitle(titleCase(title));
        quiz.setInstruction(instruction != null && !instruction.isBlank() ? instruction.trim() : null);
        quiz.setTimeDuration(timeDuration);
        quiz.setDueDate(dueDate != null && !dueDate.isBlank() ? dueDate.trim() : null);
        quiz.setClassEntity(cls);
        quiz.setTeacherId(teacherId);
        quiz.setCreatedAt(LocalDate.now().toString());

        if (paper != null && !paper.isEmpty()) {
            quiz.setPaperFileName(paper.getOriginalFilename() != null ? paper.getOriginalFilename() : "paper");
            quiz.setPaperFileType(paper.getContentType() != null ? paper.getContentType() : "application/octet-stream");
            quiz.setPaperFileSize(paper.getSize());
            quiz.setPaperFileData(paper.getBytes());
        }

        quizRepository.save(quiz);

        List<McqAnswer> answers = parseExcel(excel, quiz);
        mcqAnswerRepository.saveAll(answers);

        return quizMapper.toDto(quiz, answers.size());
    }

    @Transactional(readOnly = true)
    public List<McqAnswerDto> getAnswers(Integer quizId) {
        return quizMapper.toDtoList(mcqAnswerRepository.findByQuizIdOrderByQuestionNumberAsc(quizId));
    }

    // ── Helpers ───────────────────────────────────────────────

    private List<McqAnswer> parseExcel(MultipartFile excel, Quiz quiz) throws IOException {
        if (excel == null || excel.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Answer sheet Excel file is required");
        }
        List<McqAnswer> answers = new ArrayList<>();
        try (Workbook wb = WorkbookFactory.create(excel.getInputStream())) {
            Sheet sheet = wb.getSheetAt(0);
            Row header = sheet.getRow(0);
            if (header == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Excel file is empty");

            int qCol = -1, aCol = -1;
            for (Cell cell : header) {
                String v = getCellString(cell).toLowerCase().trim();
                if ("question".equals(v)) qCol = cell.getColumnIndex();
                if ("answer".equals(v))   aCol = cell.getColumnIndex();
            }
            if (qCol < 0 || aCol < 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Excel must have 'question' and 'answer' header columns");

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                String qStr = getCellString(row.getCell(qCol)).trim();
                String aStr = getCellString(row.getCell(aCol)).trim().toUpperCase();
                if (qStr.isEmpty() || aStr.isEmpty()) continue;
                int qNum;
                try { qNum = Integer.parseInt(qStr); } catch (NumberFormatException e) { continue; }
                McqAnswer ma = new McqAnswer();
                ma.setQuiz(quiz);
                ma.setQuestionNumber(qNum);
                ma.setAnswer(aStr);
                answers.add(ma);
            }
        }
        if (answers.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No valid answers found in Excel");
        return answers;
    }

    private String getCellString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING  -> cell.getStringCellValue();
            case NUMERIC -> {
                double d = cell.getNumericCellValue();
                yield (d == Math.floor(d)) ? String.valueOf((long) d) : String.valueOf(d);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default      -> "";
        };
    }

    private String titleCase(String s) {
        if (s == null || s.isBlank()) return s;
        String[] words = s.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (!w.isEmpty())
                sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1).toLowerCase()).append(' ');
        }
        return sb.toString().trim();
    }
}
