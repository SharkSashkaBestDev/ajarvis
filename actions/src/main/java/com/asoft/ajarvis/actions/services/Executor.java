package com.asoft.ajarvis.actions.services;

import com.asoft.ajarvis.actions.AjarvisApplication;
import com.asoft.ajarvis.actions.constant.GeneralConstants;
import com.asoft.ajarvis.actions.enities.Command;
import com.asoft.ajarvis.actions.enities.HistoryRecord;
import com.asoft.ajarvis.actions.enities.Language;
import com.asoft.ajarvis.actions.repository.CommandRepository;
import com.asoft.ajarvis.actions.repository.HistoryRepository;
import groovy.lang.GroovyShell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.asoft.ajarvis.actions.constant.GeneralConstants.*;

/**
 * This class executes some Command
 *
 * @author A.T
 * @see Command
 */
@Service
@PropertySource("classpath:servers.properties")
public class Executor {
    private static final Logger LOG = LoggerFactory.getLogger(Executor.class);

    private static final String ARG = "arg";
    private static final String RETURN = "return";
    private static final String MAP = "Map";
    private static final String CONTEXT = "context";


    @Autowired
    private CommandRepository cmdRepo;
    @Autowired
    private HistoryRepository historyRepo;

    @Autowired
    private Environment servers;


    public StringBuilder createCode(StringBuilder code, Command cmd) throws Exception {
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
                NEWLINE.concat(SPACE).concat(MAP).concat(SPACE).concat(cmd.getName()).concat(OPEN_PARENTHESIS)
                        .concat(SPACE).concat(MAP).concat(SPACE).concat(ARG).concat(SPACE).concat(CLOSE_PARENTHESIS)
                        .concat(OPEN_CURLY_BRACE).concat(NEWLINE)
        );


        if (cmd.getCode() != null) {
            if (cmd.getLanguage() != null && cmd.getLanguage() != Language.JAVA) {
                String serverAddr = servers.getProperty(cmd.getLanguage().toString());
                if (serverAddr == null) {
                    String message = String.format(
                            "Generating  code  for command %s faild :%s servers adress not found", cmd.getId(), cmd.getLanguage());
                    LOG.error(message);
                    throw new Exception(message);
                }

                code.append(
                        String.format("ArrayList<String> ids = new ArrayList<>();" +
                                        "ids.add(\"\\\"%s\\\"\");" +
                                        "HashMap<String,Object>   result = context.getBean('request').sendRequest(\"%s\",ids, arg);" +
                                        "if( result.get('error')!=null){throw new Exception(result.get('errors'))};" +
                                        "return result ",
                                cmd.getId(), serverAddr)
                );


            } else {
                code.append(cmd.getCode().concat(SPACE));
            }
        }

        code.append(invocation != null ? invocation : EMPTY);
        code.append(NEWLINE.concat(CLOSE_CURLY_BRACE).concat(SEMICOLON));

        code.insert(0, cmd.getImports() != null ? cmd.getImports() : EMPTY);

        return code;
    }

    public Object excuteCmd(final Command cmd, Map<String, Object> args) throws Exception {
        GroovyShell shell = new GroovyShell();
        shell.setVariable(ARG, args);
        shell.setVariable(CONTEXT, AjarvisApplication.getContext());
        shell.setVariable(GeneralConstants.LOG, LoggerFactory.getLogger(cmd.getId()));

        StringBuilder code = createCode(new StringBuilder(), cmd);

        return shell.run(code.toString().concat(RETURN).concat(SPACE).concat(cmd.getName())
                        .concat(OPEN_PARENTHESIS).concat(ARG).concat(CLOSE_PARENTHESIS).concat(SEMICOLON),
                "myscript.groovy", Collections.emptyList());
    }

    public Object execute(final Command cmd, Map<String, Object> args) {
        Map result = new HashMap<String, Object>();
        try {
            Map returned = (Map) excuteCmd(cmd, args);
            if (returned != null) {
                result.putAll(returned);
            }
            return result;
        } catch (Exception e) {
            LOG.error(String.format("Execution failure in %s command with error message: %s", cmd.getId(), e.getMessage()));
            result.put(ERROR, e.getMessage());
            return e;
        } finally {
            historyRepo.save(new HistoryRecord(cmd.getId(), args, result));
            LOG.info("History was updated: new record was added");
        }
    }
}
