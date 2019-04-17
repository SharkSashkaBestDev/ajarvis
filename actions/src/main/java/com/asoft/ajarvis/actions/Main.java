package com.asoft.ajarvis.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(AjarvisApplication.class);

    public static void main(String[] args) {
        ArrayList<Object> list = Stream.of(500, 600).collect(Collectors.toCollection(ArrayList::new));
        logger.info(String.format("List type: %s", list.getClass().getSimpleName().concat(list.get(0).getClass().getSimpleName())));
    }
}
