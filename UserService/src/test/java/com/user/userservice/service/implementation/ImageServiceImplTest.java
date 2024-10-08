package com.user.userservice.service.implementation;

import com.user.userservice.exception.InvalidInputException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceImplTest {

    @TempDir
    Path temporaryFolder;
    @InjectMocks
    private ImageServiceImpl imageService;

    @Test
    void testUploadImageCreatesNewFolder() throws IOException {
        String directoryName = "newFolder";
        Path newFolderPath = Paths.get(directoryName);
        String path = newFolderPath.toString();
        MultipartFile file = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                "Dummy image content".getBytes()
        );

        String result = imageService.uploadImage(path, file);

        assertTrue(Files.exists(newFolderPath), "Directory should be created by the method");
        assertNotNull(result, "The result should not be null");
    }

    @Test
    void testUploadImageExistingFolder() throws IOException {
        String directoryName = "existingFolder";
        Path existingFolderPath = Paths.get(directoryName);
        Files.createDirectories(existingFolderPath);
        String path = existingFolderPath.toString();
        MultipartFile file = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                "Dummy image content".getBytes()
        );

        String result = imageService.uploadImage(path, file);

        assertTrue(Files.exists(existingFolderPath), "Directory should already exist");
        assertNotNull(result, "The result should not be null");
    }

    @Test
    void testUploadImageWithNullFileThrowsInvalidInputException() {
        String path = temporaryFolder.toString();
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            imageService.uploadImage(path, null);
        });

        assertEquals("File must not be empty", exception.getMessage());
    }

    @Test
    void testUploadImageWithEmptyFileThrowsInvalidInputException() {
        String path = temporaryFolder.toString();
        MultipartFile emptyFile = new MockMultipartFile("file", "", "image/jpeg", new byte[0]);

        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            imageService.uploadImage(path, emptyFile);
        });

        assertEquals("File must not be empty", exception.getMessage());
    }

    @Test
    void testUploadImageWithInvalidFileExtensionThrowsInvalidInputException() {
        String path = temporaryFolder.toString();
        MultipartFile fileWithInvalidExtension = new MockMultipartFile("file", "test.txt", "text/plain", "Dummy text content".getBytes());

        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            imageService.uploadImage(path, fileWithInvalidExtension);
        });

        assertEquals("Invalid file type. Only PNG, JPG, JPEG, and SVG are allowed.", exception.getMessage());
    }

    @Test
    void testUploadImageWithNullFileNameThrowsInvalidInputException() {
        String path = temporaryFolder.toString();
        MultipartFile fileWithNoName = new MockMultipartFile("file", null, "image/jpeg", "Dummy image content".getBytes());

        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            imageService.uploadImage(path, fileWithNoName);
        });

        assertEquals("File name must not be null", exception.getMessage());
    }
}