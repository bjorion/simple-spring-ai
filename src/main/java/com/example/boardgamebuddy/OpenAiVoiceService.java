package com.example.boardgamebuddy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OpenAiVoiceService implements VoiceService {

    private final OpenAiAudioTranscriptionModel transcriptionModel;
    private final OpenAiAudioSpeechModel speechModel;

    public OpenAiVoiceService(
            OpenAiAudioTranscriptionModel transcriptionModel,
            OpenAiAudioSpeechModel speechModel) {

        this.transcriptionModel = transcriptionModel;
        this.speechModel = speechModel;
    }

    @Override
    public String transcribe(Resource audioFileResource) {

        log.info("transcribe");
        return transcriptionModel.call(audioFileResource);
    }

    @Override
    public Resource textToSpeech(String text) {

        log.info("textToSpeech");
        var speechBytes = speechModel.call(text);
        log.info("Got response from speech, length: {}", speechBytes.length);
        return new ByteArrayResource(speechBytes);
    }
}
