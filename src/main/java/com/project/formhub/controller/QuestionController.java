package com.project.formhub.controller;

import com.project.formhub.domain.Choice;
import com.project.formhub.domain.Project;
import com.project.formhub.domain.Question;
import com.project.formhub.domain.Survey;
import com.project.formhub.service.ProjectService;
import com.project.formhub.service.QuestionService;
import com.project.formhub.service.SurveyService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class QuestionController {
    public final QuestionService questionService;
    public final SurveyService surveyService;
    public final ProjectService projectService;

    public QuestionController(QuestionService questionService, SurveyService surveyService, ProjectService projectService) {
        this.questionService = questionService;
        this.surveyService = surveyService;
        this.projectService = projectService;
    }

    //    Create the new question
    @PostMapping("/project/{projectId}/survey/{surveyId}/question")
    public ResponseEntity<?> createQuestion(@PathVariable long projectId, @PathVariable long surveyId, @RequestBody Question question) {
        Optional<Project> dbProject = this.projectService.getProjectById(projectId);
//        Check if the Project exists
        if (dbProject.isEmpty())
            return ResponseEntity.badRequest().body("Could not find Project in DB");
//        Check if the Survey exists
        Survey dbSurvey = this.surveyService.getSurveyById(surveyId);
        if (dbSurvey == null)
            return ResponseEntity.badRequest().body("Could not find Survey in DB");
//        Check if the Project contains the Survey
        if (dbSurvey.getProject().getProjectId() != projectId)
            return ResponseEntity.badRequest().body("Project #" + projectId + " DOES NOT have survey #" + surveyId);

        Question.QuestionType type = question.getQuestionType();

//        check if MULTIPLE_CHOICE or CHECKBOX have not any choice
        if (type == Question.QuestionType.MULTIPLE_CHOICE && question.getChoices() == null)
            return ResponseEntity.badRequest().body("Multiple choice question could not have empty choices");
        if (type == Question.QuestionType.CHECKBOX && question.getChoices() == null)
            return ResponseEntity.badRequest().body("Checkbox question could not have empty choices");

        question.setSurvey(dbSurvey);

//        create question without choices
        List<Choice> choices = question.getChoices();
        question.setChoices(null);
        Question initialQuestion = this.questionService.createQuestion(question);
        if (initialQuestion == null)
            return ResponseEntity.badRequest().body("init question is WRONG");
//        set question for each choice in choices
        for (Choice choice : choices) {
            choice.setQuestion(initialQuestion);
        }

//        create question with choices
        initialQuestion.setChoices(choices);
        Question savedQuestion = this.questionService.createQuestion(initialQuestion);
        if (savedQuestion == null) {
            this.questionService.deleteQuestion(initialQuestion.getQuestionId());
            return ResponseEntity.badRequest().body("create question is WRONG");
        } else
            return ResponseEntity.ok(initialQuestion);
    }

    //    Update a question with idQuestion
    @PutMapping("/project/{projectId}/survey/{surveyId}/question/{idQuestion}")
    public ResponseEntity<?> updateQuestion(@RequestBody Question resQuestion, @PathVariable long projectId, @PathVariable long surveyId, @PathVariable long idQuestion) {
        Question dbQuestion = this.questionService.getQuestion(idQuestion);
        if (dbQuestion == null)
            return ResponseEntity.badRequest().body("Could not find to update question with id: " + idQuestion);
        else {
            Question updatedQuestion = this.questionService.updateQuestion(idQuestion, resQuestion);
            if (updatedQuestion == null)
                return ResponseEntity.badRequest().body("update question with id: " + idQuestion + "is WRONG");
            else
                return ResponseEntity.ok(updatedQuestion);
        }
    }

    //    Get a question with id
    @GetMapping("/project/{projectId}/survey/{surveyId}/question/{idQuestion}")
    public ResponseEntity<?> getQuestion(@PathVariable long projectId, @PathVariable long surveyId, @PathVariable long idQuestion) {
        Question dbQuestion = this.questionService.getQuestion(idQuestion);
        if (dbQuestion == null)
            return ResponseEntity.badRequest().body("Could not find question with id: " + idQuestion);
        else
            return ResponseEntity.ok(dbQuestion);
    }

    //    Delete a question with id
    @DeleteMapping("/project/{projectId}/survey/{surveyId}/question/{idQuestion}")
    public ResponseEntity<?> deleteQuestion(@PathVariable long projectId, @PathVariable long surveyId, @PathVariable long idQuestion) {
        Question dbQuestion = this.questionService.getQuestion(idQuestion);
        if (dbQuestion == null)
            return ResponseEntity.badRequest().body("Could not find to delete question with id: " + idQuestion);
        else {
            this.questionService.deleteQuestion(idQuestion);
            return ResponseEntity.ok().body("Deleted successfully question with id: " + idQuestion);
        }
    }
}
