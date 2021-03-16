package com.prodactivv.app.core.files;

import com.prodactivv.app.config.DatabaseFiles;
import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.core.exceptions.UnreachableFileStorageTypeException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class DatabaseFileService {

    public enum StorageType {
        LOCAL("LOCAL"), LOCAL_SAFE("LOCAL_SAFE"), GOOGLE_DRIVE("GOOGLE_DRIVE"), DROPBOX("DROPBOX"), ONE_DRIVE("ONE_DRIVE");

        @Getter
        private final String type;

        StorageType(String type) {
            this.type = type;
        }
    }

    private final DatabaseFileRepository databaseFileRepository;
    private final DatabaseFiles databaseFiles;

    public InputStream downloadFile(Long id) throws NotFoundException, UnreachableFileStorageTypeException, FileNotFoundException {
        DatabaseFile file = databaseFileRepository.findById(id).orElseThrow(new NotFoundException(String.format("File %s not found", id)));

        File initialFile = getFile(file);
        return new FileInputStream(initialFile);
    }

    private File getFile(DatabaseFile file) throws UnreachableFileStorageTypeException {
        if (file.getFileLocationType().equalsIgnoreCase(StorageType.LOCAL_SAFE.type)) {
            return new File(databaseFiles.getLocalSafeStoragePath() + file.getFileName());
        } else if (file.getFileLocationType().equalsIgnoreCase(StorageType.LOCAL.type)) {
            return new File(databaseFiles.getLocalStoragePath() + file.getFileName());
        }

        throw new UnreachableFileStorageTypeException(String.format("Storage type %s not known", file.getFileLocationType()));
    }

    public DatabaseFile uploadFileToLocalStorage(MultipartFile file) throws IOException {
        return uploadFileLocal(file, StorageType.LOCAL, databaseFiles.getLocalStoragePath());
    }

    public DatabaseFile uploadFileToLocalSafeStorage(MultipartFile file) throws IOException {
        return uploadFileLocal(file, StorageType.LOCAL_SAFE, databaseFiles.getLocalSafeStoragePath());
    }

    public DatabaseFile uploadFile(StorageType storageType, MultipartFile file) throws IOException, UnsupportedStorageTypeException {
        switch (storageType) {
            case LOCAL_SAFE:
                if (!file.isEmpty()) {
                    return uploadFileToLocalSafeStorage(file);
                }

                throw new IOException("Empty file");
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

    private DatabaseFile uploadFileLocal(MultipartFile file, StorageType type, String filePath) throws IOException {
        String fileName;
        if (file.getOriginalFilename() != null) {
            fileName = file.getOriginalFilename().replaceAll("[\\s\\/\\\\\\|]", "");
        } else {
            fileName = "file_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        }

        Path path = Paths.get(filePath + fileName);
        Files.write(path, file.getBytes());
        return createDatabaseFile(path, type.type);
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
