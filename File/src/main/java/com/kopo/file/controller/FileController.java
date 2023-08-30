package com.kopo.file.controller;

import com.kopo.file.service.FileService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService = new FileService();

    @GetMapping("/bigfile")
    public String chunkUploadPage() {
        return "bigfile";
    }

    @ResponseBody
    @GetMapping("/bigfile/upload/{key}")
    public ResponseEntity<?> getLastChunkNumber(@PathVariable String key) {
        return ResponseEntity.ok(fileService.getLastChunkNumber(key));
    }

    @ResponseBody
    @PostMapping("/bigfile/upload/{key}")
    public ResponseEntity<String> fileUpload(@RequestParam("chunk") MultipartFile file,
                                              @RequestParam("chunkNumber") int chunkNumber,
                                              @RequestParam("totalChunks") int totalChunks,
                                              @PathVariable String key) throws IOException {
        boolean isDone = fileService.fileUpload(file, chunkNumber, totalChunks, key);

        return isDone ?
                ResponseEntity.ok("완료") :
                ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).build();
    }
}