package com.reimbes;

import com.reimbes.implementation.ReportGeneratorServiceImpl;
import com.reimbes.interfaces.AuthService;
import com.reimbes.interfaces.MedicalService;
import com.reimbes.interfaces.TransactionService;
import com.reimbes.interfaces.UtilsService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static com.reimbes.ReimsUser.Role.USER;
import static com.reimbes.constant.General.*;
import static com.reimbes.constant.UrlConstants.GDN_LOGO_PATH;
import static com.reimbes.constant.UrlConstants.PROJECT_ROOT;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
@ComponentScan(basePackageClasses = {ReportGeneratorServiceImpl.class})
public class ReportGeneratorServiceTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private UtilsService utilsService;

    @Mock
    private AuthService authService;

    @Mock
    private MedicalService medicalService;

    @InjectMocks
    private ReportGeneratorServiceImpl reportGeneratorService;

    private String projectRootPath = "C:\\Users\\Z\\Documents\\code\\haha\\reimbes\\";
    private ReimsUser user;
    private Set<Transaction> fuels = new HashSet<>();
    private Set<Transaction> parkings = new HashSet<>();
    private Set<Medical> medicals = new HashSet<>();

    @After
    public void tearDown() {
        verifyNoMoreInteractions(medicalService, utilsService, transactionService, authService);
    }

    @Before
    public void setup() {
        user = ReimsUser.ReimsUserBuilder()
                .username("HAHA")
                .password("xoxo")
                .role(USER)
                .id(0)
                .build();
        user.setName(user.getUsername());

        Transaction fuel = new Fuel();
        fuel.setTitle("BENSIN");
        fuel.setAmount(150000);
        ((Fuel) fuel).setLiters(15);
        fuel.setImage("0/a.jpg");
        fuel.setDate(Instant.now().getEpochSecond());
        fuel.setReimsUser(user);
        ((Fuel) fuel).setType(Fuel.Type.PERTALITE);
        fuel.setCategory(Transaction.Category.FUEL);


        Transaction parking = new Parking();
        parking.setTitle("PARKIR");
        parking.setAmount(21000);
        parking.setImage("0/b.jpg");
        parking.setDate(Instant.now().getEpochSecond());
        parking.setReimsUser(user);
        parking.setCategory(Transaction.Category.PARKING);
        parking.setLocation("Thamrin");

        fuels.add(fuel);
        fuels.add(fuel);
        parkings.add(parking);
        parkings.add(parking);

        user.setTransactions(fuels);

        Medical medical = Medical.builder()
                .medicalUser(user)
                .id(1)
                .createdAt(Instant.now().toEpochMilli())
                .age(20)
                .title("TEST")
                .patient(user)
                .build();
        medicals.add(medical);

        user.setMedicals(medicals);
    }

    @Test
    public void whenCreateReportWithRangeOfDateAndTypeMedical_thenReturnReportContainsListOfMedicals() throws Exception {
        Long time = Instant.now().toEpochMilli();
        String reportName = String.format("%s_%s_%s_%s.xls", user.getUsername(), MEDICAL_VALUE, time, time);

        when(authService.getCurrentUser()).thenReturn(user);
        when(medicalService.getByDate(time, time)).thenReturn(new ArrayList<>(medicals));
        when(utilsService.getFile(reportName)).thenReturn(new byte[19]);
        when(utilsService.getFile(GDN_LOGO_PATH))
                .thenReturn(Files.readAllBytes(Paths.get(projectRootPath + GDN_LOGO_PATH)));

        reportGeneratorService.getReport(user, time, time, MEDICAL_VALUE);

        verify(authService).getCurrentUser();
        verify(medicalService).getByDate(time, time);
        verify(utilsService).getFile(GDN_LOGO_PATH);

        Files.deleteIfExists(Paths.get(PROJECT_ROOT + "\\" + reportName));

    }


    @Test
    public void whenCreateReportWithoutRangeOfDateAndWithTypeMedical_thenReturnReportContainsListOfMedicals() throws Exception {
        String reportName = String.format("%s_%s_%s.xls", user.getUsername(), MEDICAL_VALUE, "ALL");

        when(authService.getCurrentUser()).thenReturn(user);
        when(medicalService.getByDate(DEFAULT_LONG_VALUE, DEFAULT_LONG_VALUE)).thenReturn(new ArrayList<>(medicals));
        when(utilsService.getFile(reportName)).thenReturn(new byte[19]);
        when(utilsService.getFile(GDN_LOGO_PATH))
                .thenReturn(Files.readAllBytes(Paths.get(projectRootPath + GDN_LOGO_PATH)));

        reportGeneratorService.getReport(user, DEFAULT_LONG_VALUE, DEFAULT_LONG_VALUE, MEDICAL_VALUE);

        verify(authService).getCurrentUser();
        verify(medicalService).getByDate(DEFAULT_LONG_VALUE, DEFAULT_LONG_VALUE);
        verify(utilsService).getFile(GDN_LOGO_PATH);

        Files.deleteIfExists(Paths.get(PROJECT_ROOT + "\\" + reportName));

    }

    @Test
    public void whenCreateReportWithoutRangeOfDateAndWithTypeFuel_thenReturnReportOfAllTransactions() throws Exception {
        String reportName = String.format("%s_%s_%s.xls", user.getUsername(), FUEL_VALUE, "ALL");

        when(authService.getCurrentUser()).thenReturn(user);
        when(transactionService.getByDateAndCategory(DEFAULT_LONG_VALUE, DEFAULT_LONG_VALUE, Transaction.Category.FUEL)).thenReturn(new ArrayList<>(fuels));
        when(utilsService.getFile(reportName)).thenReturn(new byte[19]);
        when(utilsService.getFile(GDN_LOGO_PATH))
                .thenReturn(Files.readAllBytes(Paths.get(projectRootPath + GDN_LOGO_PATH)));

        reportGeneratorService.getReport(user, DEFAULT_LONG_VALUE, DEFAULT_LONG_VALUE, FUEL_VALUE);

        verify(authService).getCurrentUser();
        verify(transactionService).getByDateAndCategory(DEFAULT_LONG_VALUE, DEFAULT_LONG_VALUE, Transaction.Category.FUEL);
        verify(utilsService).getFile(GDN_LOGO_PATH);

        Files.deleteIfExists(Paths.get(PROJECT_ROOT + "\\" + reportName));

    }

    @Test
    public void whenCreateReportWithoutRangeOfDateAndWithTypeParking_thenReturnReportOfAllTransactions() throws Exception {
        String reportName = String.format("%s_%s_%s.xls", user.getUsername(), PARKING_VALUE, "ALL");

        when(authService.getCurrentUser()).thenReturn(user);
        when(transactionService.getByDateAndCategory(DEFAULT_LONG_VALUE, DEFAULT_LONG_VALUE, Transaction.Category.PARKING)).thenReturn(new ArrayList<>(parkings));
        when(utilsService.getFile(reportName)).thenReturn(new byte[19]);
        when(utilsService.getFile(GDN_LOGO_PATH))
                .thenReturn(Files.readAllBytes(Paths.get(projectRootPath + GDN_LOGO_PATH)));

        reportGeneratorService.getReport(user, DEFAULT_LONG_VALUE, DEFAULT_LONG_VALUE, PARKING_VALUE);

        verify(authService).getCurrentUser();
        verify(transactionService).getByDateAndCategory(DEFAULT_LONG_VALUE, DEFAULT_LONG_VALUE, Transaction.Category.PARKING);
        verify(utilsService).getFile(GDN_LOGO_PATH);

        Files.deleteIfExists(Paths.get(PROJECT_ROOT + "\\" + reportName));

    }
}
