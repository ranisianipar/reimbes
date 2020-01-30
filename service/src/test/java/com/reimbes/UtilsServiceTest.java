package com.reimbes;

import com.reimbes.exception.ReimsException;
import com.reimbes.implementation.UtilsServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;

import static com.reimbes.constant.UrlConstants.*;
import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.verifyNoMoreInteractions;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Instant.class})
public class UtilsServiceTest {

    @InjectMocks
    private UtilsServiceImpl utils;

    private String test_directory = "\\test_directory";
    private String file_directory = test_directory + "\\testfile.jpg";
    private Path file_directory_path;
    private Path test_directory_path;

    @Before
    public void setup() {
        test_directory_path = utils.createDirectory(test_directory);
    }

    @Test
    public void returnExistanceStatusOfAFile() throws Exception {
        // create file first
        byte[] data = "hehe".getBytes();
        file_directory_path = utils.createFile(file_directory, data);

        assertTrue(utils.isFileExists(file_directory));
        assertFalse(utils.isFileExists("not-found.jpg"));
    }

    @Test
    public void returnAFileByItsRelativePath() throws Exception {
        // create file first
        String data = "123";
        byte[] dataInBytes = data.getBytes();
        file_directory_path = utils.createFile(file_directory, dataInBytes);

        assertEquals(data, new String(utils.getFile(file_directory)));
    }

    @Test
    public void removeAnImageByItsRelativePath() throws Exception {
        // create file first
        String data = "123";
        byte[] dataInBytes = data.getBytes();
        file_directory_path = utils.createFile(file_directory, dataInBytes);

        utils.removeImage(file_directory);
        assertFalse(utils.isFileExists(file_directory));
    }

    @Test
    public void doNothingWhenUserRemoveInvalidImagePath() {
        String randomFileDirectory = "hahaha.jpg";
        utils.removeImage(randomFileDirectory);
        verifyNoMoreInteractions(Files.class);
    }

    @Test
    public void returnStringWithImageExtension_whenUserGenerateFilename() {
        String extension = "png";
        assertTrue(utils.generateFilename(extension).matches(".*\\."+extension));
    }

    @Test
    public void returnCurrentTime() {
        Long now = Instant.now().toEpochMilli();
        assertTrue(utils.getCurrentTime() >= now);
    }

    @Test
    public void returnEmptyPrincipalName_whenUnauthicantedUserAccessGetPrincipalUsername() {
        SecurityContextHolder.getContext().setAuthentication(null);
        assertTrue(utils.getPrincipalUsername().isEmpty());
    }

    @Test
    public void returnPrincipalName_whenAuthicantedUserAccessGetPrincipalUsername() {
        ReimsUser user = ReimsUser.ReimsUserBuilder()
                .username("test")
                .role(ReimsUser.Role.USER)
                .build();

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().toString()));

        Authentication auth = new UsernamePasswordAuthenticationToken(user.getUsername(), null,authorities);

        SecurityContextHolder.getContext().setAuthentication(auth);
        assertEquals(user.getUsername(), utils.getPrincipalUsername());
    }

    @Test
    public void returnNull_uploadImageWithUnknownExtension() throws ReimsException {
        ReimsUser user = ReimsUser.ReimsUserBuilder()
                .id(123)
                .username("test")
                .role(ReimsUser.Role.USER)
                .build();
        String invalidImage = "data:attachments/.xx;bsae64";
        assertNull(utils.uploadImage(invalidImage, user.getId(), SUB_FOLDER_REPORT));
    }

    @Test
    public void uploadImageSuccessfullyWithPngExtension() throws IOException, ReimsException {
        ReimsUser user = ReimsUser.ReimsUserBuilder()
                .id(123)
                .username("test")
                .role(ReimsUser.Role.USER)
                .build();
        String imageValue = "png,iVBORw0";
        String imagePath = utils.uploadImage(imageValue, user.getId(), SUB_FOLDER_REPORT);
        assertNotNull(imagePath);

        file_directory_path = Paths.get(PROJECT_ROOT + imagePath);
    }

    @Test
    public void uploadImageSuccessfullyWithJpgExtension() throws IOException, ReimsException {
        ReimsUser user = ReimsUser.ReimsUserBuilder()
                .id(123)
                .username("test")
                .role(ReimsUser.Role.USER)
                .build();
        String imageValue = "jpg,iVBORw0";
        String imagePath = utils.uploadImage(imageValue, user.getId(), SUB_FOLDER_TRANSACTION);
        assertNotNull(imagePath);

        file_directory_path = Paths.get(PROJECT_ROOT + imagePath);
    }

    @Test
    public void uploadImageSuccessfullyWithJpegExtension() throws IOException, ReimsException {
        ReimsUser user = ReimsUser.ReimsUserBuilder()
                .id(123)
                .username("test")
                .role(ReimsUser.Role.USER)
                .build();
        String imageValue = "jpeg,iVBORw0";
        String imagePath = utils.uploadImage(imageValue, user.getId(), SUB_FOLDER_TRANSACTION);
        assertNotNull(imagePath);
        file_directory_path = Paths.get(PROJECT_ROOT + imagePath);
    }


    @After
    public void deleteOutputFile() throws Exception {
        if (file_directory_path != null) Files.deleteIfExists(file_directory_path);
        if (test_directory_path != null) Files.deleteIfExists(test_directory_path);
    }


}
