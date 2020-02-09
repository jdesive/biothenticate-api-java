package com.softwarfare.biothenticate.api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
public class User {

    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private AccountRole role;
    private String password;

    @JsonIgnore
    @ToString.Exclude
    private String username;

    @JsonIgnore
    @ToString.Exclude
    private ObjectNode tenant;

    private boolean bypass;
    private boolean enrolled;
    private boolean inactive;
    private boolean local;
    private boolean lockedOut;
    private boolean registered;

}
