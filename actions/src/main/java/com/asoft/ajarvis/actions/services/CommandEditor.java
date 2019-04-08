package com.asoft.ajarvis.actions.services;


import com.asoft.ajarvis.actions.enities.Command;
import com.asoft.ajarvis.actions.repository.CommandRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;


@Service
public class CommandEditor {

    @Autowired
    CommandRepository repo;


    public void createCommand(String phrase, String imports, String code, List<String> includedCommand) {
        Command cmd = null;

        if (phrase == null || (code == null && includedCommand == null)) {
            return;
        }

        String name = Translit.translit(phrase.replaceAll(" ", "_"));
        LinkedHashMap<String, Object> paramType = null;
        LinkedHashMap<String, Object> returnType = null;

        if (repo.findByPhrase(phrase).isPresent() != false)
        {
            cmd = repo.findByPhrase(phrase).get();
        }


        if (cmd == null
                && (includedCommand != null)
        ) {
            if (repo.findByUsedCommandsIds(includedCommand).isPresent() != false) {

                cmd = repo.findByUsedCommandsIds(includedCommand).get();

            } else {

                if (repo.findById(includedCommand.get(0)).isPresent()) {

                    paramType = (LinkedHashMap)repo.findById(includedCommand.get(0)).get().getParamType();
                }
                if (repo.findById(includedCommand.get(includedCommand.size() - 1)).isPresent()) {

                    returnType = (LinkedHashMap) repo.findById(includedCommand.get(includedCommand.size() - 1)).get().getReturnType();
                }


                cmd = new Command();

                cmd.setPhrase(phrase);
                cmd.setReturnType(returnType);
                cmd.setParamType(paramType);

            }


        }
        if (cmd == null) {
            cmd = new Command();
            cmd.setPhrase(phrase);

        }


        cmd.setImports(imports);
        cmd.setCode(code);
        cmd.setName(name);
        cmd.setUsedCommandsIds(includedCommand);


        if (cmd != null) {


            addReference(cmd.getId(), cmd.getUsedCommandsIds());
            repo.save(cmd);
        }


    }


    public void delete(@NonNull String Phrase) throws Exception {

        Command current = repo.findByPhrase(Phrase).get();

        if (current.getWhereUsed() == null && current.getWhereUsed().isEmpty()) {
            delReference(current.getId(), current.getUsedCommandsIds());
            repo.delete(current);
        }


    }


    public void addReference(String id, List<String> included) {
        Iterable<Command> commands = repo.findAllByIdIn(included);

        for (Command current : commands
        ) {
            current.addIntoWhereUsed(id);


        }
        repo.saveAll(commands);


    }


    public void delReference(String id, List<String> included) {
        Iterable<Command> commands = repo.findAllByIdIn(included);

        for (Command current : commands
        ) {
            current.deleteFromWhereUsed(id);


        }
        repo.saveAll(commands);


    }

}
