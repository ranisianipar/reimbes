package com.reimbes.implementation;

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
import java.util.HashMap;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserServiceImpl userService;

    @Override
    public Transaction create(Transaction transaction) {
        // dihubungin ke user yg bersangkutan
        return null;
    }

    @Override
    public void delete(long id) {
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
    public String upload(HttpServletRequest req, MultipartFile image) throws IOException {


        // ngecek org yg bersangkutan udah ada foldernya apa engga
        HashMap userDetails = JWTAuthorizationFilter.getCurrentUserDetails(req);

        String userId = userService.getUserByUsername((String) userDetails.get("username")).getId(); // GANTI
        System.out.println("USER ID:"+userId);

        String filename = StringUtils.cleanPath(UrlConstants.IMAGE_FOLDER_PATH+"\\"+ userId +"\\");

        if (!Files.exists(Paths.get(filename)))
            Files.createDirectory(Paths.get(filename));
        String localPath = filename + image.getName() + image.getOriginalFilename() + image.getContentType();

        InputStream inputStream = image.getInputStream();
        Files.copy(inputStream, Paths.get(filename));
        return localPath;
    }
}