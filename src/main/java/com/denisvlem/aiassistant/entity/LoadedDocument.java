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
public class LoadedDocument {

    private Long id;
    private String fileName;
    private String contentHash;
    private String documentType;
    private LocalDateTime loadedAt = LocalDateTime.now();
    private Integer chunkCount;
}
