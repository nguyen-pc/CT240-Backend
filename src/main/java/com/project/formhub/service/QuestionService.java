package com.project.formhub.service;

import com.project.formhub.domain.Choice;
import com.project.formhub.domain.Project;
import com.project.formhub.domain.Question;
import com.project.formhub.domain.Survey;
import com.project.formhub.domain.response.QuestionDTO;
import com.project.formhub.mapper.QuestionMapper;
import com.project.formhub.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public Question createQuestion(Long projectId, Long surveyId, Question question) {
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
//        set question for each choice in choices
        for (Choice choice : choices)
            choice.setQuestion(initialQuestion);

//        create question with choices
        initialQuestion.setChoices(choices);
        Question savedQuestion = this.questionRepository.save(initialQuestion);
        if (savedQuestion == null) {
            this.questionRepository.deleteById(initialQuestion.getQuestionId());
            throw new RuntimeException("create question is WRONG");
        }
        return savedQuestion;
    }

    public QuestionDTO getQuestionDTO(Long questionId) {
        Optional<Question> question = this.questionRepository.findById(questionId);
        return question.map(QuestionMapper::toDTO).orElse(null);
    }

    public Question getQuestion(Long questionId) {
        return this.questionRepository.findById(questionId).orElse(null);
    }

    public void deleteQuestion(Long questionId) {
        this.questionRepository.deleteById(questionId);
    }

    public Question updateQuestion(Long questionId, Question resQuestion) {
        Question dbQuestion = this.getQuestion(questionId);
        if (dbQuestion == null) {
            throw new IllegalArgumentException("Could not find to update question with id: " + questionId);
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

    public List<QuestionDTO> getAllQuestionOfSurvey(Long projectId, Long surveyId) {
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

        List<Question> questionList = this.questionRepository.findBySurvey_SurveyId(surveyId);
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (Question question : questionList)
            questionDTOList.add(QuestionMapper.toDTO(question));
        return questionDTOList;
    }
}
