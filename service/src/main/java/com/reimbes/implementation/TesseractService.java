package com.reimbes.implementation;

import com.reimbes.OcrService;
import net.sourceforge.tess4j.Tesseract;

public class TesseractService implements OcrService {
    Tesseract instance = new Tesseract();

    public Tesseract getInstance() {
        instance.setLanguage("en");
        return instance;
    }
}
