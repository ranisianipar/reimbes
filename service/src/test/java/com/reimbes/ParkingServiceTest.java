package com.reimbes;

import com.reimbes.implementation.ParkingServiceImpl;
import com.reimbes.request.TransactionRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
@ComponentScan(basePackageClasses = ParkingServiceImpl.class)
public class ParkingServiceTest {

    private TransactionRequest request = new TransactionRequest();

    @InjectMocks
    private ParkingServiceImpl parkingService;

    @Before
    public void setup() {
        request.setCategory(Transaction.Category.PARKING);
        request.setHours(3);
        request.setParkingType(Parking.Type.MOTORCYCLE);
        request.setLicense("B XXXX YY");
        request.setLocation("Grha Niaga Thamrin");
    }

    @Test
    public void createParkingTest() {
        Parking parking = parkingService.create(request);

        assertEquals(request.getCategory(), parking.getCategory());
        assertEquals(request.getParkingType(), parking.getType());
        assertEquals(request.getHours(), parking.getHours());
        assertEquals(request.getLicense(), parking.getLicense());
        assertEquals(request.getLocation(), parking.getLocation());
    }
}
