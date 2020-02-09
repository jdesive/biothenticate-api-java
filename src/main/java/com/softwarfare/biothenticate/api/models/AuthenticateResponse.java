package com.softwarfare.biothenticate.api.models;

import lombok.Data;

@Data
public class AuthenticateResponse {

    private boolean authenticated;
    private boolean timedOut;

}
