package com.project.formhub.service;

import com.project.formhub.domain.*;
import com.project.formhub.repository.ResponseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResponseService {
    private final ResponseRepository responseRepository;
    private final SurveyService surveyService;
    private final QuestionService questionService;
    private final ChoiceService choiceService;

    public ResponseService(ResponseRepository responseRepository, SurveyService surveyService, QuestionService questionService, ChoiceService choiceService) {
        this.responseRepository = responseRepository;
        this.surveyService = surveyService;
        this.questionService = questionService;
        this.choiceService = choiceService;
    }

    //    create a response => create all answer of a response
    public Response createResponse(Long surveyId, Response response) {
        Survey survey = this.surveyService.getSurveyById(surveyId);
        if (survey == null)
            throw new IllegalArgumentException("Could not find survey with id: " + surveyId + " in DB");
        response.setSurvey(survey);

        List<Answer> answers = response.getAnswers();

        for (Answer answer : answers) {
//            check question
            if (answer.getQuestion() == null)
                throw new IllegalArgumentException("Could not find a question of an answer from your request");
            Question question = this.questionService.getQuestion(answer.getQuestion().getQuestionId());
            if (question == null)
                throw new IllegalArgumentException("Could not find a question with id: " + answer.getQuestion().getQuestionId());
//            check if multiple choice/ checkbox question
            if (question.getQuestionType().isMultipleOrCheckbox()) {
                if (answer.getChoice() == null)
                    throw new IllegalArgumentException("Multiple choice/ Checkbox must have at least one choice!");
                else {
                    Choice choice = this.choiceService.getChoice(answer.getChoice().getChoiceId());
                    if (choice == null)
                        throw new IllegalArgumentException("Could not find a choice with id: " + answer.getChoice().getChoiceId());
                    if (question.getQuestionId() != choice.getQuestion().getQuestionId())
                        throw new IllegalArgumentException("Question with id " + answer.getQuestion().getQuestionId() + " DOES NOT contain survey with id: " + answer.getChoice().getChoiceId());
                    answer.setAnswerText("");
                }
            } else {
//                text question: => choice = null
//                allow empty answerText?????????????????????????
                answer.setChoice(null);
            }
            answer.setResponse(response);
            answer.setQuestion(question);
        }

        response.setAnswers(null);
        this.responseRepository.save(response);

        response.setAnswers(answers);
        return this.responseRepository.save(response);
    }

    //    get a response
    public Response getResponse(Long responseId) {
        Response dbResponse = this.responseRepository.findById(responseId).orElse(null);
        if (dbResponse == null)
            throw new IllegalArgumentException("Could not find response with id: " + responseId);
        return dbResponse;
    }

    //    delete a response => delete all answer of response with id = responseId
    public void deleteResponse(Long surveyId, Long responseId) {
        Survey survey = this.surveyService.getSurveyById(surveyId);
        if (survey == null)
            throw new IllegalArgumentException("Could not find survey with id: " + surveyId + " in DB");
        Response dbResponse = this.responseRepository.findById(responseId).orElse(null);
        if (dbResponse == null)
            throw new IllegalArgumentException("Could not find response with id: " + responseId);
        if (surveyId != dbResponse.getSurvey().getSurveyId())
            throw new IllegalArgumentException("Survey: " + surveyId + " DOES NOT CONTAIN response: " + responseId);
        this.responseRepository.deleteById(responseId);
    }
//    DO NOT UPDATE
}
