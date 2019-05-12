package com.asoft.ajarvis.actions.services;

import com.asoft.ajarvis.actions.enities.Command;
import com.asoft.ajarvis.actions.repository.CommandRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.asoft.ajarvis.actions.constant.GeneralConstants.SPACE;
import static com.asoft.ajarvis.actions.constant.GeneralConstants.UNDERSCORE;

@Service
public class CommandService {

    @Autowired
    private CommandRepository commandRepository;

    public void createCommand(String phrase, String imports, String code, List<String> includedCommand) {
        Command cmd = null;

        if (phrase == null || (code == null && includedCommand == null)) {
            return;
        }

        phrase = phrase.toLowerCase();

        String name = Translit.translit(phrase.replaceAll(SPACE, UNDERSCORE));
        LinkedHashMap<String, Object> paramType = null;
        LinkedHashMap<String, Object> returnType = null;

        if (commandRepository.findByPhrase(phrase).isPresent()) {
            cmd = commandRepository.findByPhrase(phrase).get();
        }

        if (cmd == null && includedCommand != null) {
            if (commandRepository.findByUsedCommandsIds(includedCommand).isPresent()) {
                cmd = commandRepository.findByUsedCommandsIds(includedCommand).get();
            } else {
                if (commandRepository.findById(includedCommand.get(0)).isPresent()) {
                    paramType = (LinkedHashMap<String, Object>) commandRepository.findById(includedCommand.get(0)).get().getParamType();
                }
                if (commandRepository.findById(includedCommand.get(includedCommand.size() - 1)).isPresent()) {
                    returnType = (LinkedHashMap<String, Object>) commandRepository.findById(includedCommand.get(includedCommand.size() - 1)).get().getReturnType();
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
        commandRepository.save(cmd);
    }

    public void delete(@NonNull String phrase) {
        Optional<Command> command = commandRepository.findByPhrase(phrase);

        if (command.isPresent()) {
            Command existingCommand = command.get();

            if (existingCommand.getWhereUsed() == null || existingCommand.getWhereUsed().isEmpty()) {
                delReference(existingCommand.getId(), existingCommand.getUsedCommandsIds());
                commandRepository.delete(existingCommand);
            }
        }
    }

    public void addReference(String id, List<String> included) {
        Iterable<Command> commands = commandRepository.findAllByIdIn(included);

        commands.forEach(command -> command.addWhereUsed(id));

        commandRepository.saveAll(commands);
    }

    public void delReference(String id, List<String> included) {
        Iterable<Command> commands = commandRepository.findAllByIdIn(included);

        commands.forEach(command -> command.deleteFromWhereUsed(id));

        commandRepository.saveAll(commands);
    }

    public List<String> getIdFromPhrase(List<String> included) {
        Iterable<Command> commands = commandRepository.findAllByPhraseIn(included);
        List<String> result = new ArrayList<>();
        commands.forEach((s) -> result.add(s.getId()));
        return result;
    }

    public List<String> validationCheck(List<String> included) throws Exception {
        ArrayList<Command> commands = (ArrayList<Command>) commandRepository.findAllByIdIn(included);
        Map<String, Object> args = commands.get(0).getParamType();

        for (int i = 0; i < commands.size() - 1; i++) {
            Command firstCommand = commands.get(i);
            Command secondCommand = commands.get(i + 1);
            Map<String, Object> firstReturnType = firstCommand.getReturnType();
            Map<String, Object> firstParamType = firstCommand.getParamType();
            Map<String, Object> secondParamType = secondCommand.getParamType();
            args.putAll(firstParamType);
            args.putAll(firstReturnType);

            if (!args.entrySet().contains(secondParamType.entrySet())) {
                throw new Exception(String.format("command '%s' cannot be used in chain ",  secondCommand.getPhrase()));
            }

        }
        return included;
    }
}
