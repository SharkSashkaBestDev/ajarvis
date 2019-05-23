package com.asoft.ajarvis.actions.controller;

import com.asoft.ajarvis.actions.enities.HistoryRecord;
import com.asoft.ajarvis.actions.repository.HistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ajarvis/history")
public class HistoryController {

    private static final Logger logger = LoggerFactory.getLogger(HistoryController.class);
    @Autowired
    private HistoryRepository historyRepo;

@GetMapping
    public Iterable<HistoryRecord> getHistory() {
        logger.info("Returning total history");
        return historyRepo.findAllByOrderByTimeDesc();
    }

}
