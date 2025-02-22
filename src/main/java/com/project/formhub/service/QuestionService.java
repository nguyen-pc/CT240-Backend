package com.project.formhub.service;

import com.project.formhub.domain.Choice;
import com.project.formhub.domain.Question;
import com.project.formhub.repository.ChoiceRepository;
import com.project.formhub.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public Question createQuestion(Question question) {
        return this.questionRepository.save(question);
    }

    public Question getQuestion(Long id) {
        return this.questionRepository.findById(id).orElse(null);
    }

    public void deleteQuestion(Long id) {
        this.questionRepository.deleteById(id);
    }

    public Question updateQuestion(Long id, Question resQuestion) {
        Question dbQuestion = this.getQuestion(id);
        dbQuestion.setQuestionName(resQuestion.getQuestionName());
        dbQuestion.setQuestionType(resQuestion.getQuestionType());

        List<Choice> resChoices = resQuestion.getChoices();
//        NOTE!!!!!!!!!!!!!!!!!!!!!!!!!
        if (resChoices != null) {
            dbQuestion.getChoices().forEach(choice -> choice.setQuestion(null));
            dbQuestion.getChoices().clear();

            this.questionRepository.save(dbQuestion);

            for (Choice resChoice : resChoices) {
                resChoice.setQuestion(dbQuestion);
            }
            dbQuestion.getChoices().addAll(resChoices);
        }
        return this.questionRepository.save(dbQuestion);
    }
}
