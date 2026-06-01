package com.info_labs.edupulse.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateClassRequest {
    private String name;
    private String classCode;
    private String subject;
}
