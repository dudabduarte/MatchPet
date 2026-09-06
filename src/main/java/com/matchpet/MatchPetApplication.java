package com.matchpet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MatchPetApplication {

    public static void main(String[] args) {
        try (var context = SpringApplication.run(MatchPetApplication.class, args)) {
            // A CLI terminou; fecha também a conexão da aplicação com o banco.
        }
    }
}
