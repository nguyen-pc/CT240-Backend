package com.project.formhub.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties.Http;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.project.formhub.domain.Project;
import com.project.formhub.domain.Survey;
import com.project.formhub.domain.User;
import com.project.formhub.domain.response.project.ProjectDTO;
import com.project.formhub.domain.response.project.ResCreateProjectDTO;
import com.project.formhub.domain.response.project.ResUpdateProject;
import com.project.formhub.repository.ProjectRepository;
import com.project.formhub.service.ProjectService;
import com.project.formhub.service.SurveyService;
import com.project.formhub.service.UserService;
import com.project.formhub.util.SecurityUtil;
import com.project.formhub.util.error.IdInvalidException;

import io.micrometer.core.instrument.Meter.Id;
import jakarta.validation.Valid;

@Controller
public class SurveyController {
    private final SurveyService surveyService;
    private final ProjectService projectService;
    private final UserService userService;
    private final SecurityUtil securityUtil;

    public SurveyController(SurveyService surveyService, ProjectRepository projectRepository,
            SecurityUtil securityUtil, UserService userService, ProjectService projectService) {
        this.surveyService = surveyService;
        this.projectService = projectService;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @GetMapping("/project/{id}/survey/{surveyId}")
    public ResponseEntity<Survey> getSurvey(@PathVariable("id") long id, @PathVariable("surveyId") long surveyId) {
        Survey survey = surveyService.getSurveyById(surveyId);
        return ResponseEntity.ok(survey);
    }

    @GetMapping("/project/{id}/survey")
    public ResponseEntity<List<Survey>> getSurveys(@PathVariable("id") long id) {
        List<Survey> surveys = surveyService.getSurveysByProjectId(id);
        return ResponseEntity.ok(surveys);
    }

    @PostMapping("/project/{id}/survey")
    public ResponseEntity<Survey> createSurvey(@PathVariable("id") long id,
            @Valid @RequestBody Survey survey)
            throws IdInvalidException {

        Project project = projectService.getProjectById(id).get();
        survey.setProject(project);
        Survey resSurvey = surveyService.createSurvey(survey);

        return ResponseEntity.status(HttpStatus.CREATED).body(resSurvey);
    }

    @PutMapping("/project/{id}/survey")
    public ResponseEntity<Survey> updateSurvey(@PathVariable("id") long id, @RequestBody Survey survey)
            throws IdInvalidException {

        // Project project = projectService.getProjectById(id).get();
        // survey.setProject(project);
        Survey resSurvey = surveyService.updateSurvey(survey);

        return ResponseEntity.status(HttpStatus.OK).body(resSurvey);
    }

    @DeleteMapping("/project/{id}/survey/{surveyId}")
    public ResponseEntity<Void> deleteSurvey(@PathVariable("id") long id, @PathVariable("surveyId") long surveyId) {
        this.surveyService.deleteSurvey(surveyId);
        return ResponseEntity.ok(null);
    }

}
