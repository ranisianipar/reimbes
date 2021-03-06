package com.reimbes.interfaces;

import com.reimbes.exception.ReimsException;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;

public interface UtilsService {
    String getPrincipalUsername();
    void removeImage(String imagePath); // imagePath: relative path
    boolean isFileExists(String filepath); // Check file existance using relative file path (filepath)
    Path createFile(String cleanedPath, byte[] data) throws IOException; // cleanedPath: relative path
    Path createDirectory(String cleanedPath); // cleanedPath: relative path
    byte[] getFile(String filepath) throws IOException;
    String generateFilename(String extension);
    Pageable getPageRequest(Pageable pageRequest);

    /*
     *
     * imageValue: string those contains encoded attachments value in Base64 format
     * userId: used to direct the user folder
     * subfolder: transaction, medical, etc.
     *
     * RETURN --> relative attachments path
     * */
    String uploadImage(String imageValue, long userId, String subfolder) throws ReimsException;
    long getCurrentTime();

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
