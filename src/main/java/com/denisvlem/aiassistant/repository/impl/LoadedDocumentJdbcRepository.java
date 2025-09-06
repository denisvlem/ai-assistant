package com.denisvlem.aiassistant.repository.impl;

import com.denisvlem.aiassistant.entity.LoadedDocument;
import com.denisvlem.aiassistant.repository.LoadedDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LoadedDocumentJdbcRepository implements LoadedDocumentRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<LoadedDocument> documentRowMapper = (rs, rowNum) ->
            LoadedDocument.builder()
                    .id(rs.getLong("id"))
                    .fileName(rs.getString("filename"))
                    .contentHash(rs.getString("content_hash"))
                    .documentType(rs.getString("document_type"))
                    .loadedAt(rs.getTimestamp("loaded_at").toLocalDateTime())
                    .build();

    @Override
    public LoadedDocument save(LoadedDocument document) {
        return jdbcTemplate.queryForObject("INSERT INTO loaded_document(" +
                        "filename, " +
                        "content_hash, " +
                        "document_type, " +
                        "chunk_count, " +
                        "loaded_at) " +
                        "VALUES(?, ?, ?, ?, now()) " +
                        "RETURNING " +
                        "id, fileName, content_hash, document_type, chunk_count, loaded_at",
                documentRowMapper,
                document.getFileName(),
                document.getContentHash(),
                document.getDocumentType(),
                document.getChunkCount()
        );
    }

    @Override
    public boolean existsByFileNameAndContentHash(String fileName, String contentHash) {
        return Boolean.TRUE.equals(
                jdbcTemplate.queryForObject("SELECT EXISTS (SELECT 1 FROM loaded_document " +
                                "WHERE filename = ? and content_hash = ?)",
                        Boolean.class,
                        fileName,
                        contentHash)
        );
    }
}
