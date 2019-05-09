package com.asoft.ajarvis.actions.enities;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.*;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HistoryRecord {
    @Id
    private String id;
    private String command;
    private Date time;
    private Map<String, Object> arg;
    private Map<String, Object> result;
    private List<String> commandIds;


    public HistoryRecord() {
        this.id = UUID.randomUUID().toString();
        this.time = new Date();
    }

    public HistoryRecord(String command) {
        this();
        this.command = command;
    }

    public HistoryRecord(String command, Map<String, Object> arg,Map<String, Object> result) {
        this();
        this.command = command;
        this.arg = arg;
        this.result =result;
    }

    public HistoryRecord(List<String> commandIds) {
        this();
        this.commandIds = commandIds;
    }
}
