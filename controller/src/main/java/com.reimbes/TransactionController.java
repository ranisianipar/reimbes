package com.reimbes;


import com.reimbes.constant.UrlConstants;
import com.reimbes.exception.ReimsException;
import com.reimbes.implementation.TransactionServiceImpl;
import com.reimbes.request.BulkDeleteRequest;
import com.reimbes.request.UploadImageByteRequest;
import com.reimbes.response.*;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@CrossOrigin(origins = UrlConstants.CROSS_ORIGIN_URL)
@RestController
@RequestMapping(UrlConstants.API_PREFIX+UrlConstants.TRANSACTION_PREFIX)
public class TransactionController {

    private static Logger log = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private TransactionServiceImpl transactionService;

    @GetMapping
    public String getAllTransaction(
            @RequestParam(value = "pageNumber", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "month", defaultValue = "0") int month,
            @RequestParam(value = "year", defaultValue = "0") int year,
            @RequestParam (value = "search", required = false) String search
    ) {

        Pageable pageRequest = new PageRequest(page, size, new Sort(Sort.Direction.ASC, sortBy));
        Paging paging = getPagingMapper().map(pageRequest, Paging.class);
        BaseResponse br = new BaseResponse();


        // data nya itu semua transaction
        br.setData("");
        br.setPaging(paging);

        return "SOON";
    }

    @GetMapping(UrlConstants.ID_PARAM)
    public BaseResponse getTransaction(@PathVariable long id) {
        BaseResponse<TransactionResponse> br = new BaseResponse<>();

        try {
            br.setData(getTransactionResponse(transactionService.get(id)));
        }   catch (ReimsException r) {
            br.setErrorResponse(r);
        }

        return br;
    }

    @PutMapping
    public BaseResponse<TransactionResponse> createTransaction(@RequestBody Transaction newTransaction) throws Exception{
        BaseResponse br = new BaseResponse();
        br.setData(getTransactionResponse(transactionService.create(newTransaction)));
        return br;
    }

    @PostMapping
    public BaseResponse createTransaction(@RequestBody UploadImageByteRequest request) {
        BaseResponse br = new BaseResponse();

        try {
            br.setData(getTransactionResponse(transactionService.createByImage(request.getImage())));
        }   catch (ReimsException r) {
            br.setErrorResponse(r);
        }

        return br;
    }

    @PutMapping("/_encode-image")
    public String encodeImage(@RequestParam("image") MultipartFile imageValue) throws Exception {
        return transactionService.encodeImage(imageValue);
    }

    @DeleteMapping(UrlConstants.ID_PARAM)
    public String deleteById(@PathVariable String id, HttpServletRequest req) {
        return "delete by ID: "+id;
    }

    @DeleteMapping
    public String deleteMany(@RequestBody BulkDeleteRequest<Transaction> req) {
        return "delete many transaction";
    }

    private MapperFacade getPagingMapper() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(Pageable.class, Paging.class)
                .byDefault().register();
        return mapperFactory.getMapperFacade();
    }

    private List<? extends TransactionResponse> getAllTransactionResponses(List<? extends Transaction> transactions) {
        log.info("Mapping object to web response...");
        List<TransactionResponse> transactionResponses = new ArrayList<>();
        Iterator iterator = transactions.iterator();
        Object transaction;
        while (iterator.hasNext()) {
            transaction = iterator.next();
            if (((Transaction) transaction).getCategory().equals(Transaction.Category.PARKING))
                transactionResponses.add(getTransactionMapper((Transaction) transaction)
                        .map(transactions, ParkingResponse.class));
            else
                transactionResponses.add(getTransactionMapper((Transaction) transaction)
                        .map(transactions, FuelResponse.class));
        }
        return transactionResponses;
    }

    private TransactionResponse getTransactionResponse(Transaction transaction) {
        TransactionResponse transactionResponse;
        if (transaction.getCategory().equals(Transaction.Category.PARKING))
            transactionResponse = getTransactionMapper((Transaction) transaction)
                    .map(transaction, ParkingResponse.class);
        else
            transactionResponse = getTransactionMapper((Transaction) transaction)
                    .map(transaction, FuelResponse.class);
        return transactionResponse;
    }

    private MapperFacade getTransactionMapper(Transaction transaction) {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        if (transaction.getCategory().equals(Transaction.Category.PARKING)) {
            mapperFactory.classMap(Parking.class, ParkingResponse.class)
                    .byDefault().register();
            mapperFactory.getMapperFacade().map(transaction, ParkingResponse.class);
        } else {
            mapperFactory.classMap(Fuel.class, FuelResponse.class)
                    .byDefault().register();
        }

        return mapperFactory.getMapperFacade();
    }

}
