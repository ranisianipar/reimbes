package com.reimbes.response;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class MedicalResponse {

    private PatientResponse patient;

    private List<String> reports; // images

    @Data
    class PatientResponse {
        private String name;
        private Date birthOfDate;
    }
}
