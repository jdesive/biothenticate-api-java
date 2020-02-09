package com.softwarfare.biothenticate.api.models;

import lombok.Data;

@Data
public class AuthenticateRequest {

    private String authType;
    private String email;
    private String message;
    private String request;
    private String title;

}
