package com.example.boardgamebuddy.service;

public interface ImageService {

    String generateImageForUrl(String instructions);

    byte[] generateImageForImageBytes(String instructions);

}