package com.denisvlem.aiassistant.service;

import com.denisvlem.aiassistant.entity.LoadedDocument;
import com.denisvlem.aiassistant.repository.LoadedDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentLoaderService {

    private final LoadedDocumentRepository loadedDocumentRepository;
    private final ResourcePatternResolver resourcePatternResolver;
    private final VectorStore vectorStore;
    private final TransactionTemplate tx;

    @EventListener(ApplicationStartedEvent.class)
    @SneakyThrows
    private void loadDocuments() {
        String locationPattern = "classpath:/knowledgebase/**/*.txt";
        getResources(locationPattern).stream()
                .map(resource -> new ResourceHashHolder(resource, calculateContentHash(resource)))
                .filter(resourceHolder ->
                        !loadedDocumentRepository.existsByFileNameAndContentHash(
                                resourceHolder.resource().getFilename(),
                                resourceHolder.hash()))
                .forEach(resourceHolder -> {
                    List<Document> chunks = chunkDocument(resourceHolder);
                    vectorStore.accept(chunks);
                    saveDocument(resourceHolder.resource, resourceHolder.hash(), chunks.size());
                });
    }

    private List<Resource> getResources(String locationPattern) {
        try {
            return Arrays.stream(resourcePatternResolver.getResources(locationPattern)).toList();
        } catch (IOException e) {
            log.warn("Couldn't load knowledge base resources: [{}]. Prompts won't be using RAG",
                    e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<Document> chunkDocument(ResourceHashHolder resourceHolder) {
        List<Document> documents = new TextReader(resourceHolder.resource()).get();
        TokenTextSplitter splitter = TokenTextSplitter.builder().withChunkSize(500)
                .build();
        return splitter.apply(documents);
    }

    private void saveDocument(Resource resource, String hash, int chunkCount) {
        tx.executeWithoutResult(status -> loadedDocumentRepository.save(
                LoadedDocument.builder()
                        .fileName(resource.getFilename())
                        .contentHash(hash)
                        .documentType("txt")
                        .chunkCount(chunkCount)
                        .build()
        ));
    }

    @SneakyThrows
    private String calculateContentHash(Resource resource) {
        return DigestUtils.md5DigestAsHex(resource.getInputStream());
    }

    record ResourceHashHolder(Resource resource, String hash) {
    }

}
