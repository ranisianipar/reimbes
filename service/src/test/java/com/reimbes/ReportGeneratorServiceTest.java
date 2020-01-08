package com.reimbes;

import com.reimbes.*;
import com.reimbes.implementation.ReportGeneratorServiceImpl;
import com.reimbes.implementation.TransactionServiceImpl;
import com.reimbes.implementation.Utils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static com.reimbes.ReimsUser.Role.USER;
import static com.reimbes.constant.General.INFINITE_DATE_RANGE;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
@ComponentScan(basePackageClasses = {ReportGeneratorServiceImpl.class})
public class ReportGeneratorServiceTest {

    @Mock
    private TransactionServiceImpl transactionService;

    @Mock
    private Utils utils;

    @InjectMocks
    private ReportGeneratorServiceImpl reportGeneratorService;

    private ReimsUser user;
    private Set<Transaction> transactions = new HashSet<>();

    private long epochNow = Instant.now().getEpochSecond();

    @Before
    public void setup() {
        user = ReimsUser.ReimsUserBuilder()
                .username("HAHA")
                .password("xoxo")
                .role(USER)
                .id(0)
                .build();

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
        ((Parking) parking).setLocation("Thamrin");

        transactions.add(fuel);
        transactions.add(fuel);
        transactions.add(parking);
        transactions.add(parking);

        user.setTransactions(transactions);
    }


    @Test
    public void whenCreateReportWithoutRangeOfDate_thenReturnReportOfAllTransactions() throws Exception{
        when(transactionService.getByUser(user)).thenReturn(new ArrayList<>(transactions));
        when(utils.getFile(user.getUsername()+"_ALL")).thenReturn(new byte[19]);

        reportGeneratorService.getReport(user, INFINITE_DATE_RANGE, INFINITE_DATE_RANGE, null);

        verify(transactionService, times(1)).getByUser(user);

    }

    @Test
    public void whenCreateReport_thenGenerateAndWriteReport() throws Exception {
//        when(transactionService.getByUserAndDate(medicalUser, epochNow, epochNow)).thenReturn(new ArrayList<>(transactions));
//        reportGeneratorService.getReport(medicalUser, epochNow, epochNow);
//
//        verify(Files.readAllBytes(Paths.getByUser(String.format("%s_%s_%s", medicalUser.getUsername(), epochNow, epochNow))),
//                times(1));
    }
}
