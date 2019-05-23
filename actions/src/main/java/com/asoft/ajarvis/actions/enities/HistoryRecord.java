package com.asoft.ajarvis.actions.enities;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class HistoryRecord {
    @Id
    private String id;
    private String command;
    private Date time;
    private Map<String, Object> arg;
    private Map<String, Object> result;

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
        this.result = result;
    }


}
