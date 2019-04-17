package com.asoft.ajarvis.actions.enities;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;

import java.util.*;

/**
 *<h1>Command </h1>
 *This class describe a form of a  Ajarvis Command
 *
 * @see Command
 *
 * @author A.T
 * @since 04.04.2019
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Command {
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    private static final  Logger logger = LoggerFactory.getLogger(Command.class);

    @Id
    private String id;
    private String name;
    private String phrase;
    private String code;
    private String imports;
    private Map<String, Object> paramType;
    private Map<String, Object> returnType;
    private Language language;
    @Setter(value = AccessLevel.NONE)
    private Set<String> whereUsed;
    private List<String> usedCommandsIds;

    public Command() {
        this.id = UUID.randomUUID().toString();
    }

    public Command(String name, String phrase, String code, Language language) {
        this();
        this.name = name;
        this.phrase = phrase;
        this.code = code;
        this.language = language;
    }

    public Command(String name, String phrase, String code, List<String> usedCommandsIds) {
        this();
        this.name = name;
        this.code = code;
        this.phrase = phrase;
        this.usedCommandsIds = usedCommandsIds;
    }

    public Command(String name, String phrase, String code, Map<String, Object> paramType, Map<String, Object> returnType, List<String> usedCommandsIds) {
        this();
        this.name = name;
        this.phrase = phrase;
        this.code = code;
        this.paramType = paramType;
        this.returnType = returnType;
        this.usedCommandsIds = usedCommandsIds;
    }

    public Command(String name, String phrase, Map<String, Object> paramType, Map<String, Object> returnType, List<String> usedCommandsIds) {
        this();
        this.name = name;
        this.phrase = phrase;
        this.paramType = paramType;
        this.returnType = returnType;
        this.usedCommandsIds = usedCommandsIds;
    }

    public Command(String name, String phrase, String code, Map<String, Object> returnType, List<String> usedCommandsIds) {
        this();
        this.name = name;
        this.phrase = phrase;
        this.code = code;
        this.returnType = returnType;
        this.usedCommandsIds = usedCommandsIds;
    }

    public boolean addWhereUsed(String id) {
        if (this.whereUsed == null) {
            this.whereUsed = new HashSet<>();
        }
        logger.info(String.format("%s used in %s Command", this.id, id));
        return this.whereUsed.add(id);
    }

    public Boolean deleteFromWhereUsed(String id) {
        return this.whereUsed != null ? this.whereUsed.remove(id) : false;
    }
}