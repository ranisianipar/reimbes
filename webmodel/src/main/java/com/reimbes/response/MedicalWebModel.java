package com.reimbes.response;

import lombok.Data;

import java.util.List;

@Data
public class MedicalWebModel {
    private long id;
    private String title;
    private long age;
    private long amount;

    private long date;

    private PatientResponse patient;
    private List<String> attachments; // images

}
