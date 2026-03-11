package org.example.progra4proyecto1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class Progra4Proyecto1Application extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Progra4Proyecto1Application.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Progra4Proyecto1Application.class, args);
    }
}
