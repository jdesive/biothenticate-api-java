package com.softwarfare.biothenticate.api.models;

import lombok.Data;

@Data
public class LoginRequest {

    private String email;
    private String password;

}
