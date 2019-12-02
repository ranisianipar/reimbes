package com.reimbes.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UploadImageByteRequest {
    @JsonProperty("image")
    private String image;
}
