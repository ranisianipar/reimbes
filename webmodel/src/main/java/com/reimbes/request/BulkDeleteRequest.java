package com.reimbes.request;

import lombok.Data;

import java.util.List;

@Data
public class BulkDeleteRequest<T> {
    private List<String> ids;
}
