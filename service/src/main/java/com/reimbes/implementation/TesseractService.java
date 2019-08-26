package com.reimbes.implementation;


import com.reimbes.OcrService;
import com.reimbes.Transaction;
import com.reimbes.constant.UrlConstants;
import com.reimbes.exception.ReimsException;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Random;

import static com.reimbes.constant.UrlConstants.TESSERACT_TRAINNED_DATA_PATH;
import static com.sun.xml.internal.bind.v2.util.EditDistance.editDistance;

@Service
public class TesseractService implements OcrService {
    private static Logger log = LoggerFactory.getLogger(TesseractService.class);

    private String fuelKeyWords = "stationspburpvolumelitertotal";
    private String parkingKeyWords = "inoutlamaparkirrp";

    @Autowired
    private ParkingServiceImpl parkingService;

    @Autowired
    private FuelServiceImpl fuelService;

    private final ITesseract instance = new Tesseract();

    public ITesseract getInstance() {
        instance.setDatapath(TESSERACT_TRAINNED_DATA_PATH);
        return instance;
    }

    public String readImage(String imagePath) throws IOException, ReimsException {
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

    public Transaction predictImageContent(byte[] image) throws ReimsException {

        String ocrResult = "";
        try {
            ocrResult = getInstance().doOCR(createImageFromBytes(image));
        } catch (TesseractException t) {
            throw new ReimsException(t.getMessage(), HttpStatus.BAD_REQUEST, 500);
        }

        String[] ocrArr = ocrResult.toUpperCase().split("\n");
        log.info("OCR result splitting!");

        Transaction transaction;

        int parking_score = editDistance(ocrResult, parkingKeyWords);
        int fuel_score = editDistance(ocrResult, fuelKeyWords);

        log.info("Parking score: "+parking_score+" Fuel score: "+fuel_score);

        if (Math.abs(parking_score - fuel_score) < 3) {
            log.info("Get a random numbre to enlarge the distance.");

            Random random = new Random();
            fuel_score = fuel_score + random.nextInt();
            parking_score = parking_score + random.nextInt();
            log.info("Parking Score: "+parking_score+" Fuel Score: "+fuel_score);
        }
        if (parking_score > fuel_score) {
            log.info("Mapping to parking!");
            transaction = parkingService.map(ocrArr);
        } else {
            log.info("Mapping to fuel!");
            transaction = fuelService.map(ocrArr);
        }

        transaction.setTitle(ocrArr[0]);
        transaction.setDate(Instant.now().toEpochMilli());

        return transaction;
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
