package com.asoft.ajarvis.actions.services;

import com.asoft.ajarvis.actions.controller.CommandController;
import com.asoft.ajarvis.actions.enities.Command;
import com.asoft.ajarvis.actions.repository.CommandRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;



@Service
public class Filter {

    private final static Logger logger = LoggerFactory.getLogger(CommandController.class);

    @Autowired
    CommandRepository repository;

    public Map filter(String str) {

        HashMap result = new HashMap<String, Object>();


        String[] phrase_array;


        String q = "";
        List<Command> commands = null;

        phrase_array = str.split("аргументы");
        for (String first : phrase_array[0].split(" "))
            try {
                commands = (List) repository.findAllByPhraseIsStartingWith(first);
                if(!commands.isEmpty()){
                  break;
                };
            } catch (NoSuchElementException e) {
                continue;
            }


        List<Integer> position = new ArrayList<Integer>(Collections.nCopies(commands.size(), 0));

        for (int k = commands.size() - 1; k > -1; k -= 1) {
            for (String s : phrase_array[0].split(" ")) {
                if (position.get(k) < commands.get(k).getPhrase().split(" ").length) {
                    if (s.equals(commands.get(k).getPhrase().split(" ")[position.get(k)])) {
                        position.set(k, position.get(k) + 1);
                    }


                }

            }
            if (position.get(k) < commands.get(k).getPhrase().split(" ").length) {
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
            logger.error("Неправильная команда!");
            return null;
        } else {

            result.put("phrase", commands.get(0).getPhrase());

            if (commands.get(0).getParamType() != null) {
              filter_for_arguments(phrase_array[1],commands.get(0),result);
            }
        }

        return result;
    }


    public void filter_for_arguments(String args_array, Command commands, HashMap result) {
        String[] separate_fraze = args_array.trim().split(" ");

        int j = 0;
        for (String i : commands.getParamType().keySet()) {
            String obj = (String) commands.getParamType().get(i);
            for (; j < separate_fraze.length; j++) {
                if (obj.equals("String")) {
                    result.put(i, separate_fraze[j]);
                    j++;
                    break;
                } else if (obj.equals("int")) {
                    try {
                        result.put(i, Integer.parseInt(separate_fraze[j]));
                        j++;
                        break;
                    } catch (NumberFormatException e) {
                        continue;
                    }
                } else if (obj.equals("float")) {
                    try {
                        result.put(i, Float.parseFloat(separate_fraze[j]));
                        j++;
                        break;
                    } catch (NumberFormatException e) {
                        continue;
                    }
                }
            }
        }
        if ((commands.getParamType().size() + 1) != result.size())
            logger.warn("Неправеньное количество аргументов");


    }
}
