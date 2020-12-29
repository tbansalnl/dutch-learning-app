package org.tbansal.dutch.learning.app.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.tbansal.dutch.learning.app.entities.DutchWords;
import org.tbansal.dutch.learning.app.repositories.WordsRepository;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WordService {

    private final WordsRepository wordsRepository;

    public List<DutchWords> search(String filter) {
        return wordsRepository.findByDutchWordContainingOrEnglishTranslationContainingOrExampleContaining(filter, filter, filter);
    }

    public void save(DutchWords dutchWords) {
        wordsRepository.save(dutchWords);
    }

    public List<DutchWords> findAll() {
        return wordsRepository.findAll();
    }

    public Optional<DutchWords> findById(Long id) {
        return wordsRepository.findById(id);
    }

    public void delete(DutchWords dutchWords) {
        wordsRepository.deleteById(dutchWords.getId());
    }

    public void checkAndUpdate(DutchWords item) {
        String word = StringUtils.lowerCase(item.getEnglishTranslation());
        String answer = StringUtils.lowerCase(item.getAnswer());
        if (word.contains(answer) || answer.contains(word)) {
            item.setMatching(true);
        }
    }

    public void countFiledAttemptAndSave(List<DutchWords> items) {
        items.stream().filter(i -> !i.isMatching()).forEach(i -> i.setFailedAttempts(1 + i.getFailedAttempts()));
        wordsRepository.saveAll(items);
    }

    public List<DutchWords> getRandomItems() {
        SecureRandom rand = new SecureRandom();
        int n = rand.nextInt(wordsRepository.findAll().size());

        return Collections.singletonList(wordsRepository.findAll().get(n));
    }
}
