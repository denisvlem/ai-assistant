package com.denisvlem.aiassistant.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chat {

    private Long id;
    private String title;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
