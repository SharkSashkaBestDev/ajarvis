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

import static com.asoft.ajarvis.actions.constant.GeneralConstants.ERROR;

@Service
public class HistoryService {
    private static final String PHRASE_EXECUTOR = "62d5faf4-5bc6-4a11-807e-6c40c4c198b9";

    private static final Logger logger = LoggerFactory.getLogger(HistoryRecord.class);

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private CommandRepository commandRepository;

    @Autowired
    private Executor executor;

    public List<String> getIdsList(OptionalInt beginO, OptionalInt endO) {
        ArrayList<HistoryRecord> records =
                (ArrayList) historyRepository.findAllByOrderByTimeDesc();

        int begin = beginO.isPresent() ? beginO.getAsInt() : 0;
        int end = endO.isPresent() ? endO.getAsInt() : records.size();

        records = new ArrayList<>(records.subList(begin, end));
        records.removeIf(s -> (s.getCommand().equals(PHRASE_EXECUTOR)));
        List<String> resultID = records.stream().map(HistoryRecord::getId).collect(Collectors.toList());
        return resultID;
    }

    public void executeHistory(List<String> ids) {
        ArrayList<HistoryRecord> records = (ArrayList) historyRepository.findAllByIdIn(ids);

        records.stream().forEach((s) -> {
            try {
                executeHistoryRecord(s);
            } catch (Exception e) {
                logger.error(String.format("Execution failure in %s command with error message: %s", s.getId()), e.getMessage());
            }
        });
    }

    public void executeHistoryRecord(HistoryRecord rec) throws Exception {
        if (rec.getCommand() != null && !rec.getCommand().isEmpty()) {
            Optional<Command> commandOptional = commandRepository.findById(rec.getCommand());
            if (commandOptional.isPresent()) {
                executor.excuteCmd(commandOptional.get(), rec.getArg());
            }
        }
    }

    public Map getArgsofHistoryRec(String id) {
        Optional<HistoryRecord> cmd = historyRepository.findById(id);
        return cmd.isPresent() ? cmd.get().getArg() : null;
    }

    public Map getResultfHistoryRec(String id) throws Exception {
        Optional<HistoryRecord> cmd = historyRepository.findById(id);
        Map<String, Object> result = cmd.isPresent() ? cmd.get().getResult() : null;
        if (result != null && result.get(ERROR) != null) {

            throw new Exception(String.format("History record %s  have Error in result  with message %s", cmd.get().getId(), result.get(ERROR)));
        }
        return null;
    }

    public void clearHistory() {
        historyRepository.deleteAll();
    }
}
