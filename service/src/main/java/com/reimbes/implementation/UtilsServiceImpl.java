package com.reimbes.implementation;

import com.reimbes.exception.ReimsException;
import com.reimbes.interfaces.UtilsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
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
import java.util.UUID;

import static com.reimbes.constant.UrlConstants.PROJECT_ROOT;
import static com.reimbes.constant.UrlConstants.STORAGE_FOLDERNAME;

/*
* Author: Rani Lasma Uli
*
* This class filled with the static methods.
* Static methods need to be collected in one class for the sake of 'unit test' using Powermockito class.
*
* */

@Service
public class UtilsServiceImpl implements UtilsService {

    private static Logger log = LoggerFactory.getLogger(UtilsServiceImpl.class);

    public String getPrincipalUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) return auth.getPrincipal().toString();
        return "";

    }

    // imagePath: relative path
    public void removeImage(String imagePath) {
        imagePath = StringUtils.cleanPath(PROJECT_ROOT + imagePath);
        try {
            Files.delete(Paths.get(imagePath));
        } catch (Exception e) {
            // attachments probably not found
            log.warn(e.getMessage());
        }

    }

    // Check file existance using relative file path
    public boolean isFileExists(String filepath) {
        String absolutePath = PROJECT_ROOT + filepath;
        boolean existance = Files.exists(Paths.get(absolutePath));
        return existance;
    }

    // cleanedPath: relative path
    public Path createFile(String cleanedPath, byte[] data) throws IOException {
        Path file = Files.write(Paths.get(PROJECT_ROOT + cleanedPath), data, StandardOpenOption.CREATE);
        return file.toAbsolutePath();
    }

    // cleanedPath: relative path
    public Path createDirectory(String cleanedPath) {
        log.info("Create directory: " + cleanedPath);
        boolean isDirectoryCreated = (new File(PROJECT_ROOT + cleanedPath)).mkdirs(); // create directory even parent directeroy haven't created yet
        log.info(String.format("Create directory, path: %s, succeed: %b", cleanedPath, isDirectoryCreated));
        return Paths.get(PROJECT_ROOT + cleanedPath);
    }

    // filepath: relative path of file
    public byte[] getFile(String filepath) throws IOException {
        try {
            return Files.readAllBytes(Paths.get(PROJECT_ROOT + filepath));
        }   catch (Exception e) {
            return Files.readAllBytes(Paths.get(String.format("%s/%s", PROJECT_ROOT, filepath)));
        }

    }

    public String generateFilename(String extension) {
        return String.format("%s.%s", UUID.randomUUID(), extension);
    }

    /*
    *
    * imageValue: string those contains encoded attachments value in Base64 format
    * userId: used to direct the user folder
    * subfolder: transaction, medical, etc.
    *
    * RETURN --> relative attachments path
    * */
    public String uploadImage(String imageValue, long userId, String subfolder) throws ReimsException {
        System.out.println(String.format("Value: %s, User Id: %d, Subfolder: %s", imageValue, userId, subfolder));

        String[] extractedByte = imageValue.split(",");
        String extension = extractedByte[0];
        String imagePath;

        // getByUser the extension
        if (extension.contains("jpg")) extension = "jpg";
        else if (extension.contains("png")) extension = "png";
        else if (extension.contains("jpeg")) extension = "jpeg";
        else return null;

        try {
            log.info("Decoding attachments.");
            byte[] imageByte = Base64.getDecoder().decode((extractedByte[1]
                    .getBytes(StandardCharsets.UTF_8)));

            log.info("Decoding attachments succeed.");
            log.info("Uploading the attachments...");

            /*
            * do upload attachments
            * */

            // confirm folder existence
            String folderPath = StringUtils.cleanPath(String.format("/%s/%d/%s/", STORAGE_FOLDERNAME, userId, subfolder));
            log.info("Done generate folder path.");

            if (!isFileExists(folderPath)) {
                log.info(String.format("Directory '%s' not found! Make directory first.", folderPath));
                createDirectory(folderPath);
            }

            imagePath = folderPath + generateFilename(extension); // relative path

            log.info(String.format("Write attachments in this path: %s", imagePath));
            Path path = createFile(imagePath, imageByte);
            log.info(String.format("Image writing succeed: %b", (path != null)));

        } catch (IOException e) {
            throw new ReimsException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, 500);
        }

        return imagePath; // relative path
    }

    // millis
    @Override
    public long getCurrentTime() {
        Instant instant = Instant.now();
        return instant.toEpochMilli();
    }
}
