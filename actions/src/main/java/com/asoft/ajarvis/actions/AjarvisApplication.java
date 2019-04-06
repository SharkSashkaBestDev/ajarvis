package com.asoft.ajarvis.actions;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
public class AjarvisApplication {

    private final static Logger logger = LoggerFactory.getLogger(AjarvisApplication.class);

    public static ConfigurableApplicationContext context;


    public static void main(String[] args) {
        context = SpringApplication.run(AjarvisApplication.class, args);
        logger.debug("worked ");
        logger.info("worked ");
        logger.warn("worked ");
        logger.error("worked ");

//





    }


//	@EventListener(ApplicationReadyEvent.class)
//	public void doSomethingAfterStartup() {
//		CommandRepository commandRepository =(CommandRepository)context.getBean("commandRepository");
//
//		commandRepository.save(new Command("click", "py", "test", Language.PYTHON));
//	}
}
