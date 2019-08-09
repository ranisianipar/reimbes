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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static com.reimbes.constant.General.DATE_FORMAT;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    @Autowired
    private ParkingServiceImpl parkingService;

    @Autowired
    private FuelServiceImpl fuelService;

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

            log.info("Decoding image byte succeed.");
            log.info("Uploading the image...");

            System.out.println("[TEST] BEFORE UPLOAD IMAGE: "+imageByte.toString()+" EXT: "+extension);
            imagePath = uploadImage(imageByte, extension);

            log.info("Predicting image content... ");

            transaction = ocrService.predictImageContent(imageByte);

        } catch (Exception e) {
            throw new FormatTypeError(e.getMessage());
        }
            log.info("Mapping the OCR result.");
            transaction.setReimsUser(user);
            transaction.setImage(imagePath);
        return transaction;
    }

    @Override
    public Transaction update(TransactionRequest transactionRequest) throws ReimsException {

        // make sure update only happen once!
        validate(transactionRequest);

        Transaction transaction;

        if (transactionRequest.getCategory().equals(Transaction.Category.FUEL)) {
            transaction = fuelService.create(transactionRequest);
        } else {
            transaction = parkingService.create(transactionRequest);
        }

        transaction.setCreatedAt(Instant.now().getEpochSecond());
        transaction.setAmount(transactionRequest.getAmount());
        try {
            transaction.setDate(transactionRequest.getDate());
        }   catch (Exception e) {
            transaction.setDate(Instant.now().getEpochSecond()*1000);
        }
        transaction.setImage(transactionRequest.getImage());
        transaction.setTitle(transactionRequest.getTitle());
        transaction.setReimsUser(userService.getUserByUsername(authService.getCurrentUsername()));

        return transactionRepository.save(transaction);
    }


    @Override
    public void delete(long id) throws ReimsException{
        ReimsUser user = userService.getUserByUsername(authService.getCurrentUsername());
        Transaction transaction = transactionRepository.findOne(id);
        if (transaction == null || transaction.getReimsUser() != user) throw new NotFoundException("Transaction with ID "+id);
        transactionRepository.delete(transaction);
    }

    @Transactional
    @Override
    public void deleteMany(List<Long> ids) throws ReimsException{
        List<Transaction> transactions = transactionRepository.findByIdIn(ids);

        if (transactions == null)
            throw new NotFoundException("Any objects");

        log.info("Removing the images");
        Iterator iterator = transactions.iterator();
        while (iterator.hasNext()) {
            removeImage(((Transaction) iterator.next()).getImage());
        }

        transactionRepository.delete(transactions);
    }

    @Transactional
    public void deleteByUser(ReimsUser user) {
        List<Transaction> transactions = transactionRepository.findByReimsUser(user);
        if (transactions == null)
            return;

        log.info("Removing the images");
        Iterator iterator = transactions.iterator();
        while (iterator.hasNext()) {
            removeImage(((Transaction) iterator.next()).getImage());
        }

        transactionRepository.delete(transactions);
    }

    @Override
    public Transaction get(long id) throws ReimsException{
        ReimsUser user = userService.getUserByUsername(authService.getCurrentUsername());
        Transaction transaction = transactionRepository.findOne(id);
        if (transaction == null || transaction.getReimsUser() != user)
            throw new NotFoundException("Transaction with ID "+id);

        return transactionRepository.findOne(id);
    }


    @Override
    public Page<Transaction> getAll(Pageable pageRequest, long start, long end, String title,
                                    Transaction.Category category) throws ReimsException{
        log.info("[Filter Request] START: " + start+" END: " + end+ " TITLE: " + title + " CATEGORY: " + category);


        /****************************************HANDLING REQUEST PARAM************************************************/

        int index = pageRequest.getPageNumber() - 1;
        if (index < 0) index = 0;
        Pageable pageable = new PageRequest(index, pageRequest.getPageSize(), pageRequest.getSort());

        /****************************************SERVE REQUEST w/ JPA METHOD*******************************************/
        ReimsUser user = userService.getUserByUsername(authService.getCurrentUsername());
        if (title == null) title = "";

        if (start == 0 | end == 0) {
            if (category != null)
                return transactionRepository.findByReimsUserAndTitleContainingAndCategory(user, title,category, pageable);
            return transactionRepository.findByReimsUserAndTitleContaining(user, title, pageable);
        } else if (category == null) {
            log.info("[DATE] start: "+start+" end: "+end);
            return transactionRepository.findByReimsUserAndTitleContainingAndDateBetween(
                    user,
                    title,
                    start,
                    end,
                    pageable
            );
        }   else {
            return transactionRepository.findByReimsUserAndTitleContainingAndCategoryAndDateBetween(
                    user,
                    title,
                    category,
                    start,
                    end,
                    pageable
            );
        }
    }

    @Override
    public String uploadImage(byte[] data, String extension) throws Exception {
        long userId;

        userId = userService.getUserByUsername(authService.getCurrentUsername()).getId();

        String foldername = userId +"/";
        String path = StringUtils.cleanPath(UrlConstants.IMAGE_FOLDER_PATH+ foldername);

        if (!Files.exists(Paths.get(path)))
            Files.createDirectory(Paths.get(path));

        String filename = UUID.randomUUID()+"."+extension;
        path = path + filename;

        // uploadImage photo
        try {
            Files.write(Paths.get(path), data, StandardOpenOption.CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return foldername+filename;
    }

    private void removeImage(String imagePath) {
        imagePath = StringUtils.cleanPath(UrlConstants.IMAGE_FOLDER_PATH + imagePath);
        try {
            Files.delete(Paths.get(imagePath));
        }   catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getImage(long id, String imageName) throws ReimsException {
        if (id != userService.getUserByUsername(authService.getCurrentUsername()).getId())
            throw new NotFoundException("IMAGE");

        String imagePath = id+"/"+imageName;

        Transaction tr = transactionRepository.findByImageContaining(imagePath);
        if (tr != null) imagePath = StringUtils.cleanPath(tr.getImage());
        byte[] imageByte;
        String result;
        try {
            imageByte = Files.readAllBytes(Paths.get(UrlConstants.IMAGE_FOLDER_PATH + imagePath));
            log.info("Translate to byte done.");
            result = Base64.getEncoder().encodeToString(imageByte);
            log.info("Get image in Base64 format.");
            return result;
        } catch (Exception e) {
            throw new NotFoundException("IMAGE");
        }
    }

    public byte[] getImageInByte(long id, String imageName) throws ReimsException {
        if (id != userService.getUserByUsername(authService.getCurrentUsername()).getId())
            throw new NotFoundException("IMAGE");

        String imagePath = id+"/"+imageName;
        imagePath = StringUtils.cleanPath(UrlConstants.IMAGE_FOLDER_PATH + imagePath);
        log.info("ID: "+id+" NAME: "+imageName);
        try {
            return Files.readAllBytes(Paths.get(imagePath));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    @Override
    public void deleteImage(String imagePath) {
        File file = new File(StringUtils.cleanPath(UrlConstants.IMAGE_FOLDER_PATH + imagePath));
        if (file.delete())
            log.info("Image removed successfully.");
        log.warn("File " + imagePath + " doesn't exist.");
    }


    public List<Transaction> getByUser(ReimsUser user) {
        return transactionRepository.findByReimsUser(user);
    }


    @Override
    public List<Transaction> getByUserAndDate(ReimsUser user, Long start, Long end) {
        if (start == null) start = Instant.now().getEpochSecond()*1000;
        if (end == null) end = Instant.now().getEpochSecond()*1000;

        return transactionRepository.findByReimsUserAndDateBetween(user, start, end);
    }

    private void validate(TransactionRequest transaction) throws ReimsException{
        // validate the data and data type
        // date dicek harus ada isinya, dan sesuai ketentuan Date

        log.info("Validating transction value...");

        List<String> errorMessages = new ArrayList();

        if (!(transaction.getCategory() instanceof Transaction.Category))
            errorMessages.add("UNKNOWN_CATEGORY");

        if (transaction.getDate() == 0)
            errorMessages.add("NULL_DATE");

        if (transaction.getAmount() == 0)
            errorMessages.add("ZERO_AMOUNT");

        if (transaction.getCategory() == null)
            errorMessages.add("NULL_CATEGORY");

        // validate image path
        if (transaction.getImage()== null || !Files.exists(Paths.get(
                UrlConstants.IMAGE_FOLDER_PATH + transaction.getImage())))
            errorMessages.add("INVALID_IMAGE_PATH");


        // make sure the transaction use its own [NEW] image
        if (transactionRepository.existsByImage(transaction.getImage()))
            errorMessages.add("FORBIDDEN_DUPLICATE_IMAGE");

        if (transaction.getCategory().equals(Transaction.Category.PARKING)) {
            if (transaction.getParkingType() == null) errorMessages.add("NULL_PARKING_TYPE");
        }

        if (transaction.getCategory().equals(Transaction.Category.FUEL)) {
            if (transaction.getHours() == 0) errorMessages.add("ZERO_FUEL_HOURS");
        }

        if (!errorMessages.isEmpty())
            throw new DataConstraintException(errorMessages.toString());
    }
}
