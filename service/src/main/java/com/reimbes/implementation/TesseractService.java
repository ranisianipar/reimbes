package com.reimbes.implementation;

import com.reimbes.OcrService;
import com.reimbes.constant.UrlConstants;
import com.reimbes.exception.ReimsException;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.http.HttpStatus;
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

    public String readImage(String imagePath) throws IOException,ReimsException {
        // absolute path
        imagePath = UrlConstants.IMAGE_FOLDER_PATH+imagePath;

        BufferedImage image = ImageIO.read(new File(StringUtils.cleanPath(imagePath)));
        preprocessing(image);

        String ocrResult;
        try {
            ocrResult = instance.doOCR(image);
        } catch (TesseractException t) {
            throw new ReimsException(t.getMessage(), HttpStatus.BAD_REQUEST, 500);
        }
        return ocrResult;
    }

    public BufferedImage preprocessing(BufferedImage img) {
        nu.pattern.OpenCV.loadShared();
        return img;
    }
}
