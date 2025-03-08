package com.project.formhub.controller;

import com.project.formhub.domain.Question;
import com.project.formhub.domain.response.QuestionDTO;
import com.project.formhub.service.ProjectService;
import com.project.formhub.service.QuestionService;
import com.project.formhub.service.SurveyService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class QuestionController {
    public final QuestionService questionService;
    public final SurveyService surveyService;
    public final ProjectService projectService;

    public QuestionController(QuestionService questionService, SurveyService surveyService,
            ProjectService projectService) {
        this.questionService = questionService;
        this.surveyService = surveyService;
        this.projectService = projectService;
    }

    // Create the new question
    @PostMapping("/project/{projectId}/survey/{surveyId}/question")
    public ResponseEntity<?> createQuestion(@PathVariable("projectId") long projectId,
            @PathVariable("surveyId") long surveyId, @RequestBody Question question) {
        try {
            Question createdQuestion = this.questionService.createQuestion(projectId, surveyId, question);
            return ResponseEntity.ok(createdQuestion);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Internal error: " + e.getMessage());
        }
    }

    // Update a question with idQuestion
    @PutMapping("/project/{projectId}/survey/{surveyId}/question/{questionId}")
    public ResponseEntity<?> updateQuestion(@RequestBody Question resQuestion,
            @PathVariable("projectId") long projectId,
            @PathVariable("surveyId") long surveyId,
            @PathVariable("questionId") long questionId) {
        try {
            Question updatedQuestion = this.questionService.updateQuestion(questionId, resQuestion);
            return ResponseEntity.ok(updatedQuestion);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Internal error: " + e.getMessage());
        }
    }

    // Get a question with id
    @GetMapping("/project/{projectId}/survey/{surveyId}/question/{questionId}")
    public ResponseEntity<?> getQuestion(@PathVariable("projectId") long projectId,
            @PathVariable("surveyId") long surveyId,
            @PathVariable("questionId") long questionId) {
        QuestionDTO dbQuestion = this.questionService.getQuestionDTO(questionId);
        if (dbQuestion == null)
            return ResponseEntity.badRequest().body("Could not find question with id: " + questionId);
        else
            return ResponseEntity.ok(dbQuestion);
    }

    // Delete a question with id
    @DeleteMapping("/project/{projectId}/survey/{surveyId}/question/{questionId}")
    public ResponseEntity<?> deleteQuestion(@PathVariable("projectId") long projectId,
            @PathVariable("surveyId") long surveyId,
            @PathVariable("questionId") long questionId) {
        try {
            this.questionService.deleteQuestion(questionId);
            return ResponseEntity.ok("Deleted successfully question with id: " + questionId);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Internal error: " + e.getMessage());
        }
    }

    @GetMapping("/project/{projectId}/survey/{surveyId}/question/all")
    public ResponseEntity<?> getAllQuestionOfSurvey(@PathVariable("projectId") long projectId,
            @PathVariable("surveyId") long surveyId) {
        try {
            List<QuestionDTO> questions = this.questionService.getAllQuestionOfSurvey(projectId, surveyId);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Internal error: " + e.getMessage());
        }
    }
}
