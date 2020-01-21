package com.reimbes.implementation;

import com.reimbes.interfaces.ReceiptMapperService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ReceiptMapperServiceImpl implements ReceiptMapperService {

    @Override
    public String translateImage(String imageId, String imageValue) throws Exception {
        final String uri = "http://receipt-mapper.herokuapp.com/image";

        // do request
        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.getForObject(uri, String.class);
    }
}
