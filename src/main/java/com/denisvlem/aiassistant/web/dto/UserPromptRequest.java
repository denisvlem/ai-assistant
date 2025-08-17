package com.denisvlem.aiassistant.web.dto;


import jakarta.validation.constraints.NotNull;

public record UserPromptRequest(@NotNull String prompt) {
}
