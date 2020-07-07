package com.dept.video.server.enums;

public enum UserSource {

    INTERNAL("Internal"),
    GUEST("Guest"),
    EXTERNAL("External");

    private String value;

    UserSource(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
