package com.info_labs.edupulse.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ClassDetailDto {
    private Integer id;
    private String name;
    private String classCode;
    private String subject;
    private List<StudentDto> students;
    private List<AssignmentDto> assignments;
}
