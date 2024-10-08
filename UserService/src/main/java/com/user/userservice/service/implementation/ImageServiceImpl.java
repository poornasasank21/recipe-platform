package com.user.userservice.service.implementation;

import com.user.userservice.constants.ErrorConstants;
import com.user.userservice.exception.InvalidInputException;
import com.user.userservice.service.ImageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {
    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new InvalidInputException(ErrorConstants.FILE_MUST_NOT_BE_EMPTY);
        }

        String fileExtension = getString(file);

        String uniqueIdentifier = UUID.randomUUID().toString();
        String newFileName = uniqueIdentifier + fileExtension;

        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        Files.copy(file.getInputStream(), Paths.get(path, newFileName));
        return newFileName;
    }

    private String getString(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new InvalidInputException(ErrorConstants.FILE_NAME_MUST_NOT_BE_NULL);
        }

        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        List<String> allowedExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".bmp");
        if (!allowedExtensions.contains(fileExtension)) {
            throw new InvalidInputException(ErrorConstants.INVALID_FILE_TYPE);
        }
        return fileExtension;
    }
}
