package com.project.formhub.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.formhub.domain.File;
import com.project.formhub.domain.Project;
import com.project.formhub.domain.Survey;
import com.project.formhub.domain.response.file.ResUploadFileDTO;
import com.project.formhub.service.FileService;
import com.project.formhub.service.SurveyService;
import com.project.formhub.util.error.StorageException;

import jakarta.persistence.criteria.CriteriaBuilder.In;

@RestController
public class FileController {
    private final FileService fileService;
    private final SurveyService surveyService;

    @Value("${formhub.upload-file.base-uri}")
    private String baseURI;

    @Value("${formhub.upload-file.base-url}")
    private String baseURL;

    public FileController(FileService fileService, SurveyService surveyService) {
        this.fileService = fileService;
        this.surveyService = surveyService;
    }

    @PostMapping("/files")
    public ResponseEntity<ResUploadFileDTO> upload(@RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam("folder") String folder)
            throws URISyntaxException, IOException, StorageException {

        if (file == null || file.isEmpty()) {
            throw new StorageException("File is empty. Please upload a file");
        }
        long surveyId = Long.parseLong(folder);
        Survey survey = surveyService.getSurveyById(surveyId);

        this.fileService.createUploadFolder(baseURI + folder);

        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx", "mp4", "mp3");
        boolean isValid = allowedExtensions.stream().anyMatch(item -> fileName.toLowerCase().endsWith(item));

        if (!isValid) {
            throw new StorageException("Invalid file extension. Only allow" + allowedExtensions);
        }

        // Store files
        String uploadFile = this.fileService.store(file, folder, survey);
        ResUploadFileDTO res = new ResUploadFileDTO(uploadFile, Instant.now());
        return ResponseEntity.ok().body(res);

    }

    @GetMapping("/files")
    public ResponseEntity<Resource> download(@RequestParam(name = "folder", required = false) String folder,
            @RequestParam(name = "fileName", required = false) String fileName)
            throws URISyntaxException, FileNotFoundException, StorageException {
        if (fileName == null || folder == null) {
            throw new StorageException("Missing required parameters");
        }

        // check file exist (and not a directory)
        long fileLength = this.fileService.getFileLength(fileName, folder);
        if (fileLength == 0) {
            throw new StorageException("File not found or is a directory");
        }

        // download file from storage
        InputStreamResource resource = this.fileService.getResource(fileName, folder);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentLength(fileLength).contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @GetMapping("/files/{surveyId}/list")
    public ResponseEntity<List<File>> listFiles(
            @PathVariable("surveyId") long surveyId) throws StorageException {

        List<File> files = fileService.getFilesBySurveyId(surveyId);
        return ResponseEntity.ok(files);
    }

    @DeleteMapping("/files")
    public ResponseEntity<String> deleteFile(
            @RequestParam(name = "folder") String folder,
            @RequestParam(name = "fileName") String fileName) throws URISyntaxException, StorageException {

        if (fileName == null || folder == null) {
            throw new StorageException("Missing required parameters");
        }

        // Kiểm tra file có tồn tại trong CSDL không
        File fileRecord = fileService.getFileRecord(fileName, folder);
        if (fileRecord == null) {
            throw new StorageException("File not found in database");
        }

        // Kiểm tra file có tồn tại trong hệ thống không
        long fileLength = this.fileService.getFileLength(fileName, folder);
        if (fileLength == 0) {
            throw new StorageException("File not found or is a directory");
        }

        // Xóa file trên hệ thống
        this.fileService.deleteFile(fileName, folder);

        // Xóa file trong CSDL
        this.fileService.deleteFileRecord(fileRecord);

        return ResponseEntity.ok("File deleted successfully");
    }

}
