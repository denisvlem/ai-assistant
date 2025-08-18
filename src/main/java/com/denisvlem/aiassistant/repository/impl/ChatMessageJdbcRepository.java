package com.denisvlem.aiassistant.repository.impl;

import com.denisvlem.aiassistant.entity.ChatMessage;
import com.denisvlem.aiassistant.entity.Role;
import com.denisvlem.aiassistant.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatMessageJdbcRepository implements ChatMessageRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<ChatMessage> findByChatId(Long id) {
        return jdbcTemplate.query(
                "SELECT * FROM chat_message WHERE chat_id = ? ORDER BY created_at",
                messageMapper,
                id
        );
    }

    @Override
    public void save(ChatMessage chatMessage) {
        jdbcTemplate.update(
                "INSERT INTO chat_message(content, role, created_at, chat_id) VALUES (?, ?, ?, ?)",
                chatMessage.getContent(),
                chatMessage.getRole().getName(),
                chatMessage.getCreatedAt() != null ? chatMessage.getCreatedAt() : LocalDateTime.now(),
                chatMessage.getChatId()
        );
    }

    private final RowMapper<ChatMessage> messageMapper = (rs, rowNum) ->
            ChatMessage.builder()
                    .id(rs.getLong("id"))
                    .content(rs.getString("content"))
                    .role(Role.getRole(rs.getString("role")))
                    .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                    .chatId(rs.getLong("chat_id"))
                    .build();
}
