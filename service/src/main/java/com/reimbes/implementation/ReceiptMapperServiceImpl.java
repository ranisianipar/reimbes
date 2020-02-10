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
    private final String uri = URL_RECEIPT_MAPPER;

    /*
    * imageId: image path
    * transaction: consist category and image value
    *
    * */
    public Transaction map(Transaction transaction) {
        ReceiptMapperResponse result = doPost(transaction.getImage());

        transaction.setAmount(result.getAmount());
        return transaction;
    }

    private ReceiptMapperResponse doPost(String imageValue) {
        ReceiptMapperResponse result = ReceiptMapperResponse.builder().build();

        HttpEntity<ReceiptMapperRequest> request = new HttpEntity<>(
                ReceiptMapperRequest.builder()
                        .image(imageValue)
                        .build()
        );

        RestTemplate restTemplate = new RestTemplate();
        try {
            Object response = restTemplate.postForEntity(uri, request, ReceiptMapperResponse.class);

            ObjectMapper mapper = new ObjectMapper();
            result = mapper.convertValue(response, ReceiptMapperResponse.class);
        } catch (Exception e) {
            log.info("Request error! Message: ", e.getMessage());
        }

        return result;
    }
}
