package com.softwarfare.biothenticate.api;

import com.softwarfare.biothenticate.api.models.AuthenticateResponse;
import com.softwarfare.biothenticate.api.models.AuthenticationType;
import com.softwarfare.biothenticate.api.models.User;
import com.softwarfare.biothenticate.api.models.UserFilter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
public class TestApp {

    public static void main(String[] args) {
        try {
            BioThenticateClient bioThenticateClient = new BioThenticateClient("brandon@softwarfare.com", "Jayhawks2020!").setConnectionTimeout(32);
            AuthenticateResponse response = bioThenticateClient.authenticate(AuthenticationType.IRIS, "brandon@softwarfare.com", "Test", "Test", "Test");
            System.out.println(response.isAuthenticated());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
