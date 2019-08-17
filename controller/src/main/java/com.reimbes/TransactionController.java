package com.reimbes;


import com.reimbes.constant.UrlConstants;
import com.reimbes.exception.ReimsException;
import com.reimbes.implementation.TransactionServiceImpl;
import com.reimbes.request.TransactionRequest;
import com.reimbes.request.UploadImageByteRequest;
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
            transactions = transactionService.getAll(pageRequest, start, end, search, category);
            br.setData(getAllTransactionResponses(transactions.getContent()));
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
            br.setData(getTransactionResponse(transactionService.get(id)));
        }   catch (ReimsException r) {
            br.setErrorResponse(r);
        }

        return br;
    }

    @GetMapping(value = UrlConstants.ID_PARAM+UrlConstants.IMAGE_PARAM)
    public BaseResponse getImage(@PathVariable long id, @PathVariable String image) {
        BaseResponse<String> br = new BaseResponse();
        try {
            String result = transactionService.getImage(id,image);
            log.info(result);
            br.setData(result);

        }   catch (ReimsException r) {
            br.setErrorResponse(r);
        }

        return br;
    }

    @PutMapping
    public BaseResponse<TransactionResponse> updateTransaction(@RequestBody TransactionRequest newTransaction) {
        BaseResponse br = new BaseResponse();
        try {
            br.setData(getTransactionResponse(transactionService.update(newTransaction)));
        } catch (ReimsException r) {
            br.setErrorResponse(r);
        }
        return br;
    }

    @PostMapping
    public BaseResponse createTransactionByImage(@RequestBody UploadImageByteRequest request) {
        BaseResponse br = new BaseResponse();

        try {
            br.setData(getTransactionResponse(transactionService.createByImage(request.getImage())));
        }   catch (ReimsException r) {
            br.setErrorResponse(r);
        }

        return br;
    }

    @DeleteMapping(UrlConstants.ID_PARAM)
    public BaseResponse deleteById(@PathVariable Long id) {
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

    private List<? extends TransactionResponse> getAllTransactionResponses(List<Transaction> transactions) {
        log.info("Mapping object to web response...");
        List<TransactionResponse> transactionResponses = new ArrayList<>();
        Iterator<Transaction> iterator = transactions.iterator();
        Transaction transaction;
        while (iterator.hasNext()) {
            transaction = iterator.next();
            transactionResponses.add(getTransactionResponse(transaction));
        }
        return transactionResponses;
    }

    private TransactionResponse getTransactionResponse(Transaction transaction) {
        if (transaction == null) return null;
        TransactionResponse transactionResponse;

        if (transaction.getCategory().equals(Transaction.Category.PARKING))
            transactionResponse = getTransactionMapper(transaction)
                    .map(transaction, ParkingResponse.class);
        else
            transactionResponse = getTransactionMapper(transaction)
                    .map(transaction, FuelResponse.class);
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
