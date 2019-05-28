package com.reimbes.response;

import lombok.Data;

@Data
public class Paging {
    private int number;
    private int size;
    private int totalPages;
    private int totalRecords;
}
