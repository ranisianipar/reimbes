package com.reimbes.request;

import com.reimbes.FamilyMember;
import lombok.Data;

import java.util.List;

@Data
public class MedicalRequest {

    private String title;

    private long amount;
    private long date;

    private FamilyMember patient;

    private List<String> attachments;

}
