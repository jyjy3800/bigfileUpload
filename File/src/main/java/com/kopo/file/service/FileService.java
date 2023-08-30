package com.kopo.file.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

@Slf4j
@Service
public class FileService {
    public boolean fileUpload(MultipartFile file, int chunkNumber, int totalChunks, String key) throws IOException {
        String uploadDir = "video";
        String tempDir = "video/" + key;

        File dir = new File(tempDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String filename = file.getOriginalFilename() + ".part" + chunkNumber;

        Path filePath = Paths.get(tempDir, filename);
        Files.write(filePath, file.getBytes());

        if (chunkNumber == totalChunks-1) {
            
            String outputFilename = file.getOriginalFilename();
            Path outputFile = Paths.get(uploadDir, outputFilename);
            Files.createFile(outputFile);
            for (int i = 0; i < totalChunks; i++) {
                Path chunkFile = Paths.get(tempDir, file.getOriginalFilename() + ".part" + i);
                Files.write(outputFile, Files.readAllBytes(chunkFile), StandardOpenOption.APPEND);
            }
            deleteDirectory(Paths.get(tempDir));
          System.out.println("File uploaded successfully");
           
            return true;
        } else {
            return false;
        }
    }

    private void deleteDirectory(Path directory) throws IOException {
        try (Stream<Path> walk = Files.walk(directory)){
            walk.map(Path::toFile).forEach(File::delete);
        }
        Files.delete(directory);
    }

    public int getLastChunkNumber(String key) {
        Path temp = Paths.get("video", key);
        String[] list = temp.toFile().list();
        return list == null ? 0 : Math.max(list.length-2, 0);
    }
}