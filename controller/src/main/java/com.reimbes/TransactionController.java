package com.reimbes;


import com.reimbes.constant.UrlConstants;
import com.reimbes.implementation.TransactionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin(origins = UrlConstants.BASE_URL)
@RestController
@RequestMapping(UrlConstants.TRANSACTION_PREFIX)
public class TransactionController {

    @Autowired
    private TransactionServiceImpl transactionService;

    @GetMapping
    public String getAllTransaction() {
        return "All transaction";
    }

    @GetMapping(UrlConstants.ID_PARAM)
    public String getTransaction(@PathVariable String id) {
        return "Get transaction by ID: "+id;
    }

    @PostMapping
    public Transaction createTransaction(HttpServletRequest request, @RequestBody Transaction newTransaction) throws Exception{
        return transactionService.create(request, newTransaction);
    }

    @PostMapping(UrlConstants.UPLOAD)
    public String uploadImage(HttpServletRequest request, @RequestParam("image") MultipartFile image) throws Exception {

        return transactionService.upload(request, image);
    }


    @DeleteMapping(UrlConstants.ID_PARAM)
    public String deleteById(@PathVariable String id) {
        return "delete by ID: "+id;
    }

    @DeleteMapping
    public String deleteMany() {
        return "delete many transaction";
    }

}
