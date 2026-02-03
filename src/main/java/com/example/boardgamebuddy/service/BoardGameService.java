package com.example.boardgamebuddy.service;

import com.example.boardgamebuddy.model.Answer;
import com.example.boardgamebuddy.model.Question;
import com.example.boardgamebuddy.model.Translation;
import org.springframework.core.io.Resource;

public interface BoardGameService {

    Answer askQuestion(Question question, String conversationId);

    Answer askQuestion(Question question, Resource image, String imageContentType, String conversationId);

    String translate(Translation translation);
}