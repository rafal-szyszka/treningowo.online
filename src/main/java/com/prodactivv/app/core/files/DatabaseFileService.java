package com.prodactivv.app.core.files;

import com.prodactivv.app.config.DatabaseFiles;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class DatabaseFileService {

    public enum StorageType {
        LOCAL("LOCAL"), GOOGLE_DRIVE("GOOGLE_DRIVE"), DROPBOX("DROPBOX"), ONE_DRIVE("ONE_DRIVE");

        @Getter
        private final String type;

        StorageType(String type) {
            this.type = type;
        }
    }

    private final DatabaseFileRepository databaseFileRepository;
    private final DatabaseFiles databaseFiles;


    public DatabaseFile uploadFileToLocalStorage(MultipartFile file) throws IOException {
        String fileName;
        if (file.getOriginalFilename() != null) {
             fileName = file.getOriginalFilename().replaceAll("[\\s\\/\\\\\\|]", "");
        } else {
            fileName = "file_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        }

        Path path = Paths.get(databaseFiles.getLocalStoragePath() + fileName);

        Files.write(path, file.getBytes());

        return createDatabaseFile(path, StorageType.LOCAL.type);
    }

    public DatabaseFile uploadFile(StorageType storageType, MultipartFile file) throws IOException, UnsupportedStorageTypeException {
        switch (storageType) {
            case LOCAL:
                if (!file.isEmpty()) {
                    return uploadFileToLocalStorage(file);
                }

                throw new IOException("Empty file");
            case GOOGLE_DRIVE:
            case DROPBOX:
            case ONE_DRIVE:
            default:
                throw new UnsupportedStorageTypeException(storageType);
        }
    }

    private DatabaseFile createDatabaseFile(Path path, String type) {
        DatabaseFile databaseFile = DatabaseFile.builder()
                .fileLocationType(type)
                .fileLocation(String.format("%s%s", databaseFiles.getFilePublicAddress(), path.getFileName().toString()))
                .fileName(path.getFileName().toString())
                .build();

        return databaseFileRepository.save(databaseFile);
    }
}
