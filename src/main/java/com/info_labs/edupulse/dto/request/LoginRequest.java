package com.info_labs.edupulse.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String mobile;
    private String password;
}
