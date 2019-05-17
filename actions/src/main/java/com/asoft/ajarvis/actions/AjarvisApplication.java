package com.asoft.ajarvis.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class AjarvisApplication {
    private static final Logger logger = LoggerFactory.getLogger(AjarvisApplication.class);

    private static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        context=SpringApplication.run(AjarvisApplication.class, args);
        logger.info("AJarvis application started");

//        String command = "python3 recognition/src/main/python/main.py";
//        String command1 = "cd actions/src/main/python_server && export FLASK_APP=server.py && export FLASK_ENV=development && flask run";
//        try {
//            Process p = Runtime.getRuntime().exec(command);
//            Process ps = Runtime.getRuntime().exec(command1);
//            System.out.println("Hello");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    public static ConfigurableApplicationContext getContext() {
        return context;
    }
}
