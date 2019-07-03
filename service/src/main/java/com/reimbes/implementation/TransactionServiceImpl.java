package com.reimbes.implementation;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

import com.reimbes.*;

import com.reimbes.constant.UrlConstants;
import com.reimbes.exception.DataConstraintException;
import com.reimbes.exception.FormatTypeError;
import com.reimbes.exception.NotFoundException;
import com.reimbes.exception.ReimsException;
import com.reimbes.request.TransactionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

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

    @Override
    public Transaction createByImage(String imageValue) throws ReimsException {

        ReimsUser user = userService.getUserByUsername(authService.getCurrentUsername());

        String[] extractedByte = imageValue.split(",");
        String extension = extractedByte[0];
        String imagePath;

        // get the extension
        if (extension.contains("jpg")) extension = "jpg";
        else if (extension.contains("png")) extension = "png";
        else if (extension.contains("jpeg")) extension = "jpeg";

        Transaction transaction;
        try {
            byte[] imageByte = Base64.getDecoder().decode((extractedByte[1]
                    .getBytes(StandardCharsets.UTF_8)));

            log.info("Uploading the image...");
            imagePath = upload(imageByte, extension);

            log.info("Predicting image content... ");
            transaction = ocrService.predictImageContent(imageByte);
        } catch (Exception e) {
            throw new FormatTypeError(e.getMessage());
        }

            log.info("Mapping the OCR result.");
            transaction.setUser(user);
            transaction.setImage(imagePath);
            transaction.setCreatedAt(Instant.now().getEpochSecond());

        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction update(TransactionRequest transactionRequest) throws ReimsException {
        Transaction oldTransaction = transactionRepository.findOne(transactionRequest.getId());

        ReimsUser user;

        if (oldTransaction == null || !(user = oldTransaction.getUser())
                .getUsername().equals(authService.getCurrentUsername())) {
            throw new NotFoundException("Transaction");
        }

        validate(transactionRequest);

        transactionRequest.setImage(oldTransaction.getImage());

        if (transactionRequest.getCategory().equals(Transaction.Category.PARKING)) {
            try {
                ((Parking) oldTransaction).setHours(transactionRequest.getHours());
            } catch (ClassCastException c) {
                delete(oldTransaction.getId());
                oldTransaction = new Parking();
                ((Parking) oldTransaction).setHours(transactionRequest.getHours());
            }

        } else {
            try {
                ((Fuel) oldTransaction).setLiters(transactionRequest.getLiters());
            }   catch (ClassCastException c) {
                delete(transactionRequest.getId());
                oldTransaction = new Fuel();
                ((Fuel) oldTransaction).setLiters(transactionRequest.getLiters());
            }
        }

        // set old transaction value
        log.info("USERRRR:  "+user);
        oldTransaction.setUser(user);
        log.info("OLD T USERRRR:    "+oldTransaction.getUser());
//        oldTransaction.setAmount(15000);
//        oldTransaction.setCategory(transactionRequest.getCategory());
//        oldTransaction.setDate(transactionRequest.getDate());
//        oldTransaction.setAmount(transactionRequest.getAmount());
//        oldTransaction.setTitle(transactionRequest.getTitle());
        oldTransaction.setCreatedAt(Instant.now().getEpochSecond());

        return create(transactionRequest);
    }

    private Transaction create(TransactionRequest transaction) {
        Transaction newTransaction;
        if (transaction.getCategory().equals(Transaction.Category.PARKING))
            newTransaction = new Parking();
        else
            newTransaction = new Fuel();

        newTransaction.setAmount(transaction.getAmount());

        newTransaction.setCategory(transaction.getCategory());
        newTransaction.setDate(transaction.getDate());
        newTransaction.setAmount(transaction.getAmount());
        newTransaction.setTitle(transaction.getTitle());

        newTransaction.setCreatedAt(Instant.now().getEpochSecond());
        newTransaction.setUser(userService.getUserByUsername(authService.getCurrentUsername()));
        newTransaction.setId(transaction.getId());
        return transactionRepository.save(newTransaction);
    }

    @Override
    public void delete(long id) throws ReimsException{
        ReimsUser user = userService.getUserByUsername(authService.getCurrentUsername());
        Transaction transaction = transactionRepository.findOne(id);
        if (transaction == null || transaction.getUser() != user) throw new NotFoundException("Transaction with ID "+id);
        transactionRepository.delete(transaction);
    }

    public void deleteMany(List<Long> ids) {
        List<Transaction> transactions = transactionRepository.findByIdIn(ids);
        transactionRepository.delete(transactions);
    }


    public void deleteByUser(ReimsUser user) {
        List<Transaction> transaction = transactionRepository.findByUser(user);
        if (transaction == null)
            return;
        // remove the image...

        transactionRepository.delete(transaction);
    }

    @Override
    public Transaction get(long id) throws ReimsException{
        ReimsUser user = userService.getUserByUsername(authService.getCurrentUsername());
        Transaction transaction = transactionRepository.findOne(id);
        if (transaction == null || transaction.getUser() != user) throw new NotFoundException("Transaction with ID "+id);

        return transaction;
    }

    @Override
    public List<Transaction> getAll(Pageable pageable, Date startDate, Date endDate, String searchTitle) {
        ReimsUser user = userService.getUserByUsername(authService.getCurrentUsername());

        return transactionRepository.findByUserAndDateBetweenAndTitleContaining(user, startDate, endDate, searchTitle, pageable);

    }

    public String upload(byte[] data, String extension) throws Exception {
        long userId;

        userId = userService.getUserByUsername(authService.getCurrentUsername()).getId();

        String foldername = userId +"/";
        String path = StringUtils.cleanPath(UrlConstants.IMAGE_FOLDER_PATH+ foldername);

        if (!Files.exists(Paths.get(path)))
            Files.createDirectory(Paths.get(path));

        String filename = UUID.randomUUID()+"."+extension;
        path = path + filename;

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

    @Override
    public void deletePhoto(String imagePath) {
        File file = new File(StringUtils.cleanPath(UrlConstants.IMAGE_FOLDER_PATH+imagePath));
        if(file.delete()){
            System.out.println("Image "+imagePath+" has been removed");
        } else System.out.println("File "+imagePath+" doesn't exist");
    }

    private void validate(TransactionRequest transaction) throws ReimsException{
        // validate the data and data type
        // date dicek harus ada isinya, dan sesuai ketentuan Date

        List<String> errorMessages = new ArrayList();

        if (!(transaction.getCategory() instanceof Transaction.Category))
            errorMessages.add("unknown category");

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
