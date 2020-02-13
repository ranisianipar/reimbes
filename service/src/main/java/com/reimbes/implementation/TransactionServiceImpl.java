package com.reimbes.implementation;

import com.reimbes.Fuel;
import com.reimbes.ReimsUser;
import com.reimbes.Transaction;
import com.reimbes.TransactionRepository;
import com.reimbes.exception.DataConstraintException;
import com.reimbes.exception.FormatTypeError;
import com.reimbes.exception.NotFoundException;
import com.reimbes.exception.ReimsException;
import com.reimbes.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.reimbes.constant.General.*;
import static com.reimbes.constant.UrlConstants.SUB_FOLDER_TRANSACTION;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private ReceiptMapperService receiptMapperService;

    @Autowired
    private UtilsService utilsService;

    @Override
    public Transaction createByImageAndCategory(Transaction transaction) throws ReimsException {
        ReimsUser user = authService.getCurrentUser();
        String imagePath;
        try {
            imagePath = utilsService.uploadImage(transaction.getImage(), user.getId(), SUB_FOLDER_TRANSACTION);
            log.info("Predicting attachments content... ", imagePath);
            if (transaction.getCategory() == null) {
                transaction.setCategory(Transaction.Category.FUEL);
            }
            transaction = receiptMapperService.map(transaction);
            log.info("Mapping the OCR result.");

        } catch (Exception e) {
            throw new FormatTypeError(e.getMessage());
        }
        transaction.setReimsUser(user);
        transaction.setImage(imagePath);
        return transaction;
    }

    @Override
    public Transaction update(Transaction transaction) throws ReimsException {
        ReimsUser user = authService.getCurrentUser();
        long currentTime = utilsService.getCurrentTime();
        validate(transaction);
        transaction.setCreatedAt(currentTime);
        transaction.setAmount(transaction.getAmount());
        transaction.setDate(transaction.getDate());
        transaction.setImage(transaction.getImage());
        transaction.setTitle(transaction.getTitle());
        transaction.setReimsUser(user);
        return transactionRepository.save(transaction);
    }


    @Override
    public boolean delete(long id) throws NotFoundException {
        ReimsUser user = authService.getCurrentUser();
        Transaction transaction = transactionRepository.findOne(id);
        if (transaction == null || transaction.getReimsUser() != user) {
            throw new NotFoundException("Transaction with ID " + id);
        }
        utilsService.removeImage(transaction.getImage());
        transactionRepository.delete(transaction);
        return true;
    }

    public boolean deleteTransactionImageByUser(ReimsUser user) {
        List<Transaction> transactions = transactionRepository.findByReimsUser(user);
        if (transactions == null) {
            log.info("Find no transaction.");
            return false;
        }
        log.info("Remove the images.");
        transactions.forEach(transaction -> utilsService.removeImage(transaction.getImage()));
        return true;
    }

    @Override
    public Transaction get(long id) throws ReimsException {
        ReimsUser user = authService.getCurrentUser();
        Transaction transaction = transactionRepository.findOne(id);
        if (transaction == null || transaction.getReimsUser() != user) {
            throw new NotFoundException(String.format("Transaction with ID " + id));
        }
        return transaction;
    }


    @Override
    public Page<Transaction> getAll(Pageable pageRequest, String title, String startDate, String endDate,
                                    Transaction.Category category) throws ReimsException {
        Pageable pageable = utilsService.getPageRequest(pageRequest);
        ReimsUser user = authService.getCurrentUser();
        if (title == null) title = "";

        if (startDate == null || endDate == null || startDate.isEmpty() || endDate.isEmpty()) {
            if (category != null) {
                return transactionRepository.findByReimsUserAndTitleContainingIgnoreCaseAndCategory(user, title, category, pageable);
            }
            return transactionRepository.findByReimsUserAndTitleContainingIgnoreCase(user, title, pageable);
        }

        long start;
        long end;
        try {
            start = Long.parseLong(startDate);
            end = Long.parseLong(endDate);
        } catch (Exception e) {
            throw new FormatTypeError("value of start and end params");
        }

        if (category == null) {
            return transactionRepository.findByReimsUserAndTitleContainingIgnoreCaseAndDateBetween(user, title, start, end, pageable);
        } else {
            return transactionRepository.findByReimsUserAndTitleContainingIgnoreCaseAndCategoryAndDateBetween(user, title, category, start, end, pageable);
        }
    }

    public List<Transaction> getByDateAndCategory(Long start, Long end, Transaction.Category category) throws ReimsException {
        ReimsUser user = authService.getCurrentUser();
        if (isQueryByDate(start, end)) {
            return transactionRepository.findByReimsUserAndCategory(user, category);
        }
        return transactionRepository.findByReimsUserAndDateBetweenAndCategory(user, start, end, category);

    }

    public List<Transaction> getByUser(ReimsUser user) {
        return transactionRepository.findByReimsUser(user);
    }

    @Override
    public List<Transaction> getByUserAndDate(ReimsUser user, long start, long end) {
        if (isQueryByDate(start, end)) {
            return transactionRepository.findByReimsUser(user);
        }
        return transactionRepository.findByReimsUserAndDateBetween(user, start, end);
    }

    private void validate(Transaction transaction) throws ReimsException {
        log.info("Validating transction value...");
        List<String> errorMessages = new ArrayList();
        if (transaction.getDate() == 0) {
            errorMessages.add(NULL_DATE);
        }
        if (transaction.getAmount() == 0) {
            errorMessages.add(ZERO_AMOUNT);
        }
        if (transaction.getCategory() == Transaction.Category.FUEL) {
            if (((Fuel) transaction).getLiters() == 0) {
                errorMessages.add(ZERO_FUEL_LITERS);
            }
        } else if (transaction.getCategory() == null) {
            errorMessages.add(NULL_CATEGORY);
        }
        if (transaction.getImage() == null || !utilsService.isFileExists(transaction.getImage())) {
            errorMessages.add(INVALID_IMAGE_PATH);
        }
        if (transactionRepository.existsByImage(transaction.getImage())) {
            errorMessages.add(FORBIDDEN_DUPLICATE_IMAGE);
        }
        if (!errorMessages.isEmpty())
            throw new DataConstraintException(errorMessages.toString());
    }

    private boolean isQueryByDate(long start, long end) {
        return (start == DEFAULT_LONG_VALUE) || (end == DEFAULT_LONG_VALUE);
    }
}
