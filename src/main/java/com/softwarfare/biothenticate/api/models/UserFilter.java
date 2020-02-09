package com.softwarfare.biothenticate.api.models;

import lombok.Getter;

public enum UserFilter {

    NONE("undefined"),
    LOCKED_OUT("lockedout"),
    INACTIVE("inactive"),
    BYPASS("bypass");

    @Getter private String value;

    UserFilter(String value) {
        this.value = value;
    }
}
