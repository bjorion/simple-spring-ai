package com.example.boardgamebuddy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImageOptionsBuilder;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Slf4j
@Service
public class SpringAiImageService implements ImageService {

    private final ImageModel imageModel;

    public SpringAiImageService(ImageModel imageModel) {
        this.imageModel = imageModel;
    }

    @Override
    public String generateImageForUrl(String instructions) {

        return generateImage(instructions, "url")
                .getResult()
                .getOutput()
                .getUrl();
    }

    @Override
    public byte[] generateImageForImageBytes(String instructions) {

        String base64Image = generateImage(instructions, "b64_json")
                .getResult()
                .getOutput()
                .getB64Json();
        return Base64.getDecoder().decode(base64Image);
    }

    private ImageResponse generateImage(String instructions, String format) {

        log.info("Image prompt instructions: {}", instructions);
        var options = ImageOptionsBuilder.builder()
                .width(1024)
                .height(1024)
                .responseFormat(format)
                .build();

        var imagePrompt = new ImagePrompt(instructions, options);
        return imageModel.call(imagePrompt);
    }
}