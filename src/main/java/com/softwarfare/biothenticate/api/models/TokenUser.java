package com.softwarfare.biothenticate.api.models;

import lombok.Data;

import java.util.UUID;

@Data
public class TokenUser {

    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private SubscriptionType subscriptionType;
    private UUID tenantId;
    private AccountRole role;
    private boolean enrolled;
    private boolean inactive;
    private String sub;
    private long iat;
    private long exp;

}
