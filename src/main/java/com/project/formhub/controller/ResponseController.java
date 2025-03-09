package com.project.formhub.controller;

import com.project.formhub.domain.Response;
import com.project.formhub.service.ResponseService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class ResponseController {
    private final ResponseService responseService;

    public ResponseController(ResponseService responseService) {
        this.responseService = responseService;
    }

    @PostMapping("/survey/{surveyId}/response")
    public ResponseEntity<?> createResponse(@RequestBody Response response, @PathVariable("surveyId") long surveyId) {
        try {
            Response savedResponse = this.responseService.createResponse(surveyId, response);
            return ResponseEntity.ok(savedResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Internal Error: " + e.getMessage());
        }
    }

    @GetMapping("/survey/{surveyId}/response/{responseId}")
    public ResponseEntity<?> getResponse(@PathVariable("surveyId") long surveyId,
            @PathVariable("responseId") long responseId) {
        try {
            Response dbResponse = this.responseService.getResponse(responseId);
            return ResponseEntity.ok(dbResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Internal error: " + e.getMessage());
        }
    }

    @DeleteMapping("/survey/{surveyId}/response/{responseId}")
    public ResponseEntity<?> deleteResponse(@PathVariable long surveyId, @PathVariable long responseId) {
        try {
            this.responseService.deleteResponse(surveyId, responseId);
            return ResponseEntity.ok("Delete successfully a response with id: " + responseId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Internal error: " + e.getMessage());
        }
    }

    @GetMapping("project/{projectId}/survey/{surveyId}/response/all")
    public ResponseEntity<?> getAllResponseOfSurvey(@PathVariable("projectId") long projectId,
            @PathVariable("surveyId") long surveyId) {
        try {
            List<Response> responses = this.responseService.getAllResponseOfSurvey(projectId, surveyId);
            return ResponseEntity.ok(responses);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Internal error: " + e.getMessage());
        }
    }
}
