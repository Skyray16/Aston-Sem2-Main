package com.skyray16.userservice.event;

public record UserEvent(
    String email,
    String operation // "CREATED" или "DELETED"
) {}