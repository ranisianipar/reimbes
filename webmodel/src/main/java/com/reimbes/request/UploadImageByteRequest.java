package com.reimbes.request;

import com.reimbes.Transaction;
import lombok.Data;

import java.util.List;

@Data
public class UploadImageByteRequest {
    private Transaction.Category category;
    private List<String> attachments;
}
