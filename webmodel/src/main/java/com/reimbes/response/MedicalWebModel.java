package com.reimbes.response;

import com.reimbes.Patient;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class MedicalWebModel {
    private long id;
    private String title;
    private long age;
    private long amount;

    private long date;

    private Patient patient;
    private List<String> attachments; // images

}
