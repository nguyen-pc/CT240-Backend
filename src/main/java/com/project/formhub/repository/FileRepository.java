package com.project.formhub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.formhub.domain.File;

public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findBySurvey_SurveyId(Long surveyId);

    Optional<File> findByFileName(String fileName);
}
