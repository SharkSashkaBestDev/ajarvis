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
                                 @RequestBody(required = false) Map<String,Object> args) {
        Command command = repository.findById(id).get();

       return executor.execute(command, args);

    }



    //Execute a comand
    @PostMapping(value = "/execute",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public Object executeCommand(@RequestBody( required = false) Map<String,Object> args) {
        String phrase =(String) args.get("phrase");
        args.remove("phrase");

        Command command = repository.findByPhrase(phrase).get();

        return  executor.execute(command, args);

    }



    //Return GeneratedCode
    @GetMapping(value = "/giveCode/{id}")
    public String getMApp(@PathVariable("id") String id,
                       @RequestBody(required = false) Map<String,Object> args) {


        Command command = repository.findById(id).get();

        return  executor.createCode(new StringBuilder(),command).toString();
    }



    //Only send request to pythonServer
    @PostMapping(value = "/executePy")
    public String doSome(@NonNull @RequestBody HashMap<String,Object> arg
           ) {

        String prhase =(String) arg.get("phrase");
        if(prhase==null) return null;

        System.out.println("phrase = " + prhase);
        //prhase=prhase.substring(1,prhase.length()-1);System.out.println("prhase = " + prhase);
        Request request = (Request) AjarvisApplication.context.getBean("request");
        System.out.println("request = " + request);

        ArrayList<String> ids = new ArrayList<>();
        ids.add("\""+repository.findByPhrase(prhase).get().getId()+"\"");

        System.out.println("ids = " + ids);


        arg.remove("phrase");
        Map<String, Object> stringObjectMap = request.sendRequest("http://10.241.128.77:5000/execute",ids, arg);

        System.out.println("stringObjectMap = " + stringObjectMap);
        System.out.println(" dssd= " + ids.toString());
        System.out.println("arg = " + arg);

        return stringObjectMap.toString();

    }



}
