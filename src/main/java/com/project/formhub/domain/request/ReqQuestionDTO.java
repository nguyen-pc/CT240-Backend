package com.project.formhub.domain.request;

import java.util.List;

import com.project.formhub.domain.Question;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReqQuestionDTO {
    private String questionText;
    private Question.QuestionType questionType;
    private List<ChoiceDTO> choices;

    @Setter
    @Getter
    public static class ChoiceDTO {
        private String choiceText;
    }
}
