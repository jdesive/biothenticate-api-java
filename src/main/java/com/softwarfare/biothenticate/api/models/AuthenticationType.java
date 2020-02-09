package com.softwarfare.biothenticate.api.models;

import lombok.Getter;

public enum AuthenticationType {

    IRIS("IRIS"),
    FACEID("FACEID"),
    TOUCHID("TOCUCHID");

    @Getter private String value;

    AuthenticationType(String value) {
        this.value = value;
    }
}
