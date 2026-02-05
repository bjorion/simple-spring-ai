package com.example.boardgamebuddy.config;

import com.example.boardgamebuddy.gamedata.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor;

import java.util.List;
import java.util.function.Function;

@Slf4j
@Configuration
public class AiConfig {

    @Bean
    ChatClient chatClient(ChatClient.Builder chatClientBuilder) {

        var safeGuardAdvisor = SafeGuardAdvisor.builder()
                .sensitiveWords(List.of("Uno", "uno", "UNO"))
                .failureResponse("Sorry, this subject is taboo")
                .build();

        var chatOptions = ChatOptions.builder()
                .temperature(0.7)
                .build();

        return chatClientBuilder
                .defaultAdvisors(
                        // MessageChatMemoryAdvisor.builder(chatMemory).build()
                        safeGuardAdvisor,
                        new SimpleLoggerAdvisor()
                )
                .defaultOptions(chatOptions)
                // .defaultToolNames("gameComplexityFunction")
                .build();
    }

    @Bean
    RestClientCustomizer logbookCustomizer(LogbookClientHttpRequestInterceptor interceptor) {

        return restClient -> restClient.requestInterceptor(interceptor);
    }

    @SuppressWarnings("unused")
    @Description("Returns a game's complexity/difficulty given the game's title/name.")
    Function<GameComplexityRequest, GameComplexityResponse> gameComplexityFunction(
            GameRepository gameRepository) {

        return gameDataRequest -> {
            var gameSlug = gameDataRequest.title()
                    .toLowerCase()
                    .replace(" ", "_");

            log.info("Getting complexity for {} ({})",
                    gameDataRequest.title(), gameSlug);

            var gameOpt = gameRepository.findBySlug(gameSlug);

            var game = gameOpt.orElseGet(() -> {
                log.warn("Game not found: {}", gameSlug);
                return new Game(
                        null,
                        gameSlug,
                        gameDataRequest.title(),
                        GameComplexity.UNKNOWN.getValue());
            });

            return new GameComplexityResponse(game.title(), game.complexityEnum());
        };
    }
}