package com.project.formhub.domain.response.project;

import java.time.Instant;

import lombok.Data;

@Data
public class ProjectDTO {
    private Long projectId;
    private String projectName;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    public ProjectDTO() {
    }

    public ProjectDTO(Long projectId, String projectName, String description, Instant createdAt, Instant updatedAt,
            String createdBy, String updatedBy) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }
}
