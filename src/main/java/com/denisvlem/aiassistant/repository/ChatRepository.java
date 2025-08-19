package com.denisvlem.aiassistant.repository;

import com.denisvlem.aiassistant.entity.Chat;

import java.util.List;

public interface ChatRepository {

    List<Chat> findAll();
    Chat findById(Long id);
    Chat save(Chat chat);
    void delete(Long id);
}
