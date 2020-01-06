package com.reimbes.implementation;

import com.reimbes.*;
import com.reimbes.constant.UrlConstants;
import com.reimbes.exception.DataConstraintException;
import com.reimbes.exception.NotFoundException;
import com.reimbes.exception.ReimsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static com.reimbes.ReimsUser.Role.ADMIN;
import static com.reimbes.constant.ResponseCode.BAD_REQUEST;
import static com.reimbes.implementation.Utils.countAge;

@Service
public class MedicalServiceImpl implements MedicalService {

    private static Logger log = LoggerFactory.getLogger(MedicalServiceImpl.class);


    @Autowired
    private MedicalRepository medicalRepository;

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private FamilyMemberServiceImpl familyMemberService;

    @Autowired
    private Utils utils;

//    MULTIPLE upload
    public Medical create(Medical medical, List<String> files) throws ReimsException {
        ReimsUser currentUser = authService.getCurrentUser();
        log.info("Create method called. With files: " + files);

        validate(medical);

        if (files != null) {
            Set<MedicalReport> reports = new HashSet<>();
            log.info("Create register all medical attachments those have been attached.");
            for (String file: files) {
                reports.add(
                        MedicalReport.builder()
                                .image(utils.uploadImage(file, currentUser.getId(), UrlConstants.SUB_FOLDER_REPORT))
                                .medicalImage(medical)
                                .build()
                );
            }
            medical.setAttachments(reports);
        }

        // [CHECK]: user claim medical for himself or not
        // POTENTIALLY THROW ERROR
        Patient patient = (medical.getPatient() == null || medical.getPatient().getId() == 0)
                ? currentUser : familyMemberService.getById(medical.getPatient().getId());
        medical.setMedicalUser(currentUser);

        medical.setPatient(patient);
        medical.setAge(countAge(patient.getDateOfBirth()));

        log.info("MEDICAL --> " + medical.toString());

        return medicalRepository.save(medical);
    }

    @Override
    public Medical update(long id, Medical newMedical, List<String> files) throws ReimsException {
        Medical old = medicalRepository.findOne(id);
        ReimsUser currentUser = authService.getCurrentUser();
        Set<MedicalReport> reports = new HashSet<>();

        validate(newMedical);
        old.setAmount(newMedical.getAmount());

        Patient patient = (newMedical.getPatient() == null || newMedical.getPatient().getId() == 0)
                ? currentUser : familyMemberService.getById(newMedical.getPatient().getId());
        old.setPatient(patient);
        old.setAge(countAge(patient.getDateOfBirth()));
        old.setDate(newMedical.getDate());

        if (files != null) {
            log.info("Update register all medical attachments those have been attached.");
            for (String file: files) {
                reports.add(
                        MedicalReport.builder()
                                .image(utils.uploadImage(file, currentUser.getId(), UrlConstants.SUB_FOLDER_REPORT))
                                .medicalImage(old)
                                .build()
                );
            }
            old.setAttachments(reports);
        }

        return medicalRepository.save(old);
    }

    @Override
    public Medical get(long id) throws ReimsException {
        Medical report = medicalRepository.findOne(id);
        ReimsUser currentUser = authService.getCurrentUser();
        if (report == null || currentUser.getRole() == ADMIN || report.getMedicalUser() != currentUser)
            throw new NotFoundException("MEDICAL_REPORT");
        return report;
    }

    // userId == null when this method called from medical controller
    @Override
    public Page<Medical> getAll(Pageable page, String title, Long start, Long end, String userId) throws ReimsException {
        ReimsUser currentUser = authService.getCurrentUser();

        // enabling query by specific for admin. In the other hand, user get his medical report list
        ReimsUser queryUser;
        if (userId != null && currentUser.getRole() == ADMIN) {
            queryUser = userService.get(Long.parseLong(userId));
        } else if (currentUser.getRole() == ADMIN) {
            queryUser = null;
        } else {
            queryUser = currentUser;
        }

        int index = page.getPageNumber() - 1;
        if (index < 0) index = 0;
        Pageable pageRequest = new PageRequest(index, page.getPageSize(), page.getSort());

        if (start == 0 && end == 0) {
            if (queryUser == null) return medicalRepository.findByTitleContainingIgnoreCase(title, page);
            return medicalRepository.findByTitleContainingIgnoreCaseAndMedicalUser(title, queryUser, page);
        } else {
            if (queryUser == null) return medicalRepository.findByTitleContainingIgnoreCaseAndDateBetween(title, start, end, page);
            return medicalRepository.findByTitleContainingIgnoreCaseAndDateBetweenAndMedicalUser(title, start, end, queryUser, pageRequest);
        }
    }



    @Override
    public void delete(long id) throws ReimsException {
        Medical report = medicalRepository.findOne(id);
        if (report == null || report.getMedicalUser() != authService.getCurrentUser())
            throw new NotFoundException("MEDICAL_REPORT");

        medicalRepository.delete(id);
    }


    public byte[] getImage(String imagePath) throws ReimsException {
        ReimsUser currentUser = authService.getCurrentUser();

        if (!imagePath.contains(String.format("/%d/", currentUser.getId()))) throw new NotFoundException("Image");
        try {
            return utils.getFile(imagePath);
        }   catch (IOException e) {
            throw new ReimsException(e.getMessage(), HttpStatus.BAD_REQUEST, BAD_REQUEST);
        }

    }

    private void validate(Medical report) throws DataConstraintException {
        ArrayList<String> errors = new ArrayList();

        if (report.getAmount() <= 0) errors.add("PROHIBITED_AMOUNT");

        if (!errors.isEmpty()) throw new DataConstraintException(errors.toString());
    }
}
