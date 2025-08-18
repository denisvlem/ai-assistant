package com.denisvlem.aiassistant.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum Role {

    USER("assistant"),
    ASSISTANT("assistant"),
    SYSTEM("system");

    private final String name;

    public static Role getRole(String roleName) {

        return Arrays.stream(values())
                .filter(role -> role.name.equals(roleName))
                .findFirst().orElseThrow();
    }
}
