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




        String q = "";
        List<Command> commands = null;

        for (String first : str.split(" "))
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
            for (String s : str.split(" ")) {
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
            logger.debug("Неправильная команда!");
            result.put("phrase","");
            return result;
        } else {

            result.put("phrase", commands.get(0).getPhrase());

            if (commands.get(0).getParamType() != null) {
                //filter_for_arguments(phrase_array[1],commands.get(0),result);
                addParam(commands.get(0),result);
            }
        }
        logger.warn("phrase filtered "+result.get("phrase"));
        return result;
    }


    public void addParam( Command commands, HashMap result){
        LinkedHashMap<String, Object> paramType = commands.getParamType();
        Set<String> strings = paramType.keySet();
        strings.stream().forEach(s->result.put(s, paramType.get(s)));


    }
}
