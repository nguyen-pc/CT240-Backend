package com.project.formhub.service;

import org.springframework.stereotype.Service;

import com.project.formhub.domain.Project;
import com.project.formhub.domain.User;
import com.project.formhub.domain.response.project.ResCreateProjectDTO;
import com.project.formhub.repository.ProjectRepository;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
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
}
