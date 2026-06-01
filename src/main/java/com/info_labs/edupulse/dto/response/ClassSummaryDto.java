package com.info_labs.edupulse.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClassSummaryDto {
    private Integer id;
    private String name;
    private String classCode;
    private String subject;
    private long studentCount;
    private long submissions;
    private long avgScore;
}
