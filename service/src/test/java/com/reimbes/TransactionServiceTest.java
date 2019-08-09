package com.reimbes;

import com.reimbes.exception.ReimsException;
import com.reimbes.implementation.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.gen5.api.BeforeAll;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
@ComponentScan(basePackageClasses = TransactionServiceImpl.class)
public class TransactionServiceTest {

    @Mock
    private ParkingServiceImpl parkingService;

    @Mock
    private FuelServiceImpl fuelService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private AuthServiceImpl authService;

    @Mock
    private TesseractService ocrService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private ReimsUser user = new ReimsUser();

    private Fuel fuel = new Fuel();
    private Parking parking = new Parking();

    @BeforeAll
    public void setup() {
        user.setUsername("HAHA");
        user.setPassword("HEHE");
        user.setRole(ReimsUser.Role.USER);
        user.setId(1);
    }

    @Test
    public void createByImageTest() throws Exception {
        byte[] imageByte = Base64.getDecoder().decode(("iVBORw".getBytes(StandardCharsets.UTF_8)));
        String extension = "png";
        String imageValue = "data:image/png;base64,iVBORw";
        String imagePath = user.getId()+"/123."+extension;

        fuel.setAmount(61000);
        fuel.setReimsUser(user);
        fuel.setDate(Instant.now().getEpochSecond()*1000);
        fuel.setLiters(10);
        fuel.setType(Fuel.Type.PERTALITE);
        fuel.setCategory(Transaction.Category.FUEL);
        fuel.setTitle("FUEL TEST");

//        when(userService.getUserByUsername(authService.getCurrentUsername())).thenReturn(user);
//        when(transactionService.uploadImage(imageByte, extension)).thenReturn(imagePath);
//        when(ocrService.predictImageContent(imageByte)).thenReturn(fuel);
//
//        assertEquals(fuel, transactionService.createByImage(imageValue));


    }

}
