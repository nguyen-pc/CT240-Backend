package com.project.formhub.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChoiceDTO {
    private long choiceId;
    private String choiceText;
    private Long questionId;
}
