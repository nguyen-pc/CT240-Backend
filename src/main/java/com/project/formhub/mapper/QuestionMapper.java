package com.project.formhub.mapper;

import com.project.formhub.domain.Question;
import com.project.formhub.domain.response.ChoiceDTO;
import com.project.formhub.domain.response.QuestionDTO;

import java.util.stream.Collectors;

public class QuestionMapper {
    public static QuestionDTO toDTO(Question question) {
        return new QuestionDTO(
                question.getQuestionId(),
                question.getQuestionName(),
                question.getQuestionType(),
                question.getSurvey() != null ? question.getSurvey().getSurveyId() : null,
                question.getChoices() != null ? question.getChoices().stream().map(choice -> new ChoiceDTO(choice.getChoiceId(), choice.getChoiceText(), question.getQuestionId())).collect(Collectors.toList()) : null
        );
    }
}
