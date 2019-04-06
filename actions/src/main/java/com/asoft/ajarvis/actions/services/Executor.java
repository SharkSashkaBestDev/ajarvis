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

import java.util.Collections;
import java.util.HashMap;
import java.util.NoSuchElementException;


@Service
public class Executor {

    private final static Logger logger = LoggerFactory.getLogger(Executor.class);

    @Autowired
    CommandRepository cmdRepo;

    @Autowired
    HistoryRepository historyRepo;

private static HashMap<Language,String> servers=new HashMap<Language,String>();
    static {
        servers.put(Language.PYTHON,"http://10.241.128.77:5000/execute");
    }


    public StringBuilder createCode(StringBuilder code ,Command cmd) throws NoSuchElementException {
        StringBuilder
                invocation = null;
        if (cmd.getUsedCommandsIds() != null && !cmd.getUsedCommandsIds().isEmpty()) {

            invocation = new StringBuilder();
            invocation.insert(0, "arg");

            for (String id :
                    cmd.getUsedCommandsIds()
            ) {

                Command current = cmdRepo.findById(id).get();
                createCode(code ,current);

                invocation.insert(0, current.getName() + "(");
                invocation.append(")");
            }


            invocation.append(";\n");
            invocation.insert(0, " return ");

        }
        code.append("\n Map " + " " + cmd.getName() + "(" + " Map " + " arg ){\n");
        if (cmd.getCode() != null) {
            if (cmd.getLanguage() != null) {
                switch (cmd.getLanguage()) {
                    case PYTHON:
                        code.append("ArrayList<String> ids = new ArrayList<>();ids.add(\""+cmd.getId()+"\");return context.getBean('request').sendRequest(\""+servers.get(Language.PYTHON)+"\",ids, arg);");
                        break;
                    case JAVA:
                        code.append(cmd.getCode() + " ");
                        break;

                }
            } else {
                code.append(cmd.getCode() + " ");

            }


        }

        if (invocation != null) {
            code.append(invocation);
        }

        code.append("\n};");




        if (cmd.getImports() != null) {
            code.insert(0,cmd.getImports());
        }
        return code;

    }


    public Object execute(final Command cmd, Object args) throws NoSuchElementException {
        try {


            GroovyShell shell = new GroovyShell();
            shell.setVariable("arg", args);
            shell.setVariable("context", AjarvisApplication.context);
            shell.setVariable("log", LoggerFactory.getLogger(cmd.getId()));



            return shell.run(createCode(new StringBuilder(),cmd).toString() + "return " + cmd.getName() + "(arg);", "myscript.groovy", Collections.emptyList());
        } catch (Exception e) {

            logger.error("Execute failture in "+cmd.getId()+"command with :", e);
            return e;
        }
        finally {

            historyRepo.save( new HistoryRecord(cmd.getId()));
            logger.info("History was updated : record was added");

        }
    }

}
