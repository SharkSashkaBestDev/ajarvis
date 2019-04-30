package com.asoft.ajarvis.actions.enities;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Data
public class HistoryRecord {
    @Id
    private String id;
    private String command;
    private Date time;
    private Map<String, Object> arg;
    private ArrayList<String> commandIds;


    public HistoryRecord() {
        this.id = UUID.randomUUID().toString();
        this.time = new Date();
    }

    public HistoryRecord(String command) {
        this();
        this.command = command;
    }

    public HistoryRecord(String command, Map<String, Object> arg) {
        this();
        this.command = command;
        this.arg = arg;
    }

    public HistoryRecord(ArrayList<String> commandIds) {
        this();
        this.commandIds = commandIds;
    }
}
