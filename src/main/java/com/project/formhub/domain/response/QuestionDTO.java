package com.project.formhub.domain.response;

import com.project.formhub.domain.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    private long questionId;
    private String questionName;
    private Question.QuestionType questionType;
    private Long surveyId;
    private List<ChoiceDTO> choices;
}
