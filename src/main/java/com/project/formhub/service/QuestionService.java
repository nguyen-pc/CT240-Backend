package com.project.formhub.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.project.formhub.domain.Choice;
import com.project.formhub.domain.Question;
import com.project.formhub.domain.Survey;
import com.project.formhub.domain.request.ReqQuestionDTO;
import com.project.formhub.repository.ChoiceRepository;
import com.project.formhub.repository.QuestionRepository;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final SurveyService surveyService;
    private final ChoiceRepository choiceRepository;

    public QuestionService(QuestionRepository questionRepository, SurveyService surveyService,
            ChoiceRepository choiceRepository) {
        this.questionRepository = questionRepository;
        this.surveyService = surveyService;
        this.choiceRepository = choiceRepository;
    }

    public Question createQuestion(ReqQuestionDTO question, long surveyId) {
        Survey survey = surveyService.getSurveyById(surveyId);

        Question newQuestion = new Question();
        newQuestion.setSurvey(survey);
        newQuestion.setQuestionName(question.getQuestionText());
        newQuestion.setQuestionType(question.getQuestionType());

        Question resQuestion = questionRepository.save(newQuestion);

        // Luu danh sách các lựa chọn câu hỏi nếu có
        if (question.getQuestionType() == Question.QuestionType.MULTIPLE_CHOICE
                || question.getQuestionType() == Question.QuestionType.CHECKBOX ||
                question.getQuestionType() == Question.QuestionType.DROPDOWN) {

            List<Choice> choices = question.getChoices().stream().map(choiceDTO -> {
                Choice choice = new Choice();
                choice.setQuestion(resQuestion);
                choice.setChoiceText(choiceDTO.getChoiceText());
                return choice;
            }).collect(Collectors.toList());

            choiceRepository.saveAll(choices);
        }

        return resQuestion;
    }

}
