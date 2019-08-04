import com.reimbes.*;
import com.reimbes.implementation.ReportGeneratorServiceImpl;
import com.reimbes.implementation.TransactionServiceImpl;
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

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
@ComponentScan(basePackageClasses = {ReportGeneratorServiceImpl.class})
public class ReportGeneratorServiceTest {

    @Mock
    private TransactionServiceImpl transactionService;

    @InjectMocks
    private ReportGeneratorServiceImpl reportGeneratorService;

    private ReimsUser user = new ReimsUser();
    private Set<Transaction> transactions = new HashSet<>();

    private long epochNow = Instant.now().getEpochSecond();

    @Before
    public void setup() {
        user.setRole(ReimsUser.Role.USER);
        user.setUsername("HAHA");
        user.setPassword("xoxo");
        user.setId(0);

        Transaction fuel = new Fuel();
        fuel.setTitle("BENSIN");
        fuel.setAmount(0);
        ((Fuel) fuel).setLiters(0);
        fuel.setImage("0/a.jpg");
        fuel.setDate(Instant.now().getEpochSecond());
        fuel.setReimsUser(user);
        ((Fuel) fuel).setType(Fuel.Type.PERTALITE);
        fuel.setCategory(Transaction.Category.FUEL);


        Transaction parking = new Parking();
        parking.setTitle("PARKIR");
        parking.setAmount(0);
        ((Parking) parking).setType(Parking.Type.CAR);
        parking.setImage("0/b.jpg");
        parking.setDate(Instant.now().getEpochSecond());
        parking.setReimsUser(user);
        parking.setCategory(Transaction.Category.PARKING);
        ((Parking) parking).setHours(0);
        ((Parking) parking).setLicense("RI 1");
        ((Parking) parking).setLocation("Thamrin");

        transactions.add(fuel);
        transactions.add(parking);

        user.setTransactions(transactions);
    }


    @Test
    public void whenCreateReportWithoutRangeOfDate_thenReturnReportOfAllTransactions() throws Exception{
        when(transactionService.getByUser(user)).thenReturn(new ArrayList<>(transactions));

        reportGeneratorService.getReport(user, 0, 0);

        verify(transactionService, times(1)).getByUser(user);

    }

    @Test
    public void whenCreateReportWithRangeOfDate_thenReturnReportOfTheTransactions() throws Exception{
        when(transactionService.getByUserAndDate(user, epochNow, epochNow)).thenReturn(new ArrayList<>(transactions));

        reportGeneratorService.getReport(user, epochNow, epochNow);

        verify(transactionService, times(1)).getByUserAndDate(user, epochNow, epochNow);

    }

    @Test
    public void whenCreateReport_thenGenerateAndWriteReport() throws Exception {
        when(transactionService.getByUserAndDate(user, epochNow, epochNow)).thenReturn(new ArrayList<>(transactions));
        reportGeneratorService.getReport(user, epochNow, epochNow);

        verify(Files.readAllBytes(Paths.get(String.format("%s_%s_%s", user.getUsername(), epochNow, epochNow))),
                times(1));
    }
}
