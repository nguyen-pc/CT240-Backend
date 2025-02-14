package com.project.formhub.domain.response.project;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResCreateProjectDTO {
    private long projectId;
    private String projectName;

    private String description;
    private Instant createdAt;

    private String createdBy;

}
