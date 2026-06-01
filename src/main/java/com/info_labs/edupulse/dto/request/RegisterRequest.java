package com.info_labs.edupulse.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String name;
    private String mobile;
    private String password;
}
