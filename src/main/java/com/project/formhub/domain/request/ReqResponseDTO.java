package com.project.formhub.domain.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqResponseDTO {
    private long surveyId;
    private List<ReqAnswerDTO> answers;
}
