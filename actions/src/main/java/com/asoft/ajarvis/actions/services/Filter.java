package com.asoft.ajarvis.actions.services;

import com.asoft.ajarvis.actions.controller.CommandController;
import com.asoft.ajarvis.actions.enities.Command;
import com.asoft.ajarvis.actions.repository.CommandRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.asoft.ajarvis.actions.constant.GeneralConstants.EMPTY;
import static com.asoft.ajarvis.actions.constant.GeneralConstants.SPACE;

@Service
public class Filter {
    private static final Logger logger = LoggerFactory.getLogger(CommandController.class);

    private static final String PHRASE = "phrase";

    @Autowired
    private CommandRepository repository;

    public Map filter(String str) {
        Map result = new HashMap<String, Object>();

        List<Command> commands = null;

        for (String first : str.split(SPACE)) {
            try {
                commands = (List<Command>) repository.findAllByPhraseIsStartingWith(first);

                if (!commands.isEmpty()) {
                    break;
                }
            } catch (NoSuchElementException ignored) {
            }
        }

        if (commands == null) return null;

        List<Integer> position = new ArrayList<>(Collections.nCopies(commands.size(), 0));

        for (int k = commands.size() - 1; k > -1; k -= 1) {
            for (String s : str.split(SPACE)) {
                if (position.get(k) < commands.get(k).getPhrase().split(SPACE).length) {
                    if (s.equals(commands.get(k).getPhrase().split(SPACE)[position.get(k)])) {
                        position.set(k, position.get(k) + 1);
                    }
                }
            }

            if (position.get(k) < commands.get(k).getPhrase().split(SPACE).length) {
                commands.remove(k);
                position.remove(k);
            }
        }

        for (int t = 0; t < commands.size(); t++) {
            if (position.get(t) < Collections.max(position)) {
                commands.remove(t);
                position.remove(t);
            }
        }

        if (commands.size() != 1) {
            logger.debug("Неправильная команда!");
            result.put(PHRASE, EMPTY);
            return result;
        } else {
            result.put(PHRASE, commands.get(0).getPhrase());

            if (commands.get(0).getParamType() != null) {
                addParam(commands.get(0), (HashMap) result);
            }
        }

        logger.warn(String.format("phrase filtered %s", result.get(PHRASE)));
        return result;
    }

    public void addParam(Command commands, Map result) {
        LinkedHashMap<String, Object> paramType = (LinkedHashMap<String, Object>) commands.getParamType();
        Set<String> strings = paramType.keySet();
        strings.stream().forEach(s -> result.put(s, paramType.get(s)));
    }
}
