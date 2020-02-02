package com.reimbes.interfaces;

import com.reimbes.Transaction;

public interface ReceiptMapperService {
    // imageId: any unique identifier of attachments (ex. attachments path)
    // imageValue: attachments in Base64 format
    Transaction map(Transaction transaction) throws Exception;
}
