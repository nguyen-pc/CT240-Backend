package com.project.formhub.service;

import java.lang.StackWalker.Option;
import java.util.List;
import java.util.Optional;

import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;

import com.project.formhub.domain.Project;
import com.project.formhub.domain.Survey;
import com.project.formhub.repository.SurveyRepository;

@Service
public class SurveyService {
    private final SurveyRepository surveyRepository;
    private final ProjectService projectService;

    public SurveyService(SurveyRepository surveyRepository, ProjectService projectService) {
        this.projectService = projectService;
        this.surveyRepository = surveyRepository;
    }

    public Survey createSurvey(Survey survey) {
        return surveyRepository.save(survey);
    }

    public Survey getSurveyById(long surveyId) {
        return surveyRepository.findById(surveyId).orElse(null);
    }

    public Survey updateSurvey(Survey survey) {
        Optional<Survey> currentSurveyOptional = surveyRepository.findById(survey.getSurveyId());
        if (currentSurveyOptional.isPresent()) {
            Survey currentSurvey = currentSurveyOptional.get();
            currentSurvey.setSurveyName(survey.getSurveyName());
            currentSurvey.setDescription(survey.getDescription());
            currentSurvey.setUpdatedAt(survey.getUpdatedAt());
            currentSurvey.setUpdatedBy(survey.getUpdatedBy());
            return this.surveyRepository.save(currentSurvey);
        }

        return null;
    }

    public void deleteSurvey(long surveyId) {
        surveyRepository.deleteById(surveyId);
    }

    public List<Survey> getSurveysByProjectId(long projectId) {
        Project project = projectService.getProjectById(projectId).orElse(null);
        if (project == null) {
            return null;
        }
        return surveyRepository.findByProject_ProjectId(project.getProjectId());
    }
}
