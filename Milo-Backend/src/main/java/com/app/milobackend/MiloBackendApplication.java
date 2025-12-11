package com.app.milobackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MiloBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiloBackendApplication.class, args);
    }

}
