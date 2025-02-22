package com.project.formhub.service;

import com.project.formhub.domain.Question;
import com.project.formhub.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository){
        this.questionRepository = questionRepository;
    }

    public Question createQuestion(Question question){
        return this.questionRepository.save(question);
    }

    public Question getQuestion(Long id){
        return this.questionRepository.findById(id).orElse(null);
    }

    public void deleteQuestion(Long id){
        this.questionRepository.deleteById(id);
    }

    public Question updateQuestion(Long id, Question resQuestion){
        Question dbQuestion = this.getQuestion(id);
        dbQuestion.setQuestionName(resQuestion.getQuestionName());
        dbQuestion.setQuestionType(resQuestion.getQuestionType());

        return this.questionRepository.save(dbQuestion);
    }
}
