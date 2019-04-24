package com.reimbes.implementation;

import com.reimbes.ReimsUser;
import com.reimbes.Transaction;
import com.reimbes.TransactionRepository;
import com.reimbes.TransactionService;

import com.reimbes.constant.ResponseCode;
import com.reimbes.constant.UrlConstants;
import com.reimbes.exception.DataConstraintException;
import com.reimbes.exception.NotFoundException;
import com.reimbes.exception.ReimsException;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
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

    @Autowired
    private AuthServiceImpl authService;

    @Override
    public Transaction create(HttpServletRequest request, Transaction transaction) throws ReimsException{
        HashMap userDetails = authService.getCurrentUserDetails(request);
        ReimsUser user = userService.getUserByUsername((String) userDetails.get("username"));

        if (user == null) {
            throw new ReimsException("In-Memory user not allowed"
                    , HttpStatus.METHOD_NOT_ALLOWED
                    , ResponseCode.UNAUTHORIZED);
        }
        transaction.setUser(user);
        validate(transaction);
        return transactionRepository.save(transaction);
    }

    @Override
    public void delete(HttpServletRequest request, long id) throws ReimsException{
        HashMap userDetails = authService.getCurrentUserDetails(request);
        ReimsUser user = userService.getUserByUsername((String) userDetails.get("username"));
        Transaction transaction = transactionRepository.findOne(id);
        if (transaction.getUser() != user) throw new NotFoundException("Transaction with ID "+id);
        transactionRepository.delete(id);
    }

    @Override
    public void deleteAll(HttpServletRequest request) {
        HashMap userDetails = authService.getCurrentUserDetails(request);
        ReimsUser user = userService.getUserByUsername((String) userDetails.get("username"));
        transactionRepository.deleteByUser(user);
    }

    @Override
    public Transaction get(HttpServletRequest request, long id) throws ReimsException{
        HashMap userDetails = authService.getCurrentUserDetails(request);
        ReimsUser user = userService.getUserByUsername((String) userDetails.get("username"));
        Transaction transaction = transactionRepository.findOne(id);
        if (transaction.getUser() != user) throw new NotFoundException("Transaction with ID "+id);

        return transactionRepository.findOne(id);
    }

    @Override
    public String upload(HttpServletRequest request, String imageValue) throws Exception {
        HashMap userDetails = authService.getCurrentUserDetails(request);
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

    private void validate(Transaction transaction) throws ReimsException{
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
            throw new DataConstraintException("Data constraint");
    }
}
