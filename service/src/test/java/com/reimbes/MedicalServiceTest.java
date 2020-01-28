package com.reimbes;

import com.reimbes.constant.UrlConstants;
import com.reimbes.exception.DataConstraintException;
import com.reimbes.exception.NotFoundException;
import com.reimbes.exception.ReimsException;
import com.reimbes.implementation.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.*;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.reimbes.ReimsUser.Role.USER;
import static com.reimbes.interfaces.UtilsService.countAge;
import static org.junit.gen5.api.Assertions.assertEquals;
import static org.junit.gen5.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
@ComponentScan(basePackageClasses = MedicalServiceImpl.class)
public class MedicalServiceTest {

    @Mock
    private AuthServiceImpl authService;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private FamilyMemberServiceImpl familyMemberService;

    @Mock
    private UtilsServiceImpl utilsServiceImpl;

    @Mock
    private MedicalRepository repository;

    @InjectMocks
    private MedicalServiceImpl service;


    private Medical medical;
    private Medical medicalRequest;
    private MedicalReport report;
    private List<Medical> medicals = new ArrayList<>();
    private List<String> files = new ArrayList<>();
    private Set<MedicalReport> attachments = new HashSet<>();

    private Pageable pageRequest = new PageRequest(1, 5, new Sort(Sort.Direction.DESC, "createdAt"));
    private Pageable pageForQuery = new PageRequest(0, pageRequest.getPageSize(), pageRequest.getSort());

    private ReimsUser user;
    private ReimsUser admin;
    private FamilyMember member;


    @Before
    public void setUp() throws Exception {
        admin = ReimsUser.ReimsUserBuilder()
                .username("admin")
                .password("adminhaha")
                .role(ReimsUser.Role.ADMIN)
                .id(1)
                .build();

        // USER with HIS FAMILY
        user = ReimsUser.ReimsUserBuilder()
                .username("joker")
                .password("HEHE")
                .role(USER)
                .id(1)
                .gender(ReimsUser.Gender.MALE)
                .dateOfBirth(new SimpleDateFormat("dd/MM/yyyy").parse("13/05/1998"))
                .build();

        member = FamilyMember.FamilyMemberBuilder()
                .name("Decemberkid")
                .dateOfBirth(new Date())
                .relationship(FamilyMember.Relationship.SPOUSE)
                .familyMemberOf(user)
                .id(user.getId() * 10)
                .build();

        report = MedicalReport.builder()
                .id(0)
                .image("haha.jpg")
                .build();

        medical = Medical.builder()
                .medicalUser(user)
                .amount(123)
                .date(new Date().getTime())
                .title("HEHE")
                .createdAt(new Date().getTime())
                .id(1)
                .build();

        medicalRequest = Medical.builder()
                .medicalUser(user)
                .amount(123)
                .date(new Date().getTime())
                .title("HEHE")
                .createdAt(new Date().getTime())
                .id(1)
                .build();

        files.add(report.getImage() + "base64 ceritanya");
        medicals.add(medical);
    }

    @Test
    public void errorThrown_whenUserCreateMedical_withInvalidData() throws ReimsException {
        when(authService.getCurrentUser()).thenReturn(user);

        medical.setMedicalUser(user);
        medical.setAmount(0);
        assertThrows(DataConstraintException.class, () -> {
            service.create(medical, null);
        });
    }

    @Test
    public void returnMedical_whenUserCreateMedical_forHimself() throws ReimsException {
        when(authService.getCurrentUser()).thenReturn(user);
        when(utilsServiceImpl.uploadImage(files.get(0), user.getId(), UrlConstants.SUB_FOLDER_REPORT)).thenReturn(report.getImage());
        when(utilsServiceImpl.getCurrentTime()).thenReturn(medical.getDate());

        medical.setMedicalUser(user);
        medical.setPatient(user);
        medical.setAge(countAge(user.getDateOfBirth()));

        report.setMedicalImage(medical);
        attachments.add(report);
        medical.setAttachments(attachments);

        when(repository.save(medical)).thenReturn(medical);

        assertEquals(medical, service.create(medicalRequest, files));
    }

    @Test
    public void returnMedical_whenUserCreateMedical_forHisFamilyMember() throws ReimsException {
        when(authService.getCurrentUser()).thenReturn(user);
        when(familyMemberService.getById(member.getId())).thenReturn(member);
        when(utilsServiceImpl.getCurrentTime()).thenReturn(medical.getDate());

        medical.setMedicalUser(user);
        medical.setPatient(member);
        medical.setAge(countAge(member.getDateOfBirth()));

        when(repository.save(medical)).thenReturn(medical);

        medicalRequest.setPatient(member);
        assertEquals(medical, service.create(medicalRequest, null));
    }


    @Test
    public void errorThrown_whenUserUpdateMedical_withInvalidData() throws ReimsException {
        when(authService.getCurrentUser()).thenReturn(user);
        when(repository.findOne(medical.getId())).thenReturn(medical);

        medicalRequest.setAmount(0);

        assertThrows(DataConstraintException.class, () -> {
            service.update(medical.getId(), medicalRequest, null);
        });
    }

    @Test
    public void errorThrown_whenUserUpdateMedical() throws ReimsException {
        // old medical
        medical.setMedicalUser(user);
        medical.setPatient(user);
        medical.setAge(countAge(user.getDateOfBirth()));

        when(authService.getCurrentUser()).thenReturn(user);
        when(repository.findOne(medical.getId())).thenReturn(medical);

        medicalRequest.setAmount(0);


        assertThrows(DataConstraintException.class, () -> {
            service.update(medical.getId(), medicalRequest, null);
        });
    }

    @Test
    public void returnMedical_whenUserUpdateMedical() throws ReimsException {
        // old medical
        medical.setMedicalUser(user);
        medical.setPatient(user);
        medical.setAge(countAge(user.getDateOfBirth()));

        when(authService.getCurrentUser()).thenReturn(user);
        when(repository.findOne(medical.getId())).thenReturn(medical);

        // medical new data
        Medical expectedResult = Medical.builder()
                .medicalUser(user)
                .amount(1)
                .date(medical.getDate())
                .title(medical.getTitle())
                .createdAt(medical.getCreatedAt())
                .medicalUser(medical.getMedicalUser())
                .patient(medical.getPatient())
                .id(medical.getId())
                .age(countAge(user.getDateOfBirth()))
                .build();

        medicalRequest.setAmount(expectedResult.getAmount());
        when(repository.save(expectedResult)).thenReturn(expectedResult);

        assertEquals(expectedResult ,service.update(medical.getId(), medicalRequest, null));
    }

    @Test
    public void returnMedical_whenUserUpdateMedical_forHisFamilyMember() throws ReimsException {
        // old medical
        medical.setMedicalUser(user);
        medical.setPatient(user);
        medical.setAge(countAge(user.getDateOfBirth()));

        when(authService.getCurrentUser()).thenReturn(user);
        when(repository.findOne(medical.getId())).thenReturn(medical);
        when(familyMemberService.getById(member.getId())).thenReturn(member);

        // medical new data
        Medical expectedResult = Medical.builder()
                .amount(medical.getAmount())
                .date(medical.getDate())
                .title(medical.getTitle())
                .createdAt(medical.getCreatedAt())
                .medicalUser(medical.getMedicalUser())
                .patient(member) // update value
                .id(medical.getId())
                .age(countAge(member.getDateOfBirth())) // update value
                .build();

        medicalRequest.setPatient(expectedResult.getPatient());
        medicalRequest.setAge(expectedResult.getAge());
        when(repository.save(expectedResult)).thenReturn(expectedResult);

        assertEquals(expectedResult ,service.update(medical.getId(), medicalRequest, null));
    }



    @Test
    public void errorThrown_whenUserTryToGetUnexistMedical() {
        assertThrows(NotFoundException.class, () -> {
            service.get(219102);
        });
    }

    @Test
    public void errorThrown_whenRandomUserTryToGetOtherUserMedical() throws ReimsException {
        ReimsUser user2 = ReimsUser.ReimsUserBuilder()
                .role(USER)
                .password("AAA")
                .username("BBB")
                .createdAt(new Date().getTime())
                .gender(ReimsUser.Gender.MALE)
                .dateOfBirth(new Date())
                .id(user.getId() + 1)
                .build();

        when(authService.getCurrentUser()).thenReturn(user2);
        assertThrows(NotFoundException.class, () -> {
            service.get(medical.getId());
        });
    }

    @Test
    public void returnMedical_whenAdminGetMedicalById() throws ReimsException {
        when(authService.getCurrentUser()).thenReturn(admin);
        when(repository.findOne(medical.getId())).thenReturn(medical);
        medical.setMedicalUser(user);

        assertEquals(medical, service.get(medical.getId()));
    }

    @Test
    public void returnListOfMedical_whenMedicalsGetByDate() throws ReimsException {
        long start; long end;
        start = end = new Date().getTime();
        when(repository.findByDateBetweenAndMedicalUser(start, end, user)).thenReturn(medicals);
        when(authService.getCurrentUser()).thenReturn(user);

        assertEquals(medicals, service.getByDate(start, end));
    }

    @Test
    public void returnListOfMedical_whenMedicalsGetByDate_withStartAndEndDateHaveNullValue() throws ReimsException {
        when(repository.findByMedicalUser(user)).thenReturn(medicals);
        when(authService.getCurrentUser()).thenReturn(user);

        assertEquals(medicals, service.getByDate(null, null));
    }

    @Test
    public void errorThrown_whenUserTryToRemoveUnexistMedical() {

        assertThrows(NotFoundException.class, () -> {
            service.delete(1241512);
        });
    }

    @Test
    public void errorThrown_whenRandomUserTryToRemoveOthersMedical() throws ReimsException {
        ReimsUser user2 = ReimsUser.ReimsUserBuilder()
                .role(USER)
                .password("AAA")
                .username("BBB")
                .createdAt(new Date().getTime())
                .gender(ReimsUser.Gender.MALE)
                .dateOfBirth(new Date())
                .id(user.getId() + 1)
                .build();

        when(authService.getCurrentUser()).thenReturn(user2);

        assertThrows(NotFoundException.class, () -> {
            service.delete(medical.getId());
        });
    }

    @Test
    public void hasMedicalRemoved_whenUserDeleteMedicalById() throws ReimsException {
        when(authService.getCurrentUser()).thenReturn(user);
        when(repository.findOne(medical.getId())).thenReturn(medical);

        service.delete(medical.getId());
        verify(repository, times(1)).delete(medical.getId());
    }

    @Test
    public void returnPageContainedListOfMedicals_whenAdminGetAllMedicals_withoutQuery() throws ReimsException {
        Long NULL_IN_LONG = new Long(0);
        Page expectedResult = new PageImpl(medicals);

        when(authService.getCurrentUser()).thenReturn(admin);
        when(repository.findByTitleContainingIgnoreCase(medical.getTitle(), pageForQuery)).thenReturn(expectedResult);

        assertEquals(expectedResult, service.getAll(pageRequest, medical.getTitle(), NULL_IN_LONG, NULL_IN_LONG, NULL_IN_LONG));
    }

    @Test
    public void returnPageContainedListOfMedicals_whenAdminGetAllMedicals_withUserQueryButNoDateRange() throws ReimsException {
        Long NULL_IN_LONG = new Long(0);
        Page expectedResult = new PageImpl(medicals);

        when(authService.getCurrentUser()).thenReturn(admin);
        when(userService.get(user.getId())).thenReturn(user);
        when(repository.findByTitleContainingIgnoreCaseAndMedicalUser(medical.getTitle(), user, pageForQuery))
                .thenReturn(expectedResult);

        assertEquals(expectedResult, service.getAll(pageRequest, medical.getTitle(), NULL_IN_LONG, NULL_IN_LONG, user.getId()));
    }


    @Test
    public void returnPageContainedListOfMedicals_whenAdminGetAllMedicals_withDateRangeQuery() throws ReimsException {
        Long date = medical.getDate();
        Long NULL_IN_LONG = new Long(0);
        Page expectedResult = new PageImpl(medicals);

        when(authService.getCurrentUser()).thenReturn(admin);
        when(userService.get(user.getId())).thenReturn(user);
        when(repository.findByTitleContainingIgnoreCaseAndDateBetween(medical.getTitle(), date, date, pageForQuery))
                .thenReturn(expectedResult);

        assertEquals(expectedResult, service.getAll(pageRequest, medical.getTitle(), date, date, NULL_IN_LONG));
    }

    @Test
    public void returnPageContainedListOfMedicals_whenAdminGetAllMedicals_withUserAndDateRangeQuery() throws ReimsException {
        Long date = medical.getDate();
        Page expectedResult = new PageImpl(medicals);

        when(authService.getCurrentUser()).thenReturn(admin);
        when(userService.get(user.getId())).thenReturn(user);
        when(repository.findByTitleContainingIgnoreCaseAndDateBetweenAndMedicalUser(medical.getTitle(), date, date, user, pageForQuery))
                .thenReturn(expectedResult);

        assertEquals(expectedResult, service.getAll(pageRequest, medical.getTitle(), date, date, user.getId()));
    }

    @Test
    public void returnPageContainedListOfMedicals_whenUserGetAllMedicals_withDateRangeQuery() throws ReimsException {
        Long date = medical.getDate();
        Page expectedResult = new PageImpl(medicals);

        when(authService.getCurrentUser()).thenReturn(user);
        when(userService.get(user.getId())).thenReturn(user);
        when(repository.findByTitleContainingIgnoreCaseAndDateBetweenAndMedicalUser(medical.getTitle(), date, date, user, pageForQuery))
                .thenReturn(expectedResult);

        assertEquals(expectedResult, service.getAll(pageRequest, medical.getTitle(), date, date, new Long(0)));
    }

    @Test
    public void returnPageContainedListOfMedicals_whenUserGetAllMedicals_withoutDateRange() throws ReimsException {
        Long NULL_IN_LONG = new Long(0);
        Page expectedResult = new PageImpl(medicals);

        when(authService.getCurrentUser()).thenReturn(user);
        when(userService.get(user.getId())).thenReturn(user);
        when(repository.findByTitleContainingIgnoreCaseAndMedicalUser(medical.getTitle(), user, pageForQuery))
                .thenReturn(expectedResult);

        assertEquals(expectedResult, service.getAll(pageRequest, medical.getTitle(), NULL_IN_LONG, NULL_IN_LONG, user.getId()));
    }



}
