package com.reimbes;

import com.reimbes.implementation.TesseractService;
import net.sourceforge.tess4j.ITesseract;
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
    TesseractService tesseractService;

    @Test
    public void returnTesseractWithNotNullDatapath() {
        ITesseract t = tesseractService.getInstance();
        assertNotNull(t);
    }

}
