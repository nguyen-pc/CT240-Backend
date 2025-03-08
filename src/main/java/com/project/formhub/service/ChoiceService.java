package com.project.formhub.service;

import com.project.formhub.domain.Choice;
import com.project.formhub.repository.ChoiceRepository;
import org.springframework.stereotype.Service;

@Service
public class ChoiceService {
    private final ChoiceRepository choiceRepository;

    public ChoiceService(ChoiceRepository choiceRepository) {
        this.choiceRepository = choiceRepository;
    }

//    get a choice with choiceId
    public Choice getChoice(Long choiceId) {
        Choice dbChoice = this.choiceRepository.findById(choiceId).orElse(null);
        if (dbChoice == null)
            throw new IllegalArgumentException("Could not find choice with id: " + choiceId);
        return dbChoice;
    }
}
