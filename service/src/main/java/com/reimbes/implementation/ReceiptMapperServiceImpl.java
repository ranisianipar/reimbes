package com.reimbes.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reimbes.Transaction;
import com.reimbes.interfaces.ReceiptMapperService;
import com.reimbes.request.ReceiptMapperRequest;
import com.reimbes.response.ReceiptMapperResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.reimbes.constant.UrlConstants.URL_RECEIPT_MAPPER;

@Service
public class ReceiptMapperServiceImpl implements ReceiptMapperService {

    private static Logger log = LoggerFactory.getLogger(ReceiptMapperServiceImpl.class);

    @Override
    public Transaction translateImage(String imageId, String imageValue) throws Exception {
        // return empty transaction, if only request time reach 10 secs [TIMEOUT]

        final String uri = URL_RECEIPT_MAPPER;

        HttpEntity<ReceiptMapperRequest> request = new HttpEntity<>(
                ReceiptMapperRequest.builder()
                        .requestId(imageId)
                        .image(imageValue)
                        .build()
        );

        log.info("REQUEST: ", request);

        // do request
        RestTemplate restTemplate = new RestTemplate();
        try {
            Object response = restTemplate.postForEntity(uri, request, ReceiptMapperResponse.class);
            log.info("RESPONSE: ", response);

            ObjectMapper mapper = new ObjectMapper();
            ReceiptMapperResponse result = mapper.convertValue(response, ReceiptMapperResponse.class);
            log.info("RESULT: ", result);

            Transaction predictedTransaction = new Transaction();
            predictedTransaction.setAmount(result.getAmount());
            predictedTransaction.setImage(result.getRequestId());

            return predictedTransaction;
        } catch (Exception e) {
            log.info("Request error! Message: ", e.getMessage());
            return new Transaction();
        }

    }
}
