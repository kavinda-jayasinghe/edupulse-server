package com.info_labs.edupulse.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignmentDto {
    private Integer id;
    private String  title;
    private String  description;
    private String  dueDate;
    private String  createdAt;
    private boolean visible;
    private boolean hasFile;
    private String  fileName;
    private String  fileType;
}
