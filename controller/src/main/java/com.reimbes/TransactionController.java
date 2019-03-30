package com.reimbes;


import com.reimbes.constant.UrlConstants;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = UrlConstants.BASE_URL)
@RestController
@RequestMapping(UrlConstants.TRANSACTION_PREFIX)
public class TransactionController {

    @GetMapping
    public String getAllTransaction() {
        return "All transaction";
    }

    @GetMapping(UrlConstants.ID_PARAM)
    public String getTransaction(@PathVariable String id) {
        return "Get transaction by ID: "+id;
    }

    @PostMapping
    public String createTransaction(@RequestBody Transaction newTransaction) {
        return "new Transction has been created";
    }

    @PostMapping(UrlConstants.UPLOAD)
    public String uploadImage(@RequestParam("image") MultipartFile image) {
        return "image path";
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
