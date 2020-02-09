package com.softwarfare.biothenticate.api;

import lombok.Getter;

public enum Endpoints {

    LOGIN("/signin"),
    USER("/user"),
    USER_ALL("/user/all"),
    AUTHENTICATE("/integration/authenticate");

    @Getter private String path;

    Endpoints(String path) {
        this.path = path;
    }
}
