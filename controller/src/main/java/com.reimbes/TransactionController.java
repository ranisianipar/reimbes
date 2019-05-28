package com.reimbes;


import com.reimbes.constant.UrlConstants;
import com.reimbes.exception.ReimsException;
import com.reimbes.implementation.CVAzure;
import com.reimbes.implementation.TransactionServiceImpl;
import com.reimbes.request.BulkDeleteRequest;
import com.reimbes.response.BaseResponse;
import com.reimbes.response.TransactionResponse;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin(origins = UrlConstants.BASE_URL)
@RestController
@RequestMapping(UrlConstants.API_PREFIX+UrlConstants.TRANSACTION_PREFIX)
public class TransactionController {

    @Autowired
    private TransactionServiceImpl transactionService;

    @Autowired
    private CVAzure ocrService;

    @GetMapping
    public String getAllTransaction(HttpServletRequest req) {
        return "SOON";
    }

    @GetMapping(UrlConstants.ID_PARAM)
    public BaseResponse getTransaction(@PathVariable long id, HttpServletRequest req) {
        BaseResponse<Transaction> br = new BaseResponse<>();
        try {
            br.setData(transactionService.get(req, id));
        }   catch (ReimsException r) {
            br.errorResponse(r);
        }

        return br;
    }

    @PutMapping
    public BaseResponse<TransactionResponse> createTransaction(HttpServletRequest request, @RequestBody Transaction newTransaction) throws Exception{
        BaseResponse br = new BaseResponse();
        TransactionResponse tr = getMapper().map(transactionService.create(request, newTransaction), TransactionResponse.class);
        br.setData(tr);
        return br;
    }

    // handle kalo file yg diupload bukan webp
    @PostMapping
    public BaseResponse uploadImage(@RequestParam("image") MultipartFile imageValue, HttpServletRequest request) throws Exception {
        BaseResponse br = new BaseResponse();
        String ocrResult = transactionService.upload(request, imageValue);

        br.setErrors("ga ada! Ini cuma buat ngecek keluarannya output");
        br.setData(ocrResult);
        return br;
    }

    @DeleteMapping(UrlConstants.ID_PARAM)
    public String deleteById(@PathVariable String id, HttpServletRequest req) {
        return "delete by ID: "+id;
    }

    @DeleteMapping
    public String deleteMany(@RequestBody BulkDeleteRequest<Transaction> req) {
        return "delete many transaction";
    }

    private MapperFacade getMapper() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(Transaction.class, TransactionResponse.class)
                .byDefault().register();
        return mapperFactory.getMapperFacade();
    }
}
