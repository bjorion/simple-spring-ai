package com.example.boardgamebuddy.controller;

import com.example.boardgamebuddy.model.Answer;
import com.example.boardgamebuddy.model.Question;
import com.example.boardgamebuddy.tool.TimeTools;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RawController {

    private final ChatClient chatClient;

    public RawController(ChatClient.Builder chatClientBuilder) {

        this.chatClient = chatClientBuilder
                .build();
    }

    @Operation(summary = "Ask a direct question")
    @PostMapping(path = "/raw", produces = "text/plain", consumes = "text/plain")
    public String raw(@RequestBody String question) {

        return chatClient.prompt()
                .user(userSpec -> userSpec
                        .text(question))
                .call()
                .content();
    }
}
