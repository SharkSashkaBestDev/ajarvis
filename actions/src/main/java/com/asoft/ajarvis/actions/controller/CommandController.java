package com.asoft.ajarvis.actions.controller;


import com.asoft.ajarvis.actions.AjarvisApplication;
import com.asoft.ajarvis.actions.enities.Command;
import com.asoft.ajarvis.actions.enities.HistoryRecord;
import com.asoft.ajarvis.actions.repository.CommandRepository;
import com.asoft.ajarvis.actions.repository.HistoryRepository;
import com.asoft.ajarvis.actions.services.Executor;
import com.asoft.ajarvis.actions.services.Request;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/ajarvis/commands")
public class CommandController {

    private final static Logger logger = LoggerFactory.getLogger(CommandController.class);

    @Autowired
    private CommandRepository repository;

    @Autowired
    HistoryRepository historyRepo;

    @Autowired
    Executor executor;

    @GetMapping
    public Iterable<Command> getCommands() {
        logger.info("Returned  command ");
        return repository.findAll();
    }

    @GetMapping("/history")
    public Iterable<HistoryRecord> getHistoty() {
        logger.info("Returned history  ");
        return historyRepo.findAll();
    }

    @GetMapping(value = "/historyi")
    public Iterable<HistoryRecord> getHistotyri(@RequestParam Date dt) {
        logger.info("Returned history  ");
        return historyRepo.findAllByTimeGreaterThan(dt);
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
                                     @RequestBody(required = false) Map<String, Object> args) {
        Command command = repository.findById(id).get();

        return executor.execute(command, args);

    }


    //Execute a comand
    @PostMapping(value = "/execute",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public Object executeCommand(@RequestBody(required = false) Map<String, Object> args) {
        String phrase = (String) args.get("phrase");
        args.remove("phrase");

        Command command = repository.findByPhrase(phrase).get();

        return executor.execute(command, args);

    }


    //Return GeneratedCode
    @GetMapping(value = "/giveCode/{id}")
    public String getMApp(
            @PathVariable("id") String id,
            @RequestBody(required = false) Map<String, Object> args) {


        Command command = repository.findById(id).get();

        return executor.createCode(new StringBuilder(), command).toString();
    }





}
