package org.tbansal.dutch.learning.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.tbansal.dutch.learning.app.entities.DutchWords;

import java.util.List;

public interface WordsRepository extends JpaRepository<DutchWords, Long> {

    List<DutchWords> findByDutchWordContainingOrEnglishTranslationContainingOrExampleContaining(final String dutchWord, final String englishTranslation, final String example);

}