package com.reimbes.interfaces;

import com.reimbes.Transaction;

public interface ReceiptMapperService {
    // imageId: any unique identifier of image (ex. image path)
    // imageValue: image in Base64 format
    Transaction translateImage(String imageId, String imageValue) throws Exception;
}
