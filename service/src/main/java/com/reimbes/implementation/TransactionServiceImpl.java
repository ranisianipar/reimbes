package com.reimbes.implementation;

import com.reimbes.ReimsUser;
import com.reimbes.Transaction;
import com.reimbes.TransactionRepository;
import com.reimbes.TransactionService;

import com.reimbes.authentication.JWTAuthorizationFilter;
import com.reimbes.constant.UrlConstants;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CVAzure ocrService;

    @Autowired
    private UserServiceImpl userService;

    @Override
    public Transaction create(HttpServletRequest request, Transaction transaction) throws Exception{
        HashMap userDetails = JWTAuthorizationFilter.getCurrentUserDetails(request);
        String username = (String) userDetails.get("username");
        ReimsUser user;

        user = userService.getUserByUsername(username);
        // ADAPTABLE
        if (user == null) {
            throw new Exception("In-Memory user not allowed");
        }
        transaction.setUser(user);
        validate(transaction);
        return transactionRepository.save(transaction);
    }

    @Override
    public void delete(long id) {
        // tahap nge-query transactionnya --> cari yang user nya dia
        transactionRepository.delete(id);
    }

    @Override
    public void deleteAll() {
        transactionRepository.deleteAll();
    }

    @Override
    public Transaction get(long id) {
        return transactionRepository.findOne(id);
    }

    // bakal return object hasil OCR --> TransactionResponse
    @Override
    public String upload(HttpServletRequest req, String imageValue) throws Exception {
        HashMap userDetails = JWTAuthorizationFilter.getCurrentUserDetails(req);
        String userId;

        try {
            userId = userService.getUserByUsername((String) userDetails.get("username")).getId();
        } catch (NullPointerException e) {
            throw new Exception("In memory user");
        }
        String folderName = userId +"\\";
        String path = StringUtils.cleanPath(UrlConstants.IMAGE_FOLDER_PATH+ folderName);

        if (!Files.exists(Paths.get(path)))
            Files.createDirectory(Paths.get(path));

        String filename = UUID.randomUUID()+".jpg";
        path = path + filename;

        //This will decode the String which is encoded by using Base64 class
        byte[] data = Base64.decodeBase64(imageValue);

        InputStream inputStream = new ByteArrayInputStream(data);

        try {
            Files.copy(inputStream, Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return folderName+ filename;
    }

    @Override
    public byte[] getPhoto(String imagePath) {
        imagePath = StringUtils.cleanPath(UrlConstants.IMAGE_FOLDER_PATH + imagePath);
        try {
            return Files.readAllBytes(Paths.get(imagePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void validate(Transaction transaction) throws Exception{
        // validate the value and value type
        // date dicek harus ada isinya, dan sesuai ketentuan Date

        List<String> errorMessages = new ArrayList();

        if (transaction.getDate() == null)
            errorMessages.add("null date");

        if (transaction.getAmount() == 0)
            errorMessages.add("zero amount");

        if (transaction.getCategory() == null)
            errorMessages.add("null category");

        // validate image path
        // kalo transaction.getImagePath() == null raise error ga? [belom dihandle]
        if (!Files.exists(Paths.get(UrlConstants.IMAGE_FOLDER_PATH+transaction.getImagePath())))
            errorMessages.add("invalid image path");

        if (errorMessages.isEmpty())
            throw new Exception("Data constraint");
    }
}
