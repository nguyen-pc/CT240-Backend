package com.project.formhub.service;

import com.project.formhub.domain.*;
import com.project.formhub.repository.ResponseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResponseService {
    private final ResponseRepository responseRepository;
    private final SurveyService surveyService;
    private final QuestionService questionService;
    private final ChoiceService choiceService;
    private final ProjectService projectService;

    public ResponseService(ResponseRepository responseRepository, SurveyService surveyService,
            QuestionService questionService, ChoiceService choiceService, ProjectService projectService) {
        this.responseRepository = responseRepository;
        this.surveyService = surveyService;
        this.questionService = questionService;
        this.choiceService = choiceService;
        this.projectService = projectService;
    }

    public void checkParameter(Long projectId, Long surveyId) {
        // Check if the Project exists
        Optional<Project> dbProject = this.projectService.getProjectById(projectId);
        if (dbProject.isEmpty())
            throw new IllegalArgumentException("Could not find project with id: " + projectId + " in DB");
        // Check if the Survey exists
        Survey dbSurvey = this.surveyService.getSurveyById(surveyId);
        if (dbSurvey == null)
            throw new IllegalArgumentException("Could not find survey with id: " + surveyId + " in DB");
        // Check if the Project contains the Survey
        if (projectId != dbSurvey.getProject().getProjectId())
            throw new IllegalArgumentException("Project #" + projectId + " DOES NOT have survey #" + surveyId);
    }

    // create a response => create all answer of a response
    public Response createResponse(Long projectId, Long surveyId, Response response) {
        checkParameter(projectId, surveyId);
        Survey survey = this.surveyService.getSurveyById(surveyId);
        response.setSurvey(survey);

        List<Answer> answers = response.getAnswers();

        for (Answer answer : answers) {
            // check question
            if (answer.getQuestion() == null)
                throw new IllegalArgumentException("Could not find a question of an answer from your request");
            Question question = this.questionService.getQuestion(projectId, surveyId,
                    answer.getQuestion().getQuestionId());
            if (question == null)
                throw new IllegalArgumentException(
                        "Could not find a question with id: " + answer.getQuestion().getQuestionId());
            // check if multiple choice/ checkbox question
            if (question.getQuestionType().isMultipleOrCheckbox()) {
                answer.setAnswerText("");
                List<Choice> choiceList = answer.getChoices();
                if (choiceList == null || choiceList.isEmpty()) {
                    if (question.getIsRequired())
                        throw new IllegalArgumentException("Multiple choice/ Checkbox must have at least one choice!");
                    else
                        answer.setChoices(null);
                } else {
                    if (question.getQuestionType().isMultipleChoice() && choiceList.size() != 1)
                        throw new IllegalArgumentException("MultipleChoice question has not to have more choice");
                    for (Choice choice : choiceList) {
                        Choice dbChoice = this.choiceService.getChoice(choice.getChoiceId());
                        if (dbChoice == null)
                            throw new IllegalArgumentException(
                                    "Could not find a choice with id: " + choice.getChoiceId());
                        if (question.getQuestionId() != dbChoice.getQuestion().getQuestionId())
                            throw new IllegalArgumentException(
                                    "Question with id " + answer.getQuestion().getQuestionId()
                                            + " DOES NOT contain survey with id: " + dbChoice.getChoiceId());
                    }
                }
            } else {
                // text question: => choice = null
                answer.setChoices(null);
                // check if required question
                if (question.getIsRequired() && answer.getAnswerText().isEmpty())
                    throw new IllegalArgumentException("Answer must have answerText");
            }
            answer.setResponse(response);
            answer.setQuestion(question);
        }

        response.setAnswers(null);
        this.responseRepository.save(response);

        response.setAnswers(answers);
        return this.responseRepository.save(response);
    }

    // get a response
    public Response getResponse(Long projectId, Long surveyId, Long responseId) {
        checkParameter(projectId, surveyId);
        Response dbResponse = this.responseRepository.findById(responseId).orElse(null);
        if (dbResponse == null)
            throw new IllegalArgumentException("Could not find response with id: " + responseId);
        if (surveyId != dbResponse.getSurvey().getSurveyId())
            throw new IllegalArgumentException("Survey: " + surveyId + " DOES NOT CONTAIN response: " + responseId);
        return dbResponse;
    }

    // delete a response => delete all answer of response with id = responseId
    public void deleteResponse(Long projectId, Long surveyId, Long responseId) {
        checkParameter(projectId, surveyId);
        Response dbResponse = this.responseRepository.findById(responseId).orElse(null);
        if (dbResponse == null)
            throw new IllegalArgumentException("Could not find response with id: " + responseId);
        if (surveyId != dbResponse.getSurvey().getSurveyId())
            throw new IllegalArgumentException("Survey: " + surveyId + " DOES NOT CONTAIN response: " + responseId);
        this.responseRepository.deleteById(responseId);
    }
    // DO NOT UPDATE

    public List<Response> getAllResponseOfSurvey(Long projectId, Long surveyId) {
        checkParameter(projectId, surveyId);
        return this.responseRepository.findBySurvey_SurveyId(surveyId);
    }
}