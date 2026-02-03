package com.example.boardgamebuddy.service;

import com.example.boardgamebuddy.model.Question;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;

@EnableWireMock(
        @ConfigureWireMock(baseUrlProperties = "openai.base.url"))
@SpringBootTest(
        properties = "spring.ai.openai.base-url=${openai.base.url}")
public class SpringAiBoardGameServiceWireMockTests {

    @Value("classpath:/test-openai-response.json")
    Resource responseResource;

    @Value("classpath:/promptTemplates/systemPromptTemplate.st")
    Resource promptTemplate;

    @Autowired
    ChatClient.Builder chatClientBuilder;

    @BeforeEach
    public void setup() throws IOException {

        var cannedResponse = responseResource.getContentAsString(Charset.defaultCharset());
        var mapper = new ObjectMapper();
        var responseNode = mapper.readTree(cannedResponse);

        WireMock.stubFor(WireMock.post("/v1/chat/completions")
                .willReturn(ResponseDefinitionBuilder.okForJson(responseNode)));
    }

    @Test
    public void askQuestion_ok() {

        var question = new Question("Geography", "What is the capital of France?");
        var boardGameService = new SpringAiBoardGameService(chatClientBuilder.build());
        boardGameService.promptTemplate = promptTemplate;

        var answer = boardGameService.askQuestion(question, "conversationId");

        assertThat(answer).isNotNull();
        assertThat(answer.answer()).isEqualTo("Paris");
    }
}