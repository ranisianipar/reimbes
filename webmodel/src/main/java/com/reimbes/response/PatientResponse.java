package com.reimbes.response;

import lombok.Data;

import java.util.Date;

@Data
public class PatientResponse {

    public long id;
    public String name;
    private Date dateOfBirth;

}
