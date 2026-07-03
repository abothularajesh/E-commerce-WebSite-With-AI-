package com.telusko.SpringEcom.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatBotService {

    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private VectorStore vectorStore;
    @Autowired
    private ChatClient chatClient;
    ChatMemory chatMemory= MessageWindowChatMemory.builder().build();


    public String getBotResponse(String conversationId,String userQuery){

        try {
            String promptTamplate= Files.readString(
                    resourceLoader.getResource("classpath:prompts/chatbot-rag-prompt.st")
                            .getFile()
                            .toPath()
            );
            String context=fetchSemanticContent(userQuery);

            Map<String,Object> values = new HashMap<>();
            values.put("context",context);
            values.put("userQuery",userQuery);

            PromptTemplate tamplate=PromptTemplate.builder()
                    .template(promptTamplate)
                    .variables(values)
                    .build();
            return chatClient.prompt(tamplate.create())
                            .advisors(a -> a.param(ChatMemory.CONVERSATION_ID,
                            conversationId))
                            .call()
                            .content();

        } catch (IOException e) {
            return "ChatBot Failed "+e.getMessage();
        }
    }

    private String fetchSemanticContent(String userQuery) {

        List<Document> documents=vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(userQuery)
                        .topK(5)
                        .similarityThreshold(0.7f)
                        .build()
        );
        StringBuilder context=new StringBuilder();
        for(Document document:documents){
            context.append(document.getFormattedContent()).append("\n");
        }
        return context.toString();
    }
}
