package com.softwarfare.biothenticate.api.models;

import lombok.Data;

@Data
public class LoginResponse {

    private String token;
    private boolean success;

}
