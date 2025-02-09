package com.project.formhub.domain.response.file;

import java.time.Instant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResUploadFileDTO {
    private String fileName;
    private Instant uploadedAt;

    public ResUploadFileDTO(String fileName, Instant uploadedAt) {
        this.fileName = fileName;
        this.uploadedAt = uploadedAt;
    }
}
