package com.reimbes;

import com.reimbes.implementation.FuelServiceImpl;
import com.reimbes.request.TransactionRequest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

import static com.reimbes.Transaction.Category.FUEL;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
@ComponentScan(basePackageClasses = FuelServiceImpl.class)
public class FuelServiceTest {

    @InjectMocks
    private FuelServiceImpl fuelService;

    public TransactionRequest request = new TransactionRequest();

    @Before
    public void setup() {
        request.setFuelType(Fuel.Type.SOLAR);
        request.setLiters(new Float(10.9));
    }

    @Test
    public void createFuelTest() {
        Fuel fuel = fuelService.create(request);

        assertEquals(FUEL, fuel.getCategory());
        assertEquals(request.getFuelType(), fuel.getType());
        assertEquals(((int) request.getLiters()), (int) fuel.getLiters());

    }
}