package com.project.formhub.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.project.formhub.domain.Question;
import com.project.formhub.domain.Survey;
import com.project.formhub.domain.request.ReqQuestionDTO;
import com.project.formhub.service.QuestionService;
import com.project.formhub.service.SurveyService;

import jakarta.validation.Valid;

@Controller
public class QuestionController {
    private final QuestionService questionService;
    private final SurveyService surveyService;

    public QuestionController(QuestionService questionService, SurveyService surveyService) {
        this.questionService = questionService;
        this.surveyService = surveyService;
    }

    @PostMapping("/project/{projectId}/survey/{surveyId}/question")
    public ResponseEntity<Question> createQuestion(@Valid @RequestBody ReqQuestionDTO question,
            @PathVariable("surveyId") long surveyId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(questionService.createQuestion(question, surveyId));

    }
}
