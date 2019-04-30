package com.asoft.ajarvis.actions.services;

import com.asoft.ajarvis.actions.AjarvisApplication;
import com.asoft.ajarvis.actions.enities.Command;
import com.asoft.ajarvis.actions.enities.HistoryRecord;
import com.asoft.ajarvis.actions.enities.Language;
import com.asoft.ajarvis.actions.repository.CommandRepository;
import com.asoft.ajarvis.actions.repository.HistoryRepository;
import groovy.lang.GroovyShell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.asoft.ajarvis.actions.constant.GeneralConstants.*;

/**
 *This class executes some Command
 *
 * @see Command
 * @author A.T
 */
@Service
public class Executor {
    private static final Logger logger = LoggerFactory.getLogger(Executor.class);

    private static final String ARG = "arg";
    private static final String RETURN = "return";
    private static final String MAP = "Map";
    private static final String CONTEXT = "context";
    private static final String LOG = "log";

    @Autowired
    private CommandRepository cmdRepo;
    @Autowired
    private HistoryRepository historyRepo;

    private static HashMap<Language, String> servers=new HashMap<>();

    static {
        servers.put(Language.PYTHON, "http://10.241.129.11:5000/execute");
    }

    public StringBuilder createCode(StringBuilder code, Command cmd) {
        StringBuilder invocation = null;

        if (cmd.getUsedCommandsIds() != null && !cmd.getUsedCommandsIds().isEmpty()) {
            invocation = new StringBuilder().insert(0, ARG);

            for (String id : cmd.getUsedCommandsIds()) {
                Optional<Command> command = cmdRepo.findById(id);
                if (command.isPresent()) {
                    Command existingCommand = command.get();
                    createCode(code, existingCommand);

                    invocation.insert(0, existingCommand.getName() + OPEN_PARENTHESIS);
                    invocation.append(CLOSE_PARENTHESIS);
                }
            }

            invocation.append(SEMICOLON.concat(NEWLINE));
            invocation.insert(0, SPACE.concat(RETURN).concat(SPACE));
        }

        code.append(
            NEWLINE.concat(SPACE).concat(MAP).concat(SPACE).concat(cmd.getName()).concat(CLOSE_PARENTHESIS)
                .concat(SPACE).concat(MAP).concat(SPACE).concat(ARG).concat(SPACE).concat(CLOSE_PARENTHESIS)
                .concat(OPEN_CURLY_BRACE).concat(NEWLINE)
        );

        //TODO: reorganize to more readable code
        if (cmd.getCode() != null) {
            if (cmd.getLanguage() != null) {
                switch (cmd.getLanguage()) {
                    case PYTHON:
                        code.append(
                            String.format("ArrayList<String> ids = new ArrayList<>();" +
                                "ids.add(\"\\\"%s\\\"\");" +
                                "return context.getBean('request').sendRequest(\"%s\",ids, arg);",
                                    cmd.getId(), servers.get(Language.PYTHON))
                        );
                        break;
                    case JAVA:
                        code.append(cmd.getCode().concat(SPACE));
                        break;
                    default:
                        break;
                }
            } else {
                code.append(cmd.getCode().concat(SPACE));
            }
        }

        code.append(invocation != null ? invocation : EMPTY);
        code.append(NEWLINE.concat(CLOSE_CURLY_BRACE).concat(SEMICOLON));

        code.insert(0, cmd.getImports() != null ? cmd.getImports() : EMPTY);

        return code;
    }

    public Object excuteCmd(final Command cmd, Map<String, Object>  args){
        GroovyShell shell = new GroovyShell();
        shell.setVariable(ARG, args);
        shell.setVariable(CONTEXT, AjarvisApplication.getContext());
        shell.setVariable(LOG, LoggerFactory.getLogger(cmd.getId()));

        StringBuilder code = createCode(new StringBuilder(), cmd);

        return shell.run(code.toString().concat(RETURN).concat(SPACE).concat(cmd.getName())
                        .concat(OPEN_PARENTHESIS).concat(ARG).concat(CLOSE_PARENTHESIS).concat(SEMICOLON),
                "myscript.groovy", Collections.emptyList());
    }

    public Object execute(final Command cmd, Map<String, Object>  args) {
        try {
            return excuteCmd(cmd,args);
        } catch (Exception e) {
            logger.error(String.format("Execution failure in %s command with error message: %s", cmd.getId(), e.getMessage()));
            return e;
        } finally {
            historyRepo.save( new HistoryRecord(cmd.getId(),args));
            logger.info("History was updated: new record was added");
        }
    }
}
