package com.asoft.ajarvis.actions.services;

import com.asoft.ajarvis.actions.enities.Command;
import com.asoft.ajarvis.actions.repository.CommandRepository;

import groovy.lang.GroovyShell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;


@Service
public class Executor {

    private final static Logger logger = LoggerFactory.getLogger(Executor.class);

    @Autowired
    CommandRepository cmdRepo;


    public StringBuilder createCode(Command cmd) throws NoSuchElementException {
        StringBuilder code = new StringBuilder(),
                invocation = null;
        if (cmd.getUsedCommandsIds() != null && !cmd.getUsedCommandsIds().isEmpty()) {

            invocation = new StringBuilder();
            invocation.insert(0, "arg");

            for (String id :
                    cmd.getUsedCommandsIds()
            ) {

                Command current = cmdRepo.findById(id).get();
                code.append(createCode(current) + "\n");

                invocation.insert(0, current.getName() + "(");
                invocation.append(")");
            }



            invocation.append(";\n");
            invocation.insert(0, " return ");

        }
        code.append(" Map " + " " + cmd.getName() + "(" + " Map " + " arg ){");
        if (cmd.getCode() != null) {
            code.append(cmd.getCode() + " ");
        }
        if (invocation != null) {
            code.append(invocation);
        }

        code.append("\n};");


//        code.append("return " + cmd.getName() + "(arg);\n");


        return code;

    }


    public Object execute(Command cmd, Object args) throws NoSuchElementException {
        try {
            //Binding binding = new Binding(arg:arg);

            GroovyShell shell = new GroovyShell();
            shell.setVariable("arg", args);
            shell.setVariable("log", LoggerFactory.getLogger(cmd.getId()));

            return shell.run(createCode(cmd).toString() + "return " + cmd.getName() + "(arg);", "myscript.groovy", Collections.emptyList());
        } catch (NoSuchElementException e) {
            throw e;

        } catch (Exception e) {

            logger.error("Execute failture", e);
            return e;
        }
    }

    public Object executeTest(Command cmd, Map args) {
        try {
            //Binding binding = new Binding(arg:arg);
            GroovyShell shell = new GroovyShell();
            shell.setVariable("arg", args);

            return shell.run(cmd.getCode(), "myscript.groovy", Collections.emptyList());


        } catch (Exception e) {
            e.printStackTrace();
            return e;
        }
    }
}
