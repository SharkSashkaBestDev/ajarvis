package com.asoft.ajarvis.actions.services;


import com.asoft.ajarvis.actions.enities.Command;
import com.asoft.ajarvis.actions.repository.CommandRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class CommandCreator {

    @Autowired
    CommandRepository repo;


    public void createCommand(String phrase, String imports, String code, List<String> includedCommand) {
        Command cmd = null;

        if (phrase == null || (code==null && includedCommand ==null)) {
            return;
        }

        String name = Translit.translit(phrase.replaceAll(" ", "_"));
        Map<String, Object> paramType = null;
        Map<String, Object> returnType = null;

        if (repo.findByPhrase(phrase).isPresent() != false) {
            cmd = repo.findByPhrase(phrase).get();
        }


        if (cmd == null
                && (includedCommand != null)
        ) {
            if (repo.findByUsedCommandsIds(includedCommand).isPresent() != false) {

                cmd = repo.findByUsedCommandsIds(includedCommand).get();

            } else {

                if (repo.findById(includedCommand.get(0)).isPresent()) {

                    paramType = repo.findById(includedCommand.get(0)).get().getParamType();
                }
                if (repo.findById(includedCommand.get(includedCommand.size() - 1)).isPresent()) {

                    returnType = repo.findById(includedCommand.get(includedCommand.size() - 1)).get().getReturnType();
                }


                cmd = new Command();

                cmd.setPhrase(phrase);
                cmd.setReturnType(returnType);
                cmd.setParamType(paramType);

            }


        }
        if (cmd==null) {
            cmd = new Command();
            cmd.setPhrase(phrase);

        }


        cmd.setImports(imports);
        cmd.setCode(code);
        cmd.setName(name);
        cmd.setUsedCommandsIds(includedCommand);


        if (cmd != null) {
            repo.save(cmd);
        }


    }


    public void delete(@NonNull String Phrase) {
        repo.delete(repo.findByPhrase(Phrase).get());

    }




}
