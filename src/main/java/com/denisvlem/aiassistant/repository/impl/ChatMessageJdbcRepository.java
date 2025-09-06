package com.denisvlem.aiassistant.repository.impl;

import com.denisvlem.aiassistant.entity.ChatMessage;
import com.denisvlem.aiassistant.entity.Role;
import com.denisvlem.aiassistant.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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

    private final RowMapper<ChatMessage> messageMapper = (rs, rowNum) ->
            ChatMessage.builder()
                    .id(rs.getLong("id"))
                    .content(rs.getString("content"))
                    .role(Role.valueOf(rs.getString("role")))
                    .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                    .chatId(rs.getLong("chat_id"))
                    .build();

    @Override
    public ChatMessage save(ChatMessage chatMessage) {
        return jdbcTemplate.queryForObject(
                "INSERT INTO chat_message(content, role, created_at, chat_id) " +
                        "VALUES (?, ?, ?, ?) " +
                        "RETURNING id, content, role, created_at, chat_id",
                messageMapper,
                chatMessage.getContent(),
                chatMessage.getRole().getName(),
                chatMessage.getCreatedAt() != null ? chatMessage.getCreatedAt() : LocalDateTime.now(),
                chatMessage.getChatId()
        );
    }

    @Override
    public List<ChatMessage> saveAll(List<ChatMessage> messages) {
        StringBuilder sql = new StringBuilder(
                "INSERT INTO chat_message (content, role, created_at, chat_id) VALUES "
        );

        ZoneOffset offset = ZonedDateTime.now(ZoneId.systemDefault()).getOffset();
        List<Object> params = new ArrayList<>();
        for (int i = 0; i < messages.size(); i++) {
            sql.append("(?, ?, ?, ?)");
            if (i < messages.size() - 1) {
                sql.append(", ");
            }

            ChatMessage m = messages.get(i);
            params.add(m.getContent());
            params.add(m.getRole().name());
            params.add(
                    m.getCreatedAt() != null
                            ? Timestamp.from(m.getCreatedAt().toInstant(offset))
                            : Timestamp.from(LocalDateTime.now().toInstant(offset))
            );
            params.add(m.getChatId());
        }

        sql.append(" RETURNING id, content, role, created_at, chat_id");

        // Run one query, map all results
        return jdbcTemplate.query(sql.toString(), messageMapper, params.toArray());
    }

    @Override
    public List<ChatMessage> findByChatIdOrder(Long id) {
        return jdbcTemplate.query(
                "SELECT * FROM chat_message WHERE chat_id = ? ORDER BY created_at",
                messageMapper,
                id
        );
    }
}
