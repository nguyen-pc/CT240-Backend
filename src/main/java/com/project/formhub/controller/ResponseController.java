package com.project.formhub.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.project.formhub.domain.Response;
import com.project.formhub.domain.request.ReqResponseDTO;
import com.project.formhub.service.ResponseService;

import jakarta.validation.Valid;

@Controller
public class ResponseController {
    private final ResponseService responseService;

    public ResponseController(ResponseService responseService) {
        this.responseService = responseService;
    }

    @PostMapping("/project/{projectId}/survey/{surveyId}/response")
    public ResponseEntity<Response> getResponses(@PathVariable("surveyId") long surveyId,
            @Valid @RequestBody ReqResponseDTO request) {

        Response resSurvey = responseService.saveResponse(surveyId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(resSurvey);
    }

}
