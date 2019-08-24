package com.reimbes.response;

import lombok.Data;

@Data
public class Paging {
    private int pageNumber;
    private int pageSize;

    // MANUAL
    private int totalPages;
    private int totalRecords;
}
