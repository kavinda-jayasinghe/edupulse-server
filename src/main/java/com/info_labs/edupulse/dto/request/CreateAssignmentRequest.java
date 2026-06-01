package com.info_labs.edupulse.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAssignmentRequest {
    private String title;
    private String description;
    private String dueDate;
}
