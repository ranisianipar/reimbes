package com.reimbes.implementation;

import com.lowagie.text.pdf.codec.Base64;
import com.reimbes.*;

import com.reimbes.constant.UrlConstants;
import com.reimbes.exception.DataConstraintException;
import com.reimbes.exception.FormatTypeError;
import com.reimbes.exception.NotFoundException;
import com.reimbes.exception.ReimsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private TesseractService ocrService;

    public Transaction createByImage(String imageValue) throws ReimsException{
        // Decode base64 imageValue into bytes in webp format
        log.info("Image Value: "+imageValue);

        // sementara
        Transaction transaction = new Parking();

        try {
            //This will decode the String which is encoded by using Base64 class
            byte[] imageByte = Base64.decode(imageValue);

            upload(imageByte);
            transaction = ocrService.predictImageContent(imageByte);
            ReimsUser user = userService.getUserByUsername(authService.getCurrentUsername());
            transaction.setUser(user);

        } catch(Exception e) {
            throw new FormatTypeError(e.getMessage());
        }

        // decode imageBytes to jpeg/png/bmp

        // write image into server

        // mapping ocr value to Transaction value

        // return [category-table]Repository.save(them)
        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction create(Transaction transaction) throws ReimsException {
        ReimsUser user = userService.getUserByUsername(authService.getCurrentUsername());
        transaction.setUser(user);
        validate(transaction);
        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction update(Transaction transaction, long id) {
        return null;
    }

    @Override
    public void delete(long id) throws ReimsException{
        ReimsUser user = userService.getUserByUsername(authService.getCurrentUsername());
        Transaction transaction = transactionRepository.findOne(id);
        if (transaction == null || transaction.getUser() != user) throw new NotFoundException("Transaction with ID "+id);
        transactionRepository.delete(transaction);
    }

    public void deleteByUser(ReimsUser user) {
        List<Fuel> transaction = transactionRepository.findByUser(user);
        if (transaction == null)
            return;
        transactionRepository.delete(transaction);
    }

    @Override
    public void deleteAll(HttpServletRequest request) {
        HashMap userDetails = authService.getCurrentUserDetails(request);
        ReimsUser user = userService.getUserByUsername((String) userDetails.get("username"));
        transactionRepository.deleteByUser(user);
    }

    @Override
    public Transaction get(long id) throws ReimsException{
        ReimsUser user = userService.getUserByUsername(authService.getCurrentUsername());
        Transaction transaction = transactionRepository.findOne(id);
        if (transaction == null || transaction.getUser() != user) throw new NotFoundException("Transaction with ID "+id);

        return transaction;
    }

    @Override
    public List<Transaction> getAll(Pageable pageable) {
        return null;
    }

    public String upload(byte[] data) throws Exception {
        long userId;

        userId = userService.getUserByUsername(authService.getCurrentUsername()).getId();

        String foldername = userId +"\\";
        String path = StringUtils.cleanPath(UrlConstants.IMAGE_FOLDER_PATH+ foldername);

        if (!Files.exists(Paths.get(path)))
            Files.createDirectory(Paths.get(path));

        String filename = UUID.randomUUID()+".jpg";
        path = path + filename;

        InputStream inputStream = new ByteArrayInputStream(data);

        // upload photo
        try {
            Files.write(Paths.get(path), data, StandardOpenOption.CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return foldername+filename;
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

    // will be deleted
    public String encodeImage(MultipartFile imageValue) throws IOException {
        log.info("Bytes: "+imageValue.getBytes());
        String encoded = Base64.encodeBytes(imageValue.getBytes());


        return encoded;

    }

    @Override
    public void deletePhoto(String imagePath) {
        File file = new File(StringUtils.cleanPath(UrlConstants.IMAGE_FOLDER_PATH+imagePath));
        if(file.delete()){
            System.out.println("Image "+imagePath+" has been removed");
        } else System.out.println("File /Users/pankaj/file.txt doesn't exist");
    }

    private String uploadPhoto() {

        return null;
    }

    private void validate(Transaction transaction) throws ReimsException{
        // validate the data and data type
        // date dicek harus ada isinya, dan sesuai ketentuan Date

        List<String> errorMessages = new ArrayList();

        if (transaction.getDate() == null)
            errorMessages.add("null date");

        if (transaction.getAmount() == 0)
            errorMessages.add("zero amount");

        if (transaction.getCategory() == null)
            errorMessages.add("null category");

        // validate image path
        if (transaction.getImage()== null || !Files.exists(Paths.get(
                UrlConstants.IMAGE_FOLDER_PATH + transaction.getImage())))
            errorMessages.add("invalid image path");

        if (errorMessages.isEmpty())
            throw new DataConstraintException("Data constraint");
    }
}
