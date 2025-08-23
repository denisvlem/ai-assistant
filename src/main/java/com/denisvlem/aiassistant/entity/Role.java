package com.denisvlem.aiassistant.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
@Slf4j
public enum Role {

    USER("user"),
    ASSISTANT("assistant"),
    SYSTEM("system");

    private final String name;

    public static Role getRole(String roleName) {
        return Arrays.stream(values())
                .filter(role -> role.name.equals(roleName))
                .findFirst().orElseThrow();
    }
}
