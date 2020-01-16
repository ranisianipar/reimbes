package com.reimbes.interfaces;

public interface ReceiptMapperService {
    // imageId: any unique identifier of image (ex. image path)
    // imageValue: image in Base64 format
    String translateImage(String imageId, String imageValue) throws Exception;
}
