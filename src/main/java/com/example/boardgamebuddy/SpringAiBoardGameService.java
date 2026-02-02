package com.example.boardgamebuddy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.MimeTypeUtils;

import static org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor.FILTER_EXPRESSION;
import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@Slf4j
@Service
public class SpringAiBoardGameService implements BoardGameService {

    private final ChatClient chatClient;

    @Value("classpath:/promptTemplates/translationPromptTemplate.st")
    Resource translationTemplate;

    @Value("classpath:/promptTemplates/systemPromptTemplate.st")
    Resource promptTemplate;

    public SpringAiBoardGameService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public Answer askQuestion(Question question, String conversationId) {

        log.info("askQuestion on game [{}]", question.gameTitle());

        Assert.notNull(promptTemplate, "promptTemplate must not be null");
        var gameNameMatch = buildGameNameMatch(question.gameTitle());

        var answerText = chatClient.prompt()
                .user(userSpec -> userSpec
                        .text(question.question())
                        // send a media
                        // .media(mimeType, resource)
                )
                .system(systemSpec -> systemSpec
                        .text(promptTemplate)
                        .param("gameTitle", question.gameTitle())
                )
                .advisors(advisorSpec -> advisorSpec
                        .param(FILTER_EXPRESSION, gameNameMatch)
                        .param(CONVERSATION_ID, conversationId)
                )
                // blocking calls
                .call()
                // alt: chatResponse()
                // alt: entity(record)
                .content();

        var answer = new Answer(question.gameTitle(), answerText);
        log.info("answer on game [{}]", answer.gameTitle());
        return answer;
    }

    @Override
    public String translate(Translation translation) {

        return chatClient.prompt()
                .user(userSpec -> userSpec
                        .text(translationTemplate)
                        .param("sourceText", translation.text())
                        .param("sourceLanguage", translation.sourceLanguage())
                        .param("targetLanguage", translation.targetLanguage())
                )
                .call()
                .content();
    }

    @Override
    public Answer askQuestion(Question question, Resource image, String imageContentType, String conversationId) {

        var gameNameMatch = buildGameNameMatch(question.gameTitle());
        var mediaType = MimeTypeUtils.parseMimeType(imageContentType);

        return chatClient.prompt()
                .user(userSpec -> userSpec
                        .text(question.question())
                        .media(mediaType, image))
                .system(systemSpec -> systemSpec
                        .text(promptTemplate)
                        .param("gameTitle", question.gameTitle()))
                .advisors(advisorSpec -> advisorSpec
                        .param(FILTER_EXPRESSION, gameNameMatch)
                        .param(CONVERSATION_ID, conversationId))
                .call()
                .entity(Answer.class);
    }

    private String normalizeGameTitle(String in) {
        return in.toLowerCase().replace(' ', '_');
    }

    private String buildGameNameMatch(String title) {

        return String.format("gameTitle == '%s'", normalizeGameTitle(title));
    }
}