package com.project.formhub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.project.formhub.domain.Response;

@Repository
public interface ResponseRepository extends JpaRepository<Response, Long>, JpaSpecificationExecutor<Response> {
    // List<Response> findByForm_Id(Long formId);
}