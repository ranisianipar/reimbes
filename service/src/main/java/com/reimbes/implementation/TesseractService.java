package com.reimbes.implementation;

import com.reimbes.Fuel;
import com.reimbes.OcrService;
import com.reimbes.Parking;
import com.reimbes.Transaction;
import com.reimbes.constant.UrlConstants;
import com.reimbes.exception.ReimsException;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;

import static com.reimbes.constant.UrlConstants.TESSERACT_TRAINNED_DATA_PATH;

@Service
public class TesseractService implements OcrService {
    private static Logger log = LoggerFactory.getLogger(TesseractService.class);

    private final ITesseract instance = new Tesseract();

    public ITesseract getInstance() {
        instance.setDatapath(TESSERACT_TRAINNED_DATA_PATH);
        return instance;
    }

    public String readImage(String imagePath) throws IOException,ReimsException {
        // absolute path
        imagePath = UrlConstants.IMAGE_FOLDER_PATH+imagePath;

        BufferedImage image = ImageIO.read(new File(StringUtils.cleanPath(imagePath)));

        String ocrResult;
        try {
            ocrResult = instance.doOCR(image);
        } catch (TesseractException t) {
            throw new ReimsException(t.getMessage(), HttpStatus.BAD_REQUEST, 500);
        }
        return ocrResult;
    }

    public Transaction predictImageContent(byte[] image) throws ReimsException{

        String ocrResult = "";
        try {
            ocrResult = getInstance().doOCR(createImageFromBytes(image)).toLowerCase();
        } catch (TesseractException t) {
            throw new ReimsException(t.getMessage(), HttpStatus.BAD_REQUEST, 500);

        }


        Transaction transaction = new Parking();
        // iterate
        int i = 0;
        String[] ocrArr = ocrResult.split("\n");
        log.info("OCR result: \n");
        for (String w: ocrArr) {
            i++;
            log.info(i+". "+w);
        }

        // dummy transaction
        transaction.setTitle(ocrArr[0]);
        transaction.setAmount(15000);
        transaction.setDate(Instant.now().getEpochSecond()*1000);
        transaction.setCategory(Transaction.Category.PARKING);
        ((Parking) transaction).setType(Parking.Type.CAR);


        return transaction;
    }

    private Transaction.Category decideCategory(String ocrResult) {

        // default
        return Transaction.Category.PARKING;
    }

    private Parking mappingToParking(String ocrResult) {
        return null;
    }

    private Fuel mappingToFuel(String ocrResult) {
        return null;
    }

    private BufferedImage createImageFromBytes(byte[] imageData) {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
        try {
            return ImageIO.read(bais);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
