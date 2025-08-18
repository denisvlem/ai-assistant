CREATE TABLE public.chat
(
    id         BIGSERIAL PRIMARY KEY,
    title      VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE public.chat_message
(
    id         BIGSERIAL PRIMARY KEY,
    content    TEXT        NOT NULL,
    role       VARCHAR(50) NOT NULL,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    chat_id    BIGINT      NOT NULL,
    CONSTRAINT fk_chat FOREIGN KEY (chat_id) REFERENCES chat (id) ON DELETE CASCADE
);