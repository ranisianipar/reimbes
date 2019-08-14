package com.reimbes;

import com.reimbes.implementation.FuelServiceImpl;
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
@ComponentScan(basePackageClasses = FuelServiceImpl.class)
public class FuelServiceTest {

    private TransactionRequest request = new TransactionRequest();

    @InjectMocks
    private FuelServiceImpl fuelService;


    @Before
    public void setup() {
        request.setLiters(new Float(3.14));
        request.setFuelType(Fuel.Type.SOLAR);
        request.setCategory(Transaction.Category.FUEL);
    }

    @Test
    public void createFuelTest() {
        Fuel fuel = fuelService.create(request);
        assertEquals(request.getCategory(), fuel.getCategory());
        assertEquals(request.getFuelType(), fuel.getType());

    }
}
