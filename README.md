# Spring AI in Action

POC for using Spring AI

## External Links

- OpenAI: https://platform.openai.com/settings
- Code Source: https://github.com/habuma/spring-ai-in-action-samples
- Reference: https://docs.spring.io/spring-ai/reference/

## Internal Links

- Actuator: http://localhost:8080/actuator 
- Swagger: http://localhost:8080/api/swagger-ui
- Prometheus: http://localhost:9090 (if enabled)

## Configuration

### OPEN AI key

Define a variable called `OPENAI_API_KEY` as environment variable

- OPENAI_API_KEY = sk-...cmwA

### With DOCKER

- Start UBUNTU
- Start DOCKER DESKTOP

### Without Docker

Comment out in pom.xml the following dependencies:

- org.springframework.boot:spring-boot-docker-compose
- org.springframework.boot:spring-ai-spring-boot-docker-compose

### Examples

http://localhost:8080/askAudioAnswer (POST)
accept:audio/mpeg

        {
            "gameTitle": "Time's Up",
            "question": "describe the rules"
        }

http://localhost:8080/burgerBattleArt?burger=Sunrise (GET)
accept:image/png