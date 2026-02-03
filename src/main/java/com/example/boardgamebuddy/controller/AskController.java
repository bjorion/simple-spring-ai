package com.example.boardgamebuddy.controller;

import com.example.boardgamebuddy.model.Answer;
import com.example.boardgamebuddy.model.Question;
import com.example.boardgamebuddy.model.Translation;
import com.example.boardgamebuddy.service.BoardGameService;
import com.example.boardgamebuddy.service.VoiceService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class AskController {

    private static final String X_AI_CONVERSATION_ID = "X_AI_CONVERSATION_ID";
    private final BoardGameService boardGameService;
    private final VoiceService voiceService;

    public AskController(BoardGameService boardGameService, VoiceService voiceService) {

        this.boardGameService = boardGameService;
        this.voiceService = voiceService;
    }

    @Operation(summary = "Ask a written question, get a written answer")
    @PostMapping(path = "/ask", produces = "application/json")
    public Answer ask(
            @RequestHeader(name = X_AI_CONVERSATION_ID, defaultValue = "default") String conversationId,
            @RequestBody Question question) {

        return boardGameService.askQuestion(question, conversationId);
    }

    @Operation(summary = "Ask a written question, get an audio answer")
    @PostMapping(path = "/askAudioAnswer", produces = "audio/mpeg")
    public Resource askAudioAnswer(
            @RequestHeader(name = "X_AI_CONVERSATION_ID", defaultValue = "default") String conversationId,
            @RequestBody Question question) {

        var answer = boardGameService.askQuestion(question, conversationId);
        return voiceService.textToSpeech(answer.answer());
    }

    @Operation(summary = "Ask an audio question, get a written answer")
    @PostMapping(path = "/audioAsk", produces = "application/json")
    public Answer audioAsk(
            @RequestHeader(name = "X_AI_CONVERSATION_ID", defaultValue = "default") String conversationId,
            @RequestParam("audio") MultipartFile audioBlob,
            @RequestParam("gameTitle") String game) {

        var transcription = voiceService.transcribe(audioBlob.getResource());
        var transcribedQuestion = new Question(game, transcription);
        return boardGameService.askQuestion(transcribedQuestion, conversationId);
    }

    @Operation(summary = "Ask an audio question, get an audio answer")
    @PostMapping(path = "/audioAskAudioAnwser", produces = "audio/mpeg")
    public Resource audioAskAudioResponse(
            @RequestHeader(name = "X_AI_CONVERSATION_ID", defaultValue = "default") String conversationId,
            @RequestParam("audio") MultipartFile blob,
            @RequestParam("gameTitle") String game) {

        var transcription = voiceService.transcribe(blob.getResource());
        var transcribedQuestion = new Question(game, transcription);
        var answer = boardGameService.askQuestion(transcribedQuestion, conversationId);
        return voiceService.textToSpeech(answer.answer());
    }

    @Operation(summary = "Translate a given text")
    @PostMapping(path = "/translate", produces = "text/plain")
    public String translate(@RequestBody Translation request) {

        return boardGameService.translate(request);
    }

    @PostMapping(path = "/visionAsk", produces = "application/json", consumes = "multipart/form-data")
    public Answer visionAsk(
            @RequestHeader(name = "X_AI_CONVERSATION_ID", defaultValue = "default") String conversationId,
            @RequestPart("image") MultipartFile image,
            @RequestPart("gameTitle") String game,
            @RequestPart("question") String questionIn) {

        var imageResource = image.getResource();
        var imageContentType = image.getContentType();

        var question = new Question(game, questionIn);
        return boardGameService.askQuestion(question, imageResource, imageContentType, conversationId);
    }
}