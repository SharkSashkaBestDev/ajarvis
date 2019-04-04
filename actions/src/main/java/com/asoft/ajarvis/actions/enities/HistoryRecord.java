package com.asoft.ajarvis.actions.enities;

import lombok.Data;
import org.springframework.data.annotation.Id;


import java.security.Timestamp;
import java.util.Date;
import java.util.UUID;

@Data
public class HistoryRecord {

    @Id
    private String id;
    private String command;
    private Date time;


    public HistoryRecord() {
        this.id= UUID.randomUUID().toString();
        this.time = new Date();
    }


    public HistoryRecord(String command) {
        this();
        this.command = command;

    }
}
