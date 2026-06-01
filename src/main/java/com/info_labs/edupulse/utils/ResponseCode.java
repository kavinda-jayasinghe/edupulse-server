package com.info_labs.edupulse.utils;

import lombok.Getter;

@Getter
public enum ResponseCode {

    INVALID_REQUEST(1000, "Invalid Request"),
    ALREADY_ENROLLED(4000, "you are already enrolled for this room"),
    DOC_NOT_APPROVED(5008, "Referenced documents are not approved");


    private final int code;
    private final String description;

    ResponseCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

}
