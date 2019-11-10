package com.reimbes.implementation;

import com.reimbes.constant.UrlConstants;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

/*
* Author: Rani Lasma Uli
*
* This class filled with the static methods.
* Static methods need to be collected in one class for the sake of 'unit test' using Powermockito class.
*
* */

@Service
public class Utils {

    public String getUsername() {
        try {
            return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        }   catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

    public void removeImage(String imagePath) {
        imagePath = StringUtils.cleanPath(UrlConstants.IMAGE_FOLDER_PATH + imagePath);
        try {
            Files.delete(Paths.get(imagePath));
        }   catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] getImageByImagePath(String imagePath) throws IOException {
        return Files.readAllBytes(Paths.get(UrlConstants.IMAGE_FOLDER_PATH + imagePath));
    }

    public boolean isFileExists(String filepath) {
        return Files.exists(Paths.get(filepath));
    }

    public void createFile(String cleanedPath, byte[] data) throws IOException {
        Files.write(Paths.get(cleanedPath), data, StandardOpenOption.CREATE);
    }

    public void createDirectory(String cleanedPath) throws IOException {
        Files.createDirectory(Paths.get(cleanedPath));
    }

    public byte[] getFile(String filename) throws IOException {
        return Files.readAllBytes(Paths.get(filename));
    }

    public String getFilePath(String username, MultipartFile file) {
        return String.format("%s/%s.%s", username, UUID.randomUUID(), file.getContentType());
    }
}
