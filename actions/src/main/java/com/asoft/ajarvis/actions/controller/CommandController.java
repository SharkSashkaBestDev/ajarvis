package com.asoft.ajarvis.actions.controller;

import com.asoft.ajarvis.actions.enities.Command;
import com.asoft.ajarvis.actions.enities.HistoryRecord;
import com.asoft.ajarvis.actions.repository.CommandRepository;
import com.asoft.ajarvis.actions.repository.HistoryRepository;
import com.asoft.ajarvis.actions.services.Executor;
import com.asoft.ajarvis.actions.services.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

import static com.asoft.ajarvis.actions.constant.GeneralConstants.EMPTY;

@RestController
@RequestMapping("/ajarvis/commands")
public class CommandController {
    private static final Logger logger = LoggerFactory.getLogger(CommandController.class);

    private static final String PHRASE = "phrase";

    @Autowired
    private CommandRepository repository;
    @Autowired
    private HistoryRepository historyRepo;
    @Autowired
    private Executor executor;
    @Autowired
    private Filter filterService;

    @GetMapping
    public Iterable<Command> getCommands() {
        logger.info("Returning list of all commands");
        return repository.findAll();
    }

    @GetMapping("/history")
    public Iterable<HistoryRecord> getHistory(@RequestParam Date dt) {
        if (dt != null) {
            logger.info("Returning history by date");
            return historyRepo.findAllByTimeGreaterThan(dt);
        }

        logger.info("Returning total history");
        return historyRepo.findAll();
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public void addCommand(@RequestBody Command command) {
        repository.save(command);
    }

    /**
     * Executes a command
     */
    @PostMapping(value = "/execute",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public Object executeCommand(@RequestBody(required = false) Map<String, Object> args) {
        String phrase = (String) args.get(PHRASE);
        args.remove(PHRASE);

        return repository.findByPhrase(phrase)
                .map(existingCommand -> executor.execute(existingCommand, args))
                .orElse(null);
    }

    /**
     * Returns generated code
     */
    @GetMapping(value = "/giveCode/{id}")
    public String getCommandGeneratedCode(
            @PathVariable("id") String id,
            @RequestBody(required = false) Map<String, Object> args) {
        return repository.findById(id)
                .map(existingCommand -> executor.createCode(new StringBuilder(), existingCommand).toString())
                .orElse(EMPTY);
    }

    @PostMapping(value = "/filter")
    public Object filter(@RequestBody Map<String, Object> args) {
        args=filterService.filter(args.get(PHRASE).toString());
        String phrase = args.get(PHRASE).toString();

        return phrase.isEmpty() ? null : args;
    }
}
