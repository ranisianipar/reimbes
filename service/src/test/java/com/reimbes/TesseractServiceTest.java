package com.reimbes;

import com.reimbes.implementation.TesseractService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
@ComponentScan(basePackageClasses = TesseractService.class)
public class TesseractServiceTest {

    @InjectMocks
    private TesseractService tesseractService;

    @Test
    public void getTesseractTest() {
        assertNotNull(tesseractService.getInstance());
    }
}
