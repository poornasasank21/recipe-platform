package com.user.userservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {
    String uploadImage(String path, MultipartFile file) throws IOException;
}
