package com.reimbes.request;

import lombok.Data;

import java.util.List;

@Data
public class UploadImageByteRequest {
    private List<String> attachments;
}
