package com.reimbes.implementation;

import java.time.Instant;
import java.util.*;

import com.reimbes.*;

import com.reimbes.exception.DataConstraintException;
import com.reimbes.exception.FormatTypeError;
import com.reimbes.exception.NotFoundException;
import com.reimbes.exception.ReimsException;
import com.reimbes.interfaces.TransactionService;
import com.reimbes.request.TransactionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.reimbes.Transaction.Category.PARKING;
import static com.reimbes.constant.UrlConstants.SUB_FOLDER_TRANSACTION;

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
    private ReceiptMapperServiceImpl receiptMapperService;

    @Autowired
    private UtilsServiceImpl utilsServiceImpl;

    @Override
    public Transaction createByImage(String imageValue) throws ReimsException {
        ReimsUser user = authService.getCurrentUser();
        String imagePath;

        Transaction transaction;
        try {
            imagePath = utilsServiceImpl.uploadImage(imageValue, user.getId(), SUB_FOLDER_TRANSACTION);

            log.info("Predicting image content... ");

            String receiptMapperResult = receiptMapperService.translateImage(imagePath, imageValue);
            log.info(String.format("Receipt Mapper Result %s"));

            transaction = new Transaction(); // default
            transaction.setCategory(PARKING);

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

        transaction.setCreatedAt(Instant.now().toEpochMilli());
        transaction.setAmount(transactionRequest.getAmount());
        try {
            transaction.setDate(transactionRequest.getDate());
        }   catch (Exception e) {
            transaction.setDate(Instant.now().toEpochMilli());
        }
        transaction.setImage(transactionRequest.getImage());
        transaction.setTitle(transactionRequest.getTitle());
        transaction.setReimsUser(userService.getUserByUsername(utilsServiceImpl.getPrincipalUsername()));

        return transactionRepository.save(transaction);
    }


    @Override
    public void delete(long id) throws ReimsException{
        ReimsUser user = userService.getUserByUsername(utilsServiceImpl.getPrincipalUsername());
        Transaction transaction = transactionRepository.findOne(id);
        if (transaction == null || transaction.getReimsUser() != user) throw new NotFoundException("Transaction with ID "+id);
        utilsServiceImpl.removeImage(transaction.getImage());
        transactionRepository.delete(transaction);
    }

    public void deleteByUser(ReimsUser user) {
        List<Transaction> transactions = transactionRepository.findByReimsUser(user);
        if (transactions == null)
            return;

        log.info("Removing the images");
        Iterator iterator = transactions.iterator();
        while (iterator.hasNext()) {
            utilsServiceImpl.removeImage(((Transaction) iterator.next()).getImage());
        }

        transactionRepository.delete(transactions);
    }

    @Override
    public Transaction get(long id) throws ReimsException{
        ReimsUser user = userService.getUserByUsername(utilsServiceImpl.getPrincipalUsername());
        Transaction transaction = transactionRepository.findOne(id);
        if (transaction == null || transaction.getReimsUser() != user)
            throw new NotFoundException("Transaction with ID "+id);

        return transactionRepository.findOne(id);
    }


    @Override
    public Page<Transaction> getAll(Pageable pageRequest, String startDate, String endDate, String title,
                                    Transaction.Category category) throws ReimsException{

        /****************************************HANDLING REQUEST PARAM************************************************/

        int index = pageRequest.getPageNumber() - 1;
        if (index < 0) index = 0;
        Pageable pageable = new PageRequest(index, pageRequest.getPageSize(), pageRequest.getSort());

        /****************************************SERVE REQUEST w/ JPA METHOD*******************************************/
        ReimsUser user = userService.getUserByUsername(utilsServiceImpl.getPrincipalUsername());
        if (title == null) title = "";

        if (startDate == null || endDate == null || startDate.isEmpty() || endDate.isEmpty()) {
            if (category != null)
                return transactionRepository.findByReimsUserAndTitleContainingIgnoreCaseAndCategory(user, title, category, pageable);
            return transactionRepository.findByReimsUserAndTitleContainingIgnoreCase(user, title, pageable);
        }

        Long start;
        Long end;
        try {
            start = Long.parseLong(startDate);
            end = Long.parseLong(endDate);
        }   catch (Exception e) {
            throw new FormatTypeError("value of start and end params");
        }

        if (category == null) {
            return transactionRepository.findByReimsUserAndTitleContainingIgnoreCaseAndDateBetween(
                    user,
                    title,
                    start,
                    end,
                    pageable
            );
        } else {
            return transactionRepository.findByReimsUserAndTitleContainingIgnoreCaseAndCategoryAndDateBetween(
                    user,
                    title,
                    category,
                    start,
                    end,
                    pageable
            );
        }
    }

    public List<Transaction> getByDateAndType(Long start, Long end, Transaction.Category category) throws ReimsException {
        ReimsUser user = authService.getCurrentUser();

        if (start == null || end== null) {
            return transactionRepository.findByReimsUserAndCategory(user, category);
        }

        return transactionRepository.findByReimsUserAndDateBetweenAndCategory(user, start, end, category);


    }

    public List<Transaction> getByUser(ReimsUser user) {
        return transactionRepository.findByReimsUser(user);
    }


    @Override
    public List<Transaction> getByUserAndDate(ReimsUser user, long start, long end) {
        if (start == new Long(0) || end == new Long(0)) return transactionRepository.findByReimsUser(user);
        return transactionRepository.findByReimsUserAndDateBetween(user, start, end);
    }

    private void validate(TransactionRequest transaction) throws ReimsException {
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

        if (transaction.getCategory() == null) {
            errorMessages.add("NULL_CATEGORY");
        } else if (transaction.getCategory().equals(PARKING)) {
            if (transaction.getParkingType() == null) errorMessages.add("NULL_PARKING_TYPE");
            if (transaction.getHours() == 0) errorMessages.add("ZERO_PARKING_HOURS");
        } else if (transaction.getCategory().equals(Transaction.Category.FUEL)) {
            if (transaction.getLiters() == 0) errorMessages.add("ZERO_FUEL_LITERS");
        }

        // validate image path
        if (transaction.getImage()== null || !utilsServiceImpl.isFileExists(transaction.getImage()))
            errorMessages.add("INVALID_IMAGE_PATH");


        // make sure the transaction use its own [NEW] image
        if (transactionRepository.existsByImage(transaction.getImage()))
            errorMessages.add("FORBIDDEN_DUPLICATE_IMAGE");


        if (!errorMessages.isEmpty())
            throw new DataConstraintException(errorMessages.toString());
    }
}
