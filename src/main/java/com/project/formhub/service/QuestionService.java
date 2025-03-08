package com.project.formhub.service;

import com.project.formhub.domain.Choice;
import com.project.formhub.domain.Project;
import com.project.formhub.domain.Question;
import com.project.formhub.domain.Survey;
import com.project.formhub.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final ProjectService projectService;
    private final SurveyService surveyService;

    public QuestionService(QuestionRepository questionRepository, ProjectService projectService, SurveyService surveyService) {
        this.questionRepository = questionRepository;
        this.projectService = projectService;
        this.surveyService = surveyService;
    }

    public void checkParameter(Long projectId, Long surveyId) {
//        Check if the Project exists
        Optional<Project> dbProject = this.projectService.getProjectById(projectId);
        if (dbProject.isEmpty())
            throw new IllegalArgumentException("Could not find project with id: " + projectId + " in DB");
//        Check if the Survey exists
        Survey dbSurvey = this.surveyService.getSurveyById(surveyId);
        if (dbSurvey == null)
            throw new IllegalArgumentException("Could not find survey with id: " + surveyId + " in DB");
//        Check if the Project contains the Survey
        if (projectId != dbSurvey.getProject().getProjectId())
            throw new IllegalArgumentException("Project #" + projectId + " DOES NOT have survey #" + surveyId);
    }

    public Question createQuestion(Long projectId, Long surveyId, Question question) {
        checkParameter(projectId, surveyId);
        Survey dbSurvey = this.surveyService.getSurveyById(surveyId);
//        check if MULTIPLE_CHOICE or CHECKBOX have not any choice
        if ((question.getQuestionType().isMultipleOrCheckbox()) && question.getChoices() == null)
            throw new IllegalArgumentException("Multiple choice/ Checkbox question could not have empty choices");

        question.setSurvey(dbSurvey);

//        create question without choices
        List<Choice> choices = question.getChoices();
        question.setChoices(null);
        Question initialQuestion = this.questionRepository.save(question);
        if (initialQuestion == null)
            throw new RuntimeException("init question is WRONG");

//        only create choice if multiple_choice or checkbox question
        if (question.getQuestionType().isMultipleOrCheckbox()) {
//        set question for each choice in choices
            for (Choice choice : choices)
                choice.setQuestion(initialQuestion);

//        create question with choices
            initialQuestion.setChoices(choices);
            this.questionRepository.save(initialQuestion);
            if (initialQuestion == null) {
                this.questionRepository.deleteById(initialQuestion.getQuestionId());
                throw new RuntimeException("create question is WRONG");
            }
        }
        return initialQuestion;
    }


    public Question getQuestion(Long projectId, Long surveyId, Long questionId) {
        checkParameter(projectId, surveyId);
        Question dbQuestion = this.questionRepository.findById(questionId).orElse(null);
        if (dbQuestion == null)
            throw new IllegalArgumentException("Could not find question with id: " + questionId);
        if (surveyId != dbQuestion.getSurvey().getSurveyId())
            throw new IllegalArgumentException("Survey: " + surveyId + " DOES NOT CONTAIN question: " + questionId);
        return dbQuestion;
    }

    public void deleteQuestion(Long projectId, Long surveyId, Long questionId) {
        checkParameter(projectId, surveyId);
        Question dbQuestion = this.questionRepository.findById(questionId).orElse(null);
        if (dbQuestion == null)
            throw new IllegalArgumentException("Could not find question with id: " + questionId);
        if (surveyId != dbQuestion.getSurvey().getSurveyId())
            throw new IllegalArgumentException("Survey: " + surveyId + " DOES NOT CONTAIN question: " + questionId);
        this.questionRepository.deleteById(questionId);
    }

    public Question updateQuestion(Long projectId, Long surveyId, Long questionId, Question resQuestion) {
        Question dbQuestion = this.getQuestion(projectId, surveyId, questionId);
        if (dbQuestion == null) {
            throw new IllegalArgumentException("Could not find question with id: " + questionId);
        }

        dbQuestion.setQuestionName(resQuestion.getQuestionName());
        dbQuestion.setQuestionType(resQuestion.getQuestionType());

        if (resQuestion.getQuestionType().isMultipleOrCheckbox()) {
            if (resQuestion.getChoices() == null || resQuestion.getChoices().isEmpty())
                throw new IllegalArgumentException("resQuestion is MULTIPLE_CHOICE or CHECKBOX but does not have any choice!");

//            remove the association between Choice and Question
            dbQuestion.getChoices().forEach(choice -> choice.setQuestion(null));
//            remove all Choice from Question's list to ensure Hibernate detects the change
            dbQuestion.getChoices().clear();
            this.questionRepository.save(dbQuestion);
//            update choices
            for (Choice choice : resQuestion.getChoices())
                choice.setQuestion(dbQuestion);
            dbQuestion.getChoices().addAll(resQuestion.getChoices());
        } else {
//            remove the association between Choice and Question
            dbQuestion.getChoices().forEach(choice -> choice.setQuestion(null));
//            remove all Choice from Question's list to ensure Hibernate detects the change
            dbQuestion.getChoices().clear();
        }
        return this.questionRepository.save(dbQuestion);
    }

    public List<Question> getAllQuestionOfSurvey(Long projectId, Long surveyId) {
        checkParameter(projectId, surveyId);
        return this.questionRepository.findBySurvey_SurveyId(surveyId);
    }
}
