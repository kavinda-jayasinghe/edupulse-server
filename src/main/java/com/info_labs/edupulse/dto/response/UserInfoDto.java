package com.info_labs.edupulse.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class UserInfoDto {
    private Integer id;
    private String  name;
    private String  mobile;
    private String  profileType;
    private Integer classId;
    private String  className;
    private List<Map<String, Object>> classes;
}
