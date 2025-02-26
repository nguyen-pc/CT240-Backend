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
import com.project.formhub.domain.User;
import com.project.formhub.domain.response.project.ProjectDTO;
import com.project.formhub.domain.response.project.ResCreateProjectDTO;
import com.project.formhub.domain.response.project.ResUpdateProject;
import com.project.formhub.repository.ProjectRepository;
import com.project.formhub.service.ProjectService;
import com.project.formhub.service.UserService;
import com.project.formhub.util.SecurityUtil;
import com.project.formhub.util.error.IdInvalidException;

import io.micrometer.core.instrument.Meter.Id;
import jakarta.validation.Valid;

@Controller
public class ProjectController {
    private final ProjectService projectService;
    private final UserService userService;
    private final SecurityUtil securityUtil;

    public ProjectController(ProjectService projectService, ProjectRepository projectRepository,
            SecurityUtil securityUtil, UserService userService) {
        this.projectService = projectService;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @GetMapping("/project")
    public ResponseEntity<List<Project>> getProjects() {
        List<Project> projects = projectService.handleGetProjects();
        return ResponseEntity.ok(projects);
    }

    @PostMapping("/project")
    public ResponseEntity<ResCreateProjectDTO> createProject(@Valid @RequestBody Project project)
            throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        User currentUser = userService.handleGetUserByUserName(email);
        ResCreateProjectDTO resProjectDTO = projectService.createProject(project, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(resProjectDTO);
    }

    @PutMapping("/project")
    public ResponseEntity<ResUpdateProject> updateProject(@RequestBody Project project) throws IdInvalidException {

        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        User currentUser = userService.handleGetUserByUserName(email);
        ResUpdateProject resProjectDTO = projectService.updateProject(project, currentUser);

        return ResponseEntity.status(HttpStatus.OK).body(resProjectDTO);
    }

    @GetMapping("/project/{id}")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable("id") long id) {
        Optional<Project> project = projectService.handleGetProjectById(id);
        if (project.isPresent()) {
            ProjectDTO projectDTO = new ProjectDTO();
            projectDTO.setProjectId(project.get().getProjectId());
            projectDTO.setProjectName(project.get().getProjectName());
            projectDTO.setDescription(project.get().getDescription());
            projectDTO.setCreatedAt(project.get().getCreatedAt());
            projectDTO.setCreatedBy(project.get().getCreatedBy());
            return ResponseEntity.ok(projectDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/project/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable("id") long id) {
        this.projectService.handleDeleteProject(id);
        return ResponseEntity.ok(null);
    }

}
