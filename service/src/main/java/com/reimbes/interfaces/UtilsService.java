package com.reimbes.interfaces;

import com.reimbes.ReimsUser;
import com.reimbes.exception.ReimsException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;

public interface UtilsService {
    String getPrincipalUsername();
    void removeImage(String imagePath); // imagePath: relative path
    byte[] getImage(ReimsUser currentUser, String imagePath) throws ReimsException; // imagePath: relative path
    boolean isFileExists(String filepath); // Check file existance using relative file path (filepath)
    Path createFile(String cleanedPath, byte[] data) throws IOException; // cleanedPath: relative path
    void createDirectory(String cleanedPath); // cleanedPath: relative path
    byte[] getFile(String filepath) throws IOException;
    String generateFilename(String extension);

    /*
     *
     * imageValue: string those contains encoded image value in Base64 format
     * userId: used to direct the user folder
     * subfolder: transaction, medical, etc.
     *
     * RETURN --> relative image path
     * */
    String uploadImage(String imageValue, long userId, String subfolder) throws ReimsException;

    static long getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.get(Calendar.YEAR);
    }

    static long countAge(Date dateOfBirth) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateOfBirth);
        return getCurrentYear() - calendar.get(Calendar.YEAR);
    }
}
