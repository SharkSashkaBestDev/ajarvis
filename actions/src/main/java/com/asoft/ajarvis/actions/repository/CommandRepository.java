package com.asoft.ajarvis.actions.repository;

import com.asoft.ajarvis.actions.enities.Command;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommandRepository extends MongoRepository<Command, String> {
    Iterable<Command> findByPhraseEndingWith(String str);
    Iterable<Command> findAllByPhraseIsStartingWith(String str);
    Iterable<Command> findByPhraseContaining(String str);
    Iterable<Command> findAllByUsedCommandsIdsContaining(String id);
    Iterable<Command> findAllByIdIn(List<String> ids);
    Optional<Command> findByUsedCommandsIds(List<String> s);
    Optional<Command> findByPhrase(String str);
}
