package com.project.formhub.domain.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqAnswerDTO {
    private Long questionId;
    private Long choiceId;
    private String answerText;
}
