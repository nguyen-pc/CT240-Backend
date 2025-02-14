package com.project.formhub.service;

import java.lang.StackWalker.Option;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.formhub.domain.Project;
import com.project.formhub.domain.User;
import com.project.formhub.domain.response.project.ResCreateProjectDTO;
import com.project.formhub.domain.response.project.ResUpdateProject;
import com.project.formhub.repository.ProjectRepository;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Optional<Project> getProjectById(long projectId) {
        return projectRepository.findById(projectId);
    }

    public ResCreateProjectDTO createProject(Project project, User currentUser) {
        project.setUser(currentUser);
        projectRepository.save(project);

        ResCreateProjectDTO resCreateProjectDTO = new ResCreateProjectDTO();
        resCreateProjectDTO.setProjectId(project.getProjectId());
        resCreateProjectDTO.setProjectName(project.getProjectName());
        resCreateProjectDTO.setDescription(project.getDescription());
        resCreateProjectDTO.setCreatedAt(project.getCreatedAt());
        resCreateProjectDTO.setCreatedBy(currentUser.getEmail());

        return resCreateProjectDTO;

    }

    public ResUpdateProject updateProject(Project project, User currentUser) {

        Optional<Project> currentProjectOptional = projectRepository.findById(project.getProjectId());
        ResUpdateProject resUpdateProject = new ResUpdateProject();
        if (currentProjectOptional.isPresent()) {
            Project currentProject = currentProjectOptional.get();
            currentProject.setProjectName(project.getProjectName());
            currentProject.setDescription(project.getDescription());
            currentProject.setUpdatedAt(project.getUpdatedAt());
            currentProject.setUpdatedBy(currentUser.getEmail());

            this.projectRepository.save(currentProject);

            resUpdateProject.setProjectId(currentProject.getProjectId());
            resUpdateProject.setProjectName(currentProject.getProjectName());
            resUpdateProject.setDescription(currentProject.getDescription());
            resUpdateProject.setUpdatedAt(currentProject.getUpdatedAt());
            resUpdateProject.setUpdatedBy(currentUser.getEmail());

            return resUpdateProject;
        }
        return null;
    }

    public void handleDeleteProject(long projectId) {
        Optional<Project> projectOptional = this.projectRepository.findById(projectId);

        projectRepository.deleteById(projectId);
    }
}
