package com.denisvlem.aiassistant.repository;

import com.denisvlem.aiassistant.entity.LoadedDocument;

public interface LoadedDocumentRepository {

    LoadedDocument save(LoadedDocument document);

    boolean existsByFileNameAndContentHash(String fileName, String contentHash);

}
