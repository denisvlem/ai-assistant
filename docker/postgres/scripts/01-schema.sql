CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS public.chat
(
    id         BIGSERIAL PRIMARY KEY,
    title      VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS public.chat_message
(
    id         BIGSERIAL PRIMARY KEY,
    content    TEXT        NOT NULL,
    role       VARCHAR(50) NOT NULL,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    chat_id    BIGINT      NOT NULL,
    CONSTRAINT fk_chat FOREIGN KEY (chat_id) REFERENCES chat (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS loaded_document
(
    id            SERIAL PRIMARY KEY,
    filename      VARCHAR(256) NOT NULL,
    content_hash  VARCHAR(64)  NOT NULL,
    document_type VARCHAR(10)  NOT NULL,
    chunk_count   INTEGER,
    loaded_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT unique_document UNIQUE (filename, content_hash)
);

CREATE INDEX IF NOT EXISTS idx_loaded_documents_filename ON loaded_document (filename);


CREATE TABLE IF NOT EXISTS vector_store
(
    id        VARCHAR(256) PRIMARY KEY,
    content   TEXT,
    metadata  JSON,
    embedding VECTOR(1024)
);

CREATE INDEX IF NOT EXISTS vector_store_hnsw_index ON
    vector_store USING hnsw (embedding vector_cosine_ops)