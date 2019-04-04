package com.asoft.ajarvis.actions.enities;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


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
    private List<String> whereUsed;
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
        this.name = name;
        this.phrase = phrase;
        this.code = code;
        this.paramType = paramType;
        this.returnType = returnType;
        this.usedCommandsIds = usedCommandsIds;
    }

    public Command(String name, String phrase,  Map<String, Object> paramType, Map<String, Object> returnType, List<String> usedCommandsIds) {
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


    public boolean addIntoWhereUsed(String id) {

        if (this.whereUsed == null) {
            this.whereUsed = new ArrayList<String>();

        }
        logger.info(this.id + " used in " + id + " Command ");
        return this.whereUsed.add(id);

    }

    public Boolean deleteFromWhereUsed(String id) {

        if (this.whereUsed != null) {

            return this.whereUsed.remove(id);

        }

        return null;
    }


}