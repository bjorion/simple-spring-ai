package com.example.boardgamebuddy.gamedata;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Slf4j
@Component("gameComplexityFunction")
@Description("Returns a game's complexity/difficulty " +
        "given the game's title/name.")
public class GameComplexityFunction
        implements Function<GameComplexityRequest, GameComplexityResponse> {

    private final GameRepository gameRepository;

    GameComplexityFunction(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public GameComplexityResponse apply(GameComplexityRequest gameDataRequest) {

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

        return new GameComplexityResponse(
                game.title(), game.complexityEnum());

    }
}