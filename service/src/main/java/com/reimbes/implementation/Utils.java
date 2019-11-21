package com.reimbes.implementation;

import com.reimbes.constant.UrlConstants;
import com.reimbes.exception.FormatTypeError;
import com.reimbes.exception.ReimsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static com.reimbes.constant.UrlConstants.FOLDER_PATH;

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

    public String getUsername() {
        try {
            return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        }   catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

    public void removeImage(String imagePath) {
        imagePath = StringUtils.cleanPath(FOLDER_PATH + imagePath);
        try {
            Files.delete(Paths.get(imagePath));
        }   catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] getImageByImagePath(String imagePath) throws IOException {
        return Files.readAllBytes(Paths.get(FOLDER_PATH + imagePath));
    }

    public boolean isFileExists(String filepath) {
        return Files.exists(Paths.get(filepath));
    }

    public void createFile(String cleanedPath, byte[] data) throws IOException {
        Files.write(Paths.get(cleanedPath), data, StandardOpenOption.CREATE);
    }

    public void createDirectory(String cleanedPath) {
        log.info("Create directory: " + cleanedPath);
        (new File(cleanedPath)).mkdirs(); // create directory even parent directeroy haven't created yet
        log.info("[DONE] Create directory: " + cleanedPath);
    }

    public byte[] getFile(String filename) throws IOException {
        return Files.readAllBytes(Paths.get(filename));
    }

    public String getFilename(String extension) {
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

        // get the extension
        if (extension.contains("jpg")) extension = "jpg";
        else if (extension.contains("png")) extension = "png";
        else if (extension.contains("jpeg")) extension = "jpeg";
        else return null;

        try {
            byte[] imageByte = Base64.getDecoder().decode((extractedByte[1]
                    .getBytes(StandardCharsets.UTF_8)));

            log.info("Decoding image byte succeed.");
            log.info("Uploading the image...");


            /*
            * do upload image
            * */

            // confirm folder existence
            String folderPath = StringUtils.cleanPath(String.format("%d/%s/", userId, subfolder));
            log.info("Done generate folder path.");

            if (!isFileExists(folderPath)) {
                log.info("Directory '" + folderPath + "' not found! Make directory first.");
                createDirectory(folderPath);
            }

            imagePath = folderPath + getFilename(extension);
            log.info("Write image in this path: " + imagePath);
            createFile(imagePath, imageByte);
        } catch (IOException e) {
            throw new ReimsException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, 500);
        }

        return imagePath;
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
}
