package com.reimbes.implementation;

import com.reimbes.ReimsUser;
import com.reimbes.constant.SecurityConstants;
import com.reimbes.exception.NotFoundException;
import com.reimbes.exception.ReimsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static com.reimbes.constant.ResponseCode.BAD_REQUEST;
import static com.reimbes.constant.UrlConstants.PROJECT_ROOT;
import static com.reimbes.constant.UrlConstants.STORAGE_FOLDER;

/*
* Author: Rani Lasma Uli
*
* This class filled with the static methods.
* Static methods need to be collected in one class for the sake of 'unit test' using Powermockito class.
*
* */

@Service
public class Utils {

    private static Logger log = LoggerFactory.getLogger(Utils.class);

    public String getPrincipalUsername() {
        try {
            return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        }   catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

    // imagePath: relative path
    public void removeImage(String imagePath) {
        imagePath = PROJECT_ROOT + imagePath;
        imagePath = StringUtils.cleanPath(imagePath);
        try {
            Files.delete(Paths.get(imagePath));
        }   catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] getImage(ReimsUser currentUser, String imagePath) throws ReimsException {

        if (!imagePath.contains(String.format("/%d/", currentUser.getId()))) throw new NotFoundException("Image");
        try {
            return getFile(imagePath);
        }   catch (IOException e) {
            throw new ReimsException(e.getMessage(), HttpStatus.BAD_REQUEST, BAD_REQUEST);
        }

    }

    // Check file existance using relative file path
    public boolean isFileExists(String filepath) {
        return Files.exists(Paths.get(PROJECT_ROOT + filepath));
    }

    // cleanedPath: relative path
    public Path createFile(String cleanedPath, byte[] data) throws IOException {
        Path file = Files.write(Paths.get(PROJECT_ROOT + cleanedPath), data, StandardOpenOption.CREATE);
        return file.toAbsolutePath();
    }

    // cleanedPath: relative path
    public void createDirectory(String cleanedPath) {
        log.info("Create directory: " + cleanedPath);
        boolean isDirectoryCreated = (new File(PROJECT_ROOT + cleanedPath)).mkdirs(); // create directory even parent directeroy haven't created yet
        log.info(String.format("Create directory, path: %s, succeed: %b", cleanedPath, isDirectoryCreated));
    }

    // filepath: relative path of file
    public byte[] getFile(String filepath) throws IOException {
        return Files.readAllBytes(Paths.get(PROJECT_ROOT + filepath));
    }

    public String generateFilename(String extension) {
        return String.format("%s.%s", UUID.randomUUID(), extension);
    }

    /*
    *
    * imageValue: string those contains encoded image value in Base64 format
    * userId: used to direct the user folder
    * subfolder: transaction, medical, etc.
    *
    * RETURN --> relative image path
    * */
    public String uploadImage(String imageValue, long userId, String subfolder) throws ReimsException {

        String[] extractedByte = imageValue.split(",");
        String extension = extractedByte[0];
        String imagePath;

        // getByUser the extension
        if (extension.contains("jpg")) extension = "jpg";
        else if (extension.contains("png")) extension = "png";
        else if (extension.contains("jpeg")) extension = "jpeg";
        else return null;

        try {
            log.info("Decoding image.");
            byte[] imageByte = Base64.getDecoder().decode((extractedByte[1]
                    .getBytes(StandardCharsets.UTF_8)));

            log.info("Decoding image succeed.");
            log.info("Uploading the image...");

            /*
            * do upload image
            * */

            // confirm folder existence
            String folderPath = StringUtils.cleanPath(String.format("%s/%d/%s/", STORAGE_FOLDER, userId, subfolder));
            log.info("Done generate folder path.");

            if (!isFileExists(folderPath)) {
                log.info(String.format("Directory '%s' not found! Make directory first.", folderPath));
                createDirectory(folderPath);
            }

            imagePath = folderPath + generateFilename(extension); // relative path

            log.info(String.format("Write image in this path: %s", imagePath));
            Path path = createFile(imagePath, imageByte);
            log.info(String.format("Image writing succeed: %b", (path != null)));

        } catch (IOException e) {
            throw new ReimsException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, 500);
        }

        return imagePath; // relative path
    }

    public static long getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.get(Calendar.YEAR);
    }

    public static long countAge(Date dateOfBirth) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateOfBirth);
        return getCurrentYear() - calendar.get(Calendar.YEAR);
    }

    // millis
    public long getCurrentTime() {
        Instant instant = Instant.now();
        return instant.toEpochMilli();
    }
}
