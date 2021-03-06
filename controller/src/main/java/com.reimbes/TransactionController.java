package com.reimbes;


import com.reimbes.constant.UrlConstants;
import com.reimbes.exception.ReimsException;
import com.reimbes.implementation.TransactionServiceImpl;
import com.reimbes.request.TransactionRequest;
import com.reimbes.response.*;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;


import java.util.*;
import java.util.List;

import static com.reimbes.constant.Mapper.getTransactionRequestMapper;

@CrossOrigin(origins = UrlConstants.CROSS_ORIGIN_URL)
@RestController
@RequestMapping(UrlConstants.API_PREFIX + UrlConstants.TRANSACTION_PREFIX)
public class TransactionController {

    private static Logger log = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private TransactionServiceImpl transactionService;

    @GetMapping
    public BaseResponse getAllTransaction(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "sortBy", defaultValue = "date") String sortBy,
            @RequestParam(value = "start", required = false) String start,
            @RequestParam(value = "end", required = false) String end,
            @RequestParam (value = "search", required = false) String search,
            @RequestParam (value = "category", required = false) Transaction.Category category
    ) {

        Pageable pageRequest = new PageRequest(page, size, new Sort(Sort.Direction.ASC, sortBy));
        Paging paging = getPagingMapper().map(pageRequest, Paging.class);
        BaseResponse br = new BaseResponse();

        Page transactions;

        try {
            transactions = transactionService.getAll(pageRequest, search, start, end, category);
            br.setData(mapAllTransactionResponses(transactions.getContent()));
            paging.setTotalPages(transactions.getTotalPages());
            paging.setTotalRecords(transactions.getContent().size());

            br.setPaging(paging);
        }   catch (ReimsException r) {
            br.setErrorResponse(r);
        }


        return br;
    }

    @GetMapping(UrlConstants.ID_PARAM)
    public BaseResponse getTransaction(@PathVariable long id) {
        BaseResponse<TransactionResponse> br = new BaseResponse<>();

        try {
            br.setData(mapTransactionResponse(transactionService.get(id)));
        }   catch (ReimsException r) {
            br.setErrorResponse(r);
        }

        return br;
    }

    @PutMapping
    public BaseResponse<TransactionResponse> updateTransaction(@RequestBody TransactionRequest request) {
        BaseResponse br = new BaseResponse();
        try {
            br.setData(mapTransactionResponse(
                    transactionService.update(mapTransactionRequest(request)))
            );
        } catch (ReimsException r) {
            br.setErrorResponse(r);
        }
        return br;
    }

    @PostMapping
    public BaseResponse createTransactionByImageAndCategory(@RequestBody TransactionRequest request) {
        BaseResponse br = new BaseResponse();

        try {
            br.setData(mapTransactionResponse(transactionService.createByImageAndCategory(mapTransactionRequest(request))));
        }   catch (ReimsException r) {
            br.setErrorResponse(r);
        }

        return br;
    }

    @DeleteMapping(UrlConstants.ID_PARAM)
    public BaseResponse deleteById(@PathVariable Long id) {
        log.info("Delete transaction with ID", id);
        BaseResponse br = new BaseResponse();
        try {
            transactionService.delete(id);
        }   catch (ReimsException r) {
            br.setErrorResponse(r);
        }

        return new BaseResponse();
    }

    private MapperFacade getPagingMapper() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(Pageable.class, Paging.class)
                .byDefault().register();
        return mapperFactory.getMapperFacade();
    }

    private List<? extends TransactionResponse> mapAllTransactionResponses(List<Transaction> transactions) {
        log.info("Mapping object to web response...");
        List<TransactionResponse> transactionResponses = new ArrayList<>();
        Iterator<Transaction> iterator = transactions.iterator();
        Transaction transaction;
        while (iterator.hasNext()) {
            transaction = iterator.next();
            transactionResponses.add(mapTransactionResponse(transaction));
        }
        return transactionResponses;
    }

    private TransactionResponse mapTransactionResponse(Transaction transaction) {
        if (transaction == null) return null;
        TransactionResponse transactionResponse;

        if (transaction.getCategory().equals(Transaction.Category.PARKING))
            transactionResponse = getTransactionMapper(transaction)
                    .map(transaction, ParkingResponse.class);
        else
            transactionResponse = getTransactionMapper(transaction)
                    .map(transaction, FuelResponse.class);

        if (transaction.getImage() != null) {
            String[] attachments = {transaction.getImage()};
            transactionResponse.setAttachments(Arrays.asList(attachments));

        }
        return transactionResponse;
    }

    private Transaction mapTransactionRequest(TransactionRequest transaction) {
        if (transaction == null) return null;
        Transaction transactionResponse;

        if (transaction.getCategory().equals(Transaction.Category.PARKING))
            transactionResponse = getTransactionRequestMapper(transaction)
                    .map(transaction, Parking.class);
        else
            transactionResponse = getTransactionRequestMapper(transaction)
                    .map(transaction, Fuel.class);

        // get attachments of transaction
        if (transaction.getAttachments() != null) transactionResponse.setImage(transaction.getAttachments().get(0));

        return transactionResponse;
    }

    private MapperFacade getTransactionMapper(Transaction transaction) {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        if (transaction.getCategory().equals(Transaction.Category.PARKING)) {
            mapperFactory.classMap(Parking.class, ParkingResponse.class)
                    .field("reimsUser.id", "userId")
                    .byDefault().register();
            mapperFactory.getMapperFacade().map(transaction, ParkingResponse.class);
        } else {
            mapperFactory.classMap(Fuel.class, FuelResponse.class)
                    .field("reimsUser.id", "userId")
                    .byDefault().register();
            mapperFactory.getMapperFacade().map(transaction, FuelResponse.class);
        }

        return mapperFactory.getMapperFacade();
    }

}
