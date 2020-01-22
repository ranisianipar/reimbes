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

import static com.reimbes.Transaction.Category.FUEL;
import static com.reimbes.Transaction.Category.PARKING;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
@ComponentScan(basePackageClasses = ParkingServiceImpl.class)
public class ParkingServiceTest {

    @InjectMocks
    private ParkingServiceImpl parkingService;

    public TransactionRequest request = new TransactionRequest();

    @Before
    public void setup() {
        request.setParkingType(Parking.Type.CAR);
        request.setHours(4);
        request.setLocation("Sudirman");
    }

    @Test
    public void createParkingTest() {
        Parking parking = parkingService.create(request);

        assertEquals(PARKING, parking.getCategory());
        assertEquals(request.getParkingType(), parking.getType());
        assertEquals(request.getHours(), parking.getHours());
        assertEquals(request.getLocation(), parking.getLocation());
    }
}
