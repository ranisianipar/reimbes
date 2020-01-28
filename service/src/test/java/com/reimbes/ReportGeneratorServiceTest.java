package com.reimbes;

import com.reimbes.implementation.*;
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
import static com.reimbes.constant.UrlConstants.PROJECT_ROOT;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
@ComponentScan(basePackageClasses = {ReportGeneratorServiceImpl.class})
public class ReportGeneratorServiceTest {

    @Mock
    private TransactionServiceImpl transactionService;

    @Mock
    private UtilsServiceImpl utilsServiceImpl;

    @Mock
    private AuthServiceImpl authService;

    @Mock
    private MedicalServiceImpl medicalService;

    @InjectMocks
    private ReportGeneratorServiceImpl reportGeneratorService;

    private ReimsUser user;
    private Set<Transaction> fuels = new HashSet<>();
    private Set<Transaction> parkings = new HashSet<>();
    private Set<Medical> medicals = new HashSet<>();

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
        ((Parking) parking).setType(Parking.Type.CAR);
        parking.setImage("0/b.jpg");
        parking.setDate(Instant.now().getEpochSecond());
        parking.setReimsUser(user);
        parking.setCategory(Transaction.Category.PARKING);
        ((Parking) parking).setHours(3);
        ((Parking) parking).setLicense("RI 1");
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
    public void whenCreateReportWithRangeOfDateAndTypeMedical_thenReturnReportContainsListOfMedicals() throws Exception{
        Long time = Instant.now().toEpochMilli();
        String reportName = String.format("%s_%s_%s_%s.xls", user.getUsername(), MEDICAL, time, time);
        String BLIBLI_LOGO_PATH = "image/blibli-logo.png";

        when(authService.getCurrentUser()).thenReturn(user);
        when(medicalService.getByDate(time, time)).thenReturn(new ArrayList<>(medicals));
        when(utilsServiceImpl.getFile(reportName)).thenReturn(new byte[19]);
        when(utilsServiceImpl.getFile(BLIBLI_LOGO_PATH))
                .thenReturn(Files.readAllBytes(Paths.get("C:\\Users\\Z\\Documents\\code\\haha\\reimbes\\" + BLIBLI_LOGO_PATH)));

        reportGeneratorService.getReport(user, time, time, MEDICAL);

        verify(medicalService, times(1)).getByDate(time, time);


        Files.deleteIfExists(Paths.get(PROJECT_ROOT + "\\" + reportName));

    }


    @Test
    public void whenCreateReportWithoutRangeOfDateAndWithTypeMedical_thenReturnReportContainsListOfMedicals() throws Exception{
        String reportName = String.format("%s_%s_%s.xls", user.getUsername(), MEDICAL, "ALL");
        String BLIBLI_LOGO_PATH = "image/blibli-logo.png";

        when(authService.getCurrentUser()).thenReturn(user);
        when(medicalService.getByDate(INFINITE_DATE_RANGE, INFINITE_DATE_RANGE)).thenReturn(new ArrayList<>(medicals));
        when(utilsServiceImpl.getFile(reportName)).thenReturn(new byte[19]);
        when(utilsServiceImpl.getFile(BLIBLI_LOGO_PATH))
                .thenReturn(Files.readAllBytes(Paths.get("C:\\Users\\Z\\Documents\\code\\haha\\reimbes\\" + BLIBLI_LOGO_PATH)));

        reportGeneratorService.getReport(user, INFINITE_DATE_RANGE, INFINITE_DATE_RANGE, MEDICAL);

        verify(medicalService, times(1)).getByDate(INFINITE_DATE_RANGE, INFINITE_DATE_RANGE);

        Files.deleteIfExists(Paths.get(PROJECT_ROOT + "\\" + reportName));

    }

    @Test
    public void whenCreateReportWithoutRangeOfDateAndWithTypeFuel_thenReturnReportOfAllTransactions() throws Exception{
        String reportName = String.format("%s_%s_%s.xls", user.getUsername(), FUEL, "ALL");
        String BLIBLI_LOGO_PATH = "image/blibli-logo.png";

        when(authService.getCurrentUser()).thenReturn(user);
        when(transactionService.getByDateAndType(INFINITE_DATE_RANGE, INFINITE_DATE_RANGE, Transaction.Category.FUEL)).thenReturn(new ArrayList<>(fuels));
        when(utilsServiceImpl.getFile(reportName)).thenReturn(new byte[19]);
        when(utilsServiceImpl.getFile(BLIBLI_LOGO_PATH))
                .thenReturn(Files.readAllBytes(Paths.get("C:\\Users\\Z\\Documents\\code\\haha\\reimbes\\" + BLIBLI_LOGO_PATH)));

        reportGeneratorService.getReport(user, INFINITE_DATE_RANGE, INFINITE_DATE_RANGE, FUEL);

        verify(transactionService, times(1)).getByDateAndType(INFINITE_DATE_RANGE, INFINITE_DATE_RANGE, Transaction.Category.FUEL);

        Files.deleteIfExists(Paths.get(PROJECT_ROOT + "\\" + reportName));

    }

    @Test
    public void whenCreateReportWithoutRangeOfDateAndWithTypeParking_thenReturnReportOfAllTransactions() throws Exception{
        String reportName = String.format("%s_%s_%s.xls", user.getUsername(), PARKING, "ALL");
        String BLIBLI_LOGO_PATH = "image/blibli-logo.png";

        when(authService.getCurrentUser()).thenReturn(user);
        when(transactionService.getByDateAndType(INFINITE_DATE_RANGE, INFINITE_DATE_RANGE, Transaction.Category.PARKING)).thenReturn(new ArrayList<>(parkings));
        when(utilsServiceImpl.getFile(reportName)).thenReturn(new byte[19]);
        when(utilsServiceImpl.getFile(BLIBLI_LOGO_PATH))
                .thenReturn(Files.readAllBytes(Paths.get("C:\\Users\\Z\\Documents\\code\\haha\\reimbes\\" + BLIBLI_LOGO_PATH)));

        reportGeneratorService.getReport(user, INFINITE_DATE_RANGE, INFINITE_DATE_RANGE, PARKING);

        verify(transactionService, times(1)).getByDateAndType(INFINITE_DATE_RANGE, INFINITE_DATE_RANGE, Transaction.Category.PARKING);

        Files.deleteIfExists(Paths.get(PROJECT_ROOT + "\\" + reportName));

    }
}
