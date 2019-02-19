package com.reimbes;


import com.reimbes.request.TransactionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = Constant.BASE_URL)
@RestController
@RequestMapping(Constant.TRANSACTION_PREFIX)
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @GetMapping
    public List<Transaction> getAllTransactions(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortedBy", defaultValue = "updatedDate") String sortBy,
            @RequestParam(value = "search") String sesrch
    ) {
        Pageable pageRequest = new PageRequest(page, size, new Sort(Sort.Direction.DESC, sortBy));
        return null;
    }

    // pake ID
    public Transaction getTransaction() {
        return null;
    }

    // pake ID
    @PostMapping
    public Transaction createTransaction(@RequestBody TransactionRequest transactionRequest) {
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setCategory(transactionRequest.getCategory());
        return transactionService.create(transaction);
    }

    // pake ID
    public Transaction updateTransaction() {
        return null;
    }

    @DeleteMapping
    public void deleteTransaction() {}

    @GetMapping("/_xls")
    public void getTheReport() {

    }


}
