package com.reimbes;

import com.reimbes.exception.DataConstraintException;
import com.reimbes.exception.NotFoundException;
import com.reimbes.exception.ReimsException;
import com.reimbes.implementation.*;
import com.reimbes.interfaces.AuthService;
import com.reimbes.interfaces.FamilyMemberService;
import com.reimbes.interfaces.UserService;
import com.reimbes.interfaces.UtilsService;
import org.junit.After;
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
import static com.reimbes.constant.UrlConstants.SUB_FOLDER_REPORT;
import static com.reimbes.interfaces.UtilsService.countAge;
import static org.junit.gen5.api.Assertions.assertEquals;
import static org.junit.gen5.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
@ComponentScan(basePackageClasses = MedicalServiceImpl.class)
public class MedicalServiceTest {

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @Mock
    private FamilyMemberService familyMemberService;

    @Mock
    private UtilsService utilsService;

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

    @After
    public void tearDown() {
        verifyNoMoreInteractions(utilsService, familyMemberService, userService, authService, repository);
    }

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

        verify(authService).getCurrentUser();
    }

    @Test
    public void returnMedical_whenUserCreateMedical_forHimself() throws ReimsException {
        Medical result;

        when(authService.getCurrentUser()).thenReturn(user);
        when(utilsService.uploadImage(files.get(0), user.getId(), SUB_FOLDER_REPORT)).thenReturn(report.getImage());
        when(utilsService.getCurrentTime()).thenReturn(medical.getDate());

        medical.setMedicalUser(user);
        medical.setPatient(user);
        medical.setAge(countAge(user.getDateOfBirth()));

        report.setMedicalImage(medical);
        attachments.add(report);
        medical.setAttachments(attachments);

        when(repository.save(medical)).thenReturn(medical);

        result = service.create(medicalRequest, files);
        verify(authService).getCurrentUser();
        verify(utilsService).uploadImage(files.get(0), user.getId(), SUB_FOLDER_REPORT); // upload file
        verify(utilsService).getCurrentTime(); // fill createdAt field
        verify(repository).save(medical);

        assertEquals(medical, result);
    }

    @Test
    public void returnMedical_whenUserCreateMedical_forHisFamilyMember() throws ReimsException {
        Medical result;
        when(authService.getCurrentUser()).thenReturn(user);
        when(familyMemberService.getById(member.getId())).thenReturn(member);
        when(utilsService.getCurrentTime()).thenReturn(medical.getDate());

        medical.setMedicalUser(user);
        medical.setPatient(member);
        medical.setAge(countAge(member.getDateOfBirth()));

        when(repository.save(medical)).thenReturn(medical);

        medicalRequest.setPatient(member);
        result = service.create(medicalRequest, null);
        verify(authService).getCurrentUser();
        verify(utilsService).getCurrentTime(); // fill createdAt field
        verify(familyMemberService).getById(medical.getPatient().getId()); // determine patient
        verify(repository).save(medical);

        assertEquals(medical, result);
    }


    @Test
    public void errorThrown_whenUserUpdateMedical_withInvalidData() throws ReimsException {
        when(authService.getCurrentUser()).thenReturn(user);
        when(repository.findOne(medical.getId())).thenReturn(medical);

        medicalRequest.setAmount(0);

        assertThrows(DataConstraintException.class, () -> {
            service.update(medical.getId(), medicalRequest, null);
        });

        verify(authService).getCurrentUser();
        verify(repository).findOne(medical.getId());
    }

    @Test
    public void returnMedical_whenUserUpdateMedical() throws ReimsException {
        Medical result;

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

        result = service.update(medical.getId(), medicalRequest, null);
        verify(authService).getCurrentUser();
        verify(repository).findOne(medical.getId());
        verify(repository).save(expectedResult);

        assertEquals(expectedResult, result);
    }

    @Test
    public void returnMedical_whenUserUpdateMedical_forHisFamilyMember() throws ReimsException {
        Medical result;

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

        result = service.update(medical.getId(), medicalRequest, null);
        verify(authService).getCurrentUser();
        verify(repository).findOne(medical.getId());
        verify(familyMemberService).getById(medical.getPatient().getId());
        verify(repository).save(expectedResult);

        assertEquals(expectedResult, result);
    }



    @Test
    public void errorThrown_whenUserTryToGetUnexistMedical() throws ReimsException {
        long id = 211;
        assertThrows(NotFoundException.class, () -> {
            service.get(id);
        });

        verify(repository).findOne(id);
        verify(authService).getCurrentUser();
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

        verify(authService).getCurrentUser();
        verify(repository).findOne(medical.getId());
    }

    @Test
    public void returnMedical_whenAdminGetMedicalById() throws ReimsException {
        Medical result;
        when(authService.getCurrentUser()).thenReturn(admin);
        when(repository.findOne(medical.getId())).thenReturn(medical);
        medical.setMedicalUser(user);

        result = service.get(medical.getId());
        verify(authService).getCurrentUser();
        verify(repository).findOne(medical.getId());

        assertEquals(medical, result);
    }

    @Test
    public void returnListOfMedical_whenMedicalsGetByDate() throws ReimsException {
        List<Medical> result;

        long start; long end;
        start = end = new Date().getTime();
        when(repository.findByDateBetweenAndMedicalUser(start, end, user)).thenReturn(medicals);
        when(authService.getCurrentUser()).thenReturn(user);

        result = service.getByDate(start, end);
        verify(authService).getCurrentUser();
        verify(repository).findByDateBetweenAndMedicalUser(start, end, user);

        assertEquals(medicals, result);
    }

    @Test
    public void returnListOfMedical_whenMedicalsGetByDate_withStartAndEndDateHaveNullValue() throws ReimsException {
        List<Medical> result;

        when(repository.findByMedicalUser(user)).thenReturn(medicals);
        when(authService.getCurrentUser()).thenReturn(user);

        result = service.getByDate(null, null);
        verify(authService).getCurrentUser();
        verify(repository).findByMedicalUser(user);

        assertEquals(medicals, result);
    }

    @Test
    public void errorThrown_whenUserTryToRemoveUnexistMedical() {
        long id = 15315;
        assertThrows(NotFoundException.class, () -> {
            service.delete(id);
        });
        verify(repository).findOne(id);
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

        verify(repository).findOne(medical.getId());
    }

    @Test
    public void hasMedicalRemoved_whenUserDeleteMedicalById() throws ReimsException {
        when(authService.getCurrentUser()).thenReturn(user);
        when(repository.findOne(medical.getId())).thenReturn(medical);

        service.delete(medical.getId());
        verify(repository).findOne(medical.getId());
        verify(authService).getCurrentUser();
        verify(repository).delete(medical.getId());
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
        Page result;
        long nullDate = 0;
        Page expectedResult = new PageImpl(medicals);

        when(authService.getCurrentUser()).thenReturn(admin);
        when(userService.get(user.getId())).thenReturn(user);
        when(repository.findByTitleContainingIgnoreCaseAndMedicalUser(medical.getTitle(), user, pageForQuery))
                .thenReturn(expectedResult);

        result = service.getAll(pageRequest, medical.getTitle(), nullDate, nullDate, user.getId());
        verify(authService).getCurrentUser();
        verify(userService).get(user.getId());
        verify(repository).findByTitleContainingIgnoreCaseAndMedicalUser(medical.getTitle(), user, pageForQuery);

        assertEquals(expectedResult, result);
    }


    @Test
    public void returnPageContainedListOfMedicals_whenAdminGetAllMedicals_withDateRangeQuery() throws ReimsException {
        Page result;
        long date = medical.getDate();
        long nullDate = 0;
        Page expectedResult = new PageImpl(medicals);

        when(authService.getCurrentUser()).thenReturn(admin);
        when(repository.findByTitleContainingIgnoreCaseAndDateBetween(medical.getTitle(), date, date, pageForQuery))
                .thenReturn(expectedResult);

        result = service.getAll(pageRequest, medical.getTitle(), date, date, nullDate);
        verify(authService).getCurrentUser();
        verify(repository).findByTitleContainingIgnoreCaseAndDateBetween(medical.getTitle(), date, date, pageForQuery);

        assertEquals(expectedResult, result);
    }

    @Test
    public void returnPageContainedListOfMedicals_whenAdminGetAllMedicals_withUserAndDateRangeQuery() throws ReimsException {
        Page result;
        long date = medical.getDate();
        Page expectedResult = new PageImpl(medicals);

        when(authService.getCurrentUser()).thenReturn(admin);
        when(userService.get(user.getId())).thenReturn(user);
        when(repository.findByTitleContainingIgnoreCaseAndDateBetweenAndMedicalUser(medical.getTitle(), date, date, user, pageForQuery))
                .thenReturn(expectedResult);

        result = service.getAll(pageRequest, medical.getTitle(), date, date, user.getId());
        verify(authService).getCurrentUser();
        verify(userService).get(user.getId());
        verify(repository).findByTitleContainingIgnoreCaseAndDateBetweenAndMedicalUser(medical.getTitle(), date, date, user, pageForQuery);

        assertEquals(expectedResult, result);
    }

    @Test
    public void returnPageContainedListOfMedicals_whenUserGetAllMedicals_withDateRangeQuery() throws ReimsException {
        Page result;
        long date = medical.getDate();
        Page expectedResult = new PageImpl(medicals);

        when(authService.getCurrentUser()).thenReturn(user);
        when(repository.findByTitleContainingIgnoreCaseAndDateBetweenAndMedicalUser(medical.getTitle(), date, date, user, pageForQuery))
                .thenReturn(expectedResult);

        result = service.getAll(pageRequest, medical.getTitle(), date, date, new Long(0));
        verify(authService).getCurrentUser();
        verify(repository).findByTitleContainingIgnoreCaseAndDateBetweenAndMedicalUser(medical.getTitle(), date, date, user, pageForQuery);

        assertEquals(expectedResult, result);
    }

    @Test
    public void returnPageContainedListOfMedicals_whenUserGetAllMedicals_withoutDateRange() throws ReimsException {
        Page result;
        long nullLong = 0;
        Page expectedResult = new PageImpl(medicals);

        when(authService.getCurrentUser()).thenReturn(user);
        when(repository.findByTitleContainingIgnoreCaseAndMedicalUser(medical.getTitle(), user, pageForQuery))
                .thenReturn(expectedResult);

        result = service.getAll(pageRequest, medical.getTitle(), nullLong, nullLong, user.getId());
        verify(authService).getCurrentUser();
        verify(repository).findByTitleContainingIgnoreCaseAndMedicalUser(medical.getTitle(), user, pageForQuery);

        assertEquals(expectedResult, result);
    }



}
