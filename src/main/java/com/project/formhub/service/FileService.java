package com.project.formhub.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Collectors;

import com.project.formhub.util.error.StorageException;

@Service
public class FileService {
    @Value("${formhub.upload-file.base-uri}")
    private String baseURI;

    @Value("${formhub.upload-file.base-url}")
    private String baseURL;

    public void createUploadFolder(String folder) throws URISyntaxException {
        URI uri = new URI(folder);
        Path path = Paths.get(uri);
        File tmpDir = new File(path.toString());
        if (!tmpDir.isDirectory()) {
            try {
                Files.createDirectory(tmpDir.toPath());
                System.out.println("<<<<<< Create new directory successful, path = " + tmpDir.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("<<<<<< Skip making directory already exists");
        }
    }

    public String store(MultipartFile file, String folder) throws URISyntaxException, IOException {
        // create unique filename
        String finalName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
        URI uri = new URI(baseURI + folder + "/" + finalName);
        Path path = Paths.get(uri);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        }
        return finalName;
    }

    public long getFileLength(String fileName, String folder) throws URISyntaxException {
        URI uri = new URI(baseURI + folder + "/" + fileName);
        Path path = Paths.get(uri);

        File tmpDir = new File(path.toString());

        // file khong ton tai, hoac file la 1 directory => return 0
        if (!tmpDir.exists() || tmpDir.isDirectory()) {
            System.out.println("<<<<<< File not found or is a directory");
            return 0;
        }
        return tmpDir.length();
    }

    public InputStreamResource getResource(String fileName, String folder)
            throws URISyntaxException, FileNotFoundException {
        URI uri = new URI(baseURI + folder + "/" + fileName);
        Path path = Paths.get(uri);

        File file = new File(path.toString());
        return new InputStreamResource(new FileInputStream(file));
    }

    public void deleteFile(String fileName, String folder) throws URISyntaxException {
        URI uri = new URI(baseURI + folder + "/" + fileName);
        Path path = Paths.get(uri);

        File file = new File(path.toString());
        if (file.exists()) {
            file.delete();
        }
    }

    public List<String> listFiles(String folder) throws StorageException {
        if (folder.contains(",")) {
            throw new StorageException("Invalid folder name: " + folder);
        }

        String folderPath = Paths.get(baseURL, folder).toString();
        System.out.println("Checking folder path: " + folderPath); // Debug

        File dir = new File(folderPath);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new StorageException("Folder does not exist or is not a directory: " + folderPath);
        }

        File[] files = dir.listFiles();
        List<String> fileNames = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    fileNames.add(file.getName());
                }
            }
        }

        return fileNames;
    }

}
