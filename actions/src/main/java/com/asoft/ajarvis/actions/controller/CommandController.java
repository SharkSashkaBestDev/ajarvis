package com.asoft.ajarvis.actions.controller;


import com.asoft.ajarvis.actions.enities.Command;
import com.asoft.ajarvis.actions.repository.CommandRepository;
import com.asoft.ajarvis.actions.services.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ajarvis/commands")
public class CommandController {

    private final static Logger logger = LoggerFactory.getLogger(CommandController.class);

    @Autowired
    private CommandRepository repository;

    @Autowired
    Executor executor;

    @GetMapping
    public Iterable<Command> getCommands() {
        logger.info("Returned ");
        return repository.findAll();
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public void addCommand(@RequestBody Command command) {
        repository.save(command);
    }

    @PostMapping(value = "/execute/{id}",
                    consumes = MediaType.APPLICATION_JSON_VALUE,
                    produces = MediaType.APPLICATION_JSON_VALUE
                )
    public Object executeCommandById(@PathVariable("id") String id,
                                 @RequestBody(required = false) Map<String,Object> args) {
        Command command = repository.findById(id).get();

       return executor.execute(command, args);

    }


    @PostMapping(value = "/execute",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Object executeCommand(@RequestParam(value = "phrase") String phrase,
                                             @RequestBody( required = false) Map<String,Object> args
                                             ) {
        Command command = repository.findByPhrase(phrase).get();

        return  executor.execute(command, args);

    }


    @GetMapping(value = "/giveCode/{id}")
    public String getMApp(@PathVariable("id") String id,
                       @RequestBody(required = false) Map<String,Object> args) {


        Command command = repository.findById(id).get();

        return  executor.createCode(command).toString();
    }
}
