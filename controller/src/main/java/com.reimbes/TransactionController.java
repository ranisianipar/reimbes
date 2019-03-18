package com.reimbes;


import com.reimbes.constant.UrlConstants;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = UrlConstants.BASE_URL)
@RestController
@RequestMapping(UrlConstants.TRANSACTION_PREFIX)
public class TransactionController {

    @GetMapping
    public String getAllTransaction() {
        return "All transaction";
    }

    @GetMapping("{id}")
    public String getTransaction(@PathVariable String id) {
        return "Get transaction by ID: "+id;
    }

    @PostMapping
    public String createTransaction() {
        return "new Transction has been created";
    }

    @GetMapping(UrlConstants.MONTHLY_REPORT)
    public String getMonthlyReport() {
        return "GET monthly report";
    }

    @DeleteMapping("{id}")
    public String deleteById(@PathVariable String id) {
        return "delete by ID: "+id;
    }

    @DeleteMapping
    public String deleteMany() {
        return "delete many transaction";
    }

}
