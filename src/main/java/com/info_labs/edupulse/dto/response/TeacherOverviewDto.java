package com.info_labs.edupulse.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TeacherOverviewDto {
    private TeacherSummaryDto teacher;
    private int totalClasses;
    private long totalStudents;
    private long totalSubmissions;
    private List<ClassSummaryDto> classes;
}
