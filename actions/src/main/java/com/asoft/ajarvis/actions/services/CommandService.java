package com.asoft.ajarvis.actions.services;

import com.asoft.ajarvis.actions.enities.Command;
import com.asoft.ajarvis.actions.repository.CommandRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static com.asoft.ajarvis.actions.constant.GeneralConstants.SPACE;
import static com.asoft.ajarvis.actions.constant.GeneralConstants.UNDERSCORE;

@Service
public class CommandService {
    @Autowired
    private CommandRepository repo;

    public void createCommand(String phrase, String imports, String code, List<String> includedCommand) {
        Command cmd = null;

        if (phrase == null || (code == null && includedCommand == null)) {
            return;
        }

        phrase = phrase.toLowerCase();

        String name = Translit.translit(phrase.replaceAll(SPACE, UNDERSCORE));
        LinkedHashMap<String, Object> paramType = null;
        LinkedHashMap<String, Object> returnType = null;

        if (repo.findByPhrase(phrase).isPresent()) {
            cmd = repo.findByPhrase(phrase).get();
        }

        if (cmd == null && includedCommand != null) {
            if (repo.findByUsedCommandsIds(includedCommand).isPresent()) {
                cmd = repo.findByUsedCommandsIds(includedCommand).get();
            } else {
                if (repo.findById(includedCommand.get(0)).isPresent()) {
                    paramType = (LinkedHashMap<String, Object>) repo.findById(includedCommand.get(0)).get().getParamType();
                }
                if (repo.findById(includedCommand.get(includedCommand.size() - 1)).isPresent()) {
                    returnType = (LinkedHashMap<String, Object>) repo.findById(includedCommand.get(includedCommand.size() - 1)).get().getReturnType();
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

        addReference(cmd.getId(), cmd.getUsedCommandsIds());
        repo.save(cmd);
    }

    public void delete(@NonNull String phrase) {
        Optional<Command> command = repo.findByPhrase(phrase);

        if (command.isPresent()) {
            Command existingCommand = command.get();

            if (existingCommand.getWhereUsed() == null || existingCommand.getWhereUsed().isEmpty()) {
                delReference(existingCommand.getId(), existingCommand.getUsedCommandsIds());
                repo.delete(existingCommand);
            }
        }
    }

    public void addReference(String id, List<String> included) {
        Iterable<Command> commands = repo.findAllByIdIn(included);

        commands.forEach(command -> command.addWhereUsed(id));

        repo.saveAll(commands);
    }

    public void delReference(String id, List<String> included) {
        Iterable<Command> commands = repo.findAllByIdIn(included);

        commands.forEach(command -> command.deleteFromWhereUsed(id));

        repo.saveAll(commands);
    }
}
