package com.reimbes.implementation;

import com.reimbes.OcrService;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Service
public class TesseractService implements OcrService {
    private final Tesseract instance = new Tesseract();

    public Tesseract getInstance() {
        instance.setLanguage("en");
        return instance;
    }

    public String readImage(String imagePath) throws IOException {
        BufferedImage image = ImageIO.read(new File(StringUtils.cleanPath(imagePath)));

//        instance.doOCR();
        return "";
    }
}
