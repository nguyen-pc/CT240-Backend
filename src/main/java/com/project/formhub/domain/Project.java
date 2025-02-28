package com.project.formhub.domain;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.formhub.util.SecurityUtil;

import jakarta.annotation.Generated;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "projects")
@Getter
@Setter
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long projectId;

    @NotBlank(message = "Project name is required")
    private String projectName;

    private String description;
    private Instant createdAt;
    private Instant updatedAt;

    private String createdBy;
    private String updatedBy;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<Survey> surveys;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    public void handleBeforeCreate() {
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        this.updatedAt = Instant.now();
    }
}
