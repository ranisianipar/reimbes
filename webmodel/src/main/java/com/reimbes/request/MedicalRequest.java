package com.reimbes.request;

import com.reimbes.FamilyMember;
import lombok.Data;

@Data
public class MedicalRequest {

    private String title;

    private long amount;
    private long date;

    private long dateOfBirth; // for age calculation

    private FamilyMember patient;

    private String attachement;

}
