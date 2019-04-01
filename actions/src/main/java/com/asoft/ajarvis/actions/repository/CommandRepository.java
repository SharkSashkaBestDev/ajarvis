package com.asoft.ajarvis.actions.repository;



import com.asoft.ajarvis.actions.enities.Command;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CommandRepository extends CrudRepository<Command, String> {

    Iterable<Command> findByPhraseEndingWith(String str);

    Iterable<Command> findByPhraseContaining(String str);

    Iterable<Command> findAllByUsedCommandsIdsContaining(String id);


    Optional<Command> findByPhrase(String str);
}
