package com.asoft.ajarvis.actions.repository;

import com.asoft.ajarvis.actions.enities.HistoryRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface HistoryRepository extends MongoRepository<HistoryRecord, String> {
    List<HistoryRecord> findAll();
    Iterable<HistoryRecord> findAllByTimeGreaterThan(Date date);
    Iterable<HistoryRecord> findAllByOrderByTimeDesc();
    Iterable<HistoryRecord> findAllByIdIn(List<String> ids);
}
