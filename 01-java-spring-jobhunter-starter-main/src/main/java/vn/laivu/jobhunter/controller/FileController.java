package vn.laivu.jobhunter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.laivu.jobhunter.domain.response.file.ResUploadFileDTO;
import vn.laivu.jobhunter.service.FileService;
import vn.laivu.jobhunter.util.Annotation.ApiMessage;
import vn.laivu.jobhunter.util.error.StorageException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class FileController {

    @Value("${laivu.upload-file.base-uri}")
    private String baseUri;

    private FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/files")
    @ApiMessage("Upload success")
    public ResponseEntity<ResUploadFileDTO> upload(
            @RequestParam(name = "file", required = false) MultipartFile file, // This parameter is required
            @RequestParam("folder") String folder) throws URISyntaxException, IOException, StorageException {
        // skip validate (File is empty - File extensions - File size (max = 5MB))
        if (file == null || file.isEmpty()) {
            throw new StorageException("Chưa tải file, Vui lòng upload 1 file");
        }

        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        // kiểm tra tên file có đứa hậu tố mở rộng trong List không => boolean
        boolean isValid = allowedExtensions
                .stream()
                .anyMatch(item -> fileName.toLowerCase().endsWith(item));
        if (!isValid) {
            throw new StorageException("Không được phép tải file định dạng " + allowedExtensions.toString());
        }

        // create dir if not exist
        this.fileService.createDirectory(baseUri + folder);
        // storage file
        String uploadFile = this.fileService.store(file, folder);

        ResUploadFileDTO res = new ResUploadFileDTO(uploadFile, Instant.now());

        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/files")
    @ApiMessage("Download a file")
    public ResponseEntity<InputStreamResource> download(
            @RequestParam(name = "fileName", required = false) String fileName,
            @RequestParam(name = "folder", required = false) String folder)
            throws StorageException, URISyntaxException, FileNotFoundException {
        if (fileName == null || folder == null) {
            throw new StorageException("Miss tham số (fileName và folder)");
        }

        // check file exist (and not a dir)
        long fileLength = this.fileService.getFileLength(fileName, folder);
        if (fileLength == 0) {
            throw new StorageException("File name: " + fileName + " not found");
        }

        // download file
        InputStreamResource resource = this.fileService.getResource(fileName, folder);

        return ResponseEntity
                .ok()
                // tự động set fileName khi download
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentLength(fileLength)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}

