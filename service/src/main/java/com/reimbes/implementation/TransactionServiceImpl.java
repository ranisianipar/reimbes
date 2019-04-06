package com.reimbes.implementation;

import com.reimbes.ReimsUser;
import com.reimbes.Transaction;
import com.reimbes.TransactionRepository;
import com.reimbes.TransactionService;

import com.reimbes.authentication.JWTAuthorizationFilter;
import com.reimbes.constant.UrlConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserServiceImpl userService;

    @Override
    public Transaction create(HttpServletRequest request, Transaction transaction) throws Exception{
        // dihubungin ke user yg bersangkutan
        List<String> errorMessages = new ArrayList();


        // check the transaction value
        HashMap userDetails = JWTAuthorizationFilter.getCurrentUserDetails(request);
        String username = (String) userDetails.get("username");
        System.out.println("USER DETAILS:"+userDetails.toString());
        ReimsUser user;


        user = userService.getUserByUsername(username);
        // bisa diganti
        if (user == null) {
            throw new Exception("In-Memory user not allowed");
        }

        // validate the value and value type
        // date dicek harus ada isinya, dan sesuai ketentuan Date

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

        transaction.setUser(user);
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

    @Override
    public String upload(HttpServletRequest req, MultipartFile image) throws Exception {
        HashMap userDetails = JWTAuthorizationFilter.getCurrentUserDetails(req);
        String userId;
        try {
            userId = userService.getUserByUsername((String) userDetails.get("username")).getId(); // GANTI
        } catch (NullPointerException e) {
            throw new Exception("In memory user");
        }

        System.out.println("USER ID:"+userId);

        String filename = StringUtils.cleanPath(UrlConstants.IMAGE_FOLDER_PATH+ userId +"\\");

        if (!Files.exists(Paths.get(filename)))
            Files.createDirectory(Paths.get(filename));
        String localPath = filename + image.getName() + image.getOriginalFilename() + image.getContentType();

        InputStream inputStream = image.getInputStream();
        Files.copy(inputStream, Paths.get(filename));
        return localPath;
    }
}
