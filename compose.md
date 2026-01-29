
## POM Dependencies

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-docker-compose</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>

## Start

To start the qdrant database with `docker compose`, issue this command

    $ docker compose --file compose.yaml up

With Spring Boot, add the following dependencies to the project's build

    implementation 'org.springframework.boot:spring-boot-docker-compose'
    implementation 'org.springframework.ai:spring-ai-spring-boot-docker-compose'

The first dependency add Spring Boot's support for automatically starting containers in Docker
based on the contents of the `compose.yaml` file.

The second dependency enables a service connection so that Spring AI will be properly configured 
to connect to the Qdrant database.