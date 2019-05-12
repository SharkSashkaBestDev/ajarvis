package com.asoft.ajarvis.actions.services;

import com.asoft.ajarvis.actions.enities.Command;
import com.asoft.ajarvis.actions.enities.HistoryRecord;
import com.asoft.ajarvis.actions.repository.CommandRepository;
import com.asoft.ajarvis.actions.repository.HistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class HistoryService {

    private static final Logger logger = LoggerFactory.getLogger(HistoryRecord.class);

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private CommandRepository commandRepository;

    @Autowired
    private Executor executor;


    public List<String> getIdsList(OptionalInt   beginO, OptionalInt   endO){
        ArrayList<HistoryRecord> records =
                (ArrayList) historyRepository.findAllByOrderByTimeDesc();

        int begin =beginO.isPresent()?beginO.getAsInt():0;
        int end =endO.isPresent()?endO.getAsInt():records.size();

        records = new ArrayList<> (records.subList(begin, end));

        return records.stream().map(HistoryRecord::getId).collect(Collectors.toList());

    }

    public void executeHistory(List<String> ids) {

        ArrayList<HistoryRecord> records = (ArrayList)historyRepository.findAllByIdIn(ids);

        try {
            records.stream().forEach((s) -> {
                try {
                    executeHistoryRecord(s);
                } catch (Exception e) {
                    logger.error(String.format("Execution failure in %s command with error message: %s", s.getId()), e.getMessage());
                }
            });

        } finally {
            List<String> id = records.stream().map((s) -> s.getId()).collect(Collectors.toList());
            historyRepository.save(new HistoryRecord(id));
        }

    }


    public void executeHistoryRecord(HistoryRecord rec) throws Exception  {

        if (rec.getCommand() != null && !rec.getCommand().isEmpty()) {
            Optional<Command> commandOptional = commandRepository.findById(rec.getCommand());
            if (commandOptional.isPresent()) {
                executor.excuteCmd(commandOptional.get(), rec.getArg());
            }
        }
        if (rec.getCommandIds() != null && !rec.getCommandIds().isEmpty()) {
            Iterable<HistoryRecord> commandIds =
                    historyRepository.findAllByIdIn(rec.getCommandIds());
            for (HistoryRecord current :
                    commandIds) {
                executeHistoryRecord(current);
            }
        }
    }

    public Map getArgsofHistoryRec(String id){
        Optional<HistoryRecord> cmd = historyRepository.findById(id);
        return cmd.isPresent()?cmd.get().getArg():null;
    }

    public Map getResultfHistoryRec(String id ){
        Optional<HistoryRecord> cmd = historyRepository.findById(id);
        return cmd.isPresent()?cmd.get().getResult():null;
    }

}
