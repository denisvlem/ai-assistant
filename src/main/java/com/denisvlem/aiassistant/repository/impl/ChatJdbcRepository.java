package com.denisvlem.aiassistant.repository.impl;

import com.denisvlem.aiassistant.entity.Chat;
import com.denisvlem.aiassistant.repository.ChatRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ChatJdbcRepository implements ChatRepository {

    private final JdbcTemplate jdbcTemplate;

    public ChatJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Chat> findAll() {
        return jdbcTemplate.query("SELECT * FROM chat ORDER BY created_at DESC", chatMapper);
    }

    @Override
    public Chat findById(Long id) {
        return jdbcTemplate.queryForObject("SELECT * FROM chat WHERE id = ?", chatMapper, id);
    }

    @Override
    public Chat save(Chat chat) {
        return jdbcTemplate.queryForObject(
                "INSERT INTO chat(title, created_at) VALUES (?, ?) RETURNING id, title, created_at",
                chatMapper,
                chat.getTitle(),
                chat.getCreatedAt() != null ? chat.getCreatedAt() : LocalDateTime.now()
        );
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM chat WHERE id = ?", id);
    }

    private final RowMapper<Chat> chatMapper = (rs, rowNum) ->
            Chat.builder()
                    .id(rs.getLong("id"))
                    .title(rs.getString("title"))
                    .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                    .build();

}
