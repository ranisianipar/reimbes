package com.reimbes;

import com.reimbes.constant.UrlConstants;
import com.reimbes.implementation.MedicalServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = UrlConstants.CROSS_ORIGIN_URL)
@RestController
@RequestMapping(UrlConstants.API_PREFIX + UrlConstants.MEDICAL_PREFIX)
public class MedicalController {

    @Autowired
    private MedicalServiceImpl medicalService;

    public List<Medical> getAll() {
        return medicalService.getAll(null, "",0,0).getContent();
    }
}
