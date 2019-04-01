package com.asoft.ajarvis.actions;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
public class AjarvisApplication {

	private final static Logger logger = LoggerFactory.getLogger(AjarvisApplication.class);

	private static ConfigurableApplicationContext context;

	public static void main(String[] args) {
		context = SpringApplication.run(AjarvisApplication.class, args);
		logger.debug("worked ");
		logger.info("worked ");
		logger.warn("worked ");
		logger.error("worked ");
	}
}
