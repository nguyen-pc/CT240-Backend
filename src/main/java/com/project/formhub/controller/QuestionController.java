package com.project.formhub.controller;

import com.project.formhub.domain.Project;
import com.project.formhub.domain.Question;
import com.project.formhub.domain.Survey;
import com.project.formhub.service.ProjectService;
import com.project.formhub.service.QuestionService;
import com.project.formhub.service.SurveyService;
import com.project.formhub.util.error.IdInvalidException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
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

//    create the new question
    @PostMapping("/project/{projectId}/survey/{surveyId}/question")
    public ResponseEntity<?> createQuestion(@PathVariable long projectId, @PathVariable long surveyId, @RequestBody Question question) {
        Optional <Project> dbProject = this.projectService.getProjectById(projectId);
//        Check if the Project exists
        if(dbProject.isEmpty())
            return ResponseEntity.badRequest().body("Could not find Project in DB");
        else{
//            The Project is available.
            Survey dbSurvey = this.surveyService.getSurveyById(surveyId);
//            Check if the Survey exists
            if(dbSurvey==null)
                return ResponseEntity.badRequest().body("Could not find Survey in DB");
            else{
//                The Survey is available
//                Check if the Project contains the Survey
                if(dbSurvey.getProject().getProjectId()==projectId)
                    question.setSurvey(dbSurvey);
                else
                    return ResponseEntity.badRequest().body("Project #"+projectId+" DOES NOT have survey #"+surveyId);
            }
        }
        Question savedQuestion = this.questionService.createQuestion(question);
        if (savedQuestion == null)
            return ResponseEntity.badRequest().body("create question is WRONG");
        else
            return ResponseEntity.ok(savedQuestion);
    }

    @PutMapping("/project/{projectId}/survey/{surveyId}/question/{idQuestion}")
    public ResponseEntity<?> updateQuestion(@RequestBody Question resQuestion, @PathVariable long projectId, @PathVariable long surveyId, @PathVariable long idQuestion){
        Question dbQuestion = this.questionService.getQuestion(idQuestion);
        if(dbQuestion==null)
            return ResponseEntity.badRequest().body("Could not find to update question with id: "+idQuestion);
        else{
            Question updatedQuestion = this.questionService.updateQuestion(idQuestion,resQuestion);
            if(updatedQuestion==null)
                return ResponseEntity.badRequest().body("update question with id: "+idQuestion+"is WRONG");
            else
                return ResponseEntity.ok(updatedQuestion);
        }
    }

//    Get a question with id
    @GetMapping("/project/{projectId}/survey/{surveyId}/question/{idQuestion}")
    public ResponseEntity<?> getQuestion(@PathVariable long projectId, @PathVariable long surveyId, @PathVariable long idQuestion){
        Question dbQuestion = this.questionService.getQuestion(idQuestion);
        if (dbQuestion == null)
            return ResponseEntity.badRequest().body("Could not find question with id: " + idQuestion);
        else
            return ResponseEntity.ok(dbQuestion);
    }

//    Delete a question with id
    @DeleteMapping("/project/{projectId}/survey/{surveyId}/question/{idQuestion}")
    public ResponseEntity<?> deleteQuestion(@PathVariable long projectId, @PathVariable long surveyId, @PathVariable long idQuestion){
        Question dbQuestion = this.questionService.getQuestion(idQuestion);
        if(dbQuestion == null)
            return ResponseEntity.badRequest().body("Could not find to delete question with id: "+idQuestion);
        else{
            this.questionService.deleteQuestion(idQuestion);
            return ResponseEntity.ok().body("Deleted successfully question with id: "+idQuestion);
        }
    }
}
