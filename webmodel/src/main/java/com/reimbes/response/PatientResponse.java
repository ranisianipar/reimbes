package com.reimbes.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class PatientResponse {

    public long id;
    public String name;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date dateOfBirth;

}
