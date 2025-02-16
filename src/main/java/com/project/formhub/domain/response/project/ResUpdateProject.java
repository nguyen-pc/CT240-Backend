package com.project.formhub.domain.response.project;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResUpdateProject {
    private long projectId;
    private String projectName;
    private String description;
    private Instant updatedAt;
    private String updatedBy;
}
