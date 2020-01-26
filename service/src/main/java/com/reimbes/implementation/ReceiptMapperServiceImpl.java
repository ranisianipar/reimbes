package com.reimbes.implementation;

import com.reimbes.interfaces.ReceiptMapperService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.reimbes.constant.UrlConstants.URL_RECEIPT_MAPPER;

@Service
public class ReceiptMapperServiceImpl implements ReceiptMapperService {

    @Override
    public String translateImage(String imageId, String imageValue) throws Exception {
        final String uri = URL_RECEIPT_MAPPER;

        // do request
        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.getForObject(uri, String.class);
    }
}
