package com.reimbes.implementation;

import com.reimbes.*;
import com.reimbes.exception.ReimsException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static com.reimbes.constant.General.*;

@Service
public class ReportGeneratorServiceImpl implements ReportGeneratorService {

    private static Logger log = LoggerFactory.getLogger(ReportGeneratorServiceImpl.class);

    @Autowired
    private TransactionServiceImpl transactionService;

    @Autowired
    private MedicalServiceImpl medicalService;

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private Utils utils;

    @Override
    public byte[] getReport(ReimsUser user, Long start, Long end, String reimbursementType) throws Exception {

        String filename;


        filename = String.format("%s_%s_%s.xls", user.getUsername(), reimbursementType, "ALL");
        if (start != 0 && end != 0){
            filename = String.format("%s_%s_%s_%s.xls", user.getUsername(), reimbursementType, start+"", end+"");

        }

        /* INIT */
        Workbook wb = new HSSFWorkbook();
        DATE_FORMAT.setTimeZone(TIME_ZONE);
        OutputStream out;
        out = new FileOutputStream(filename);

        Sheet reimbursement = wb.createSheet(String.format("%s report", reimbursementType));


        // styling header
        Font headerFont = stylingHeader(wb.createFont());

        CellStyle headerCellStyle = wb.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row reportRow = reimbursement.createRow(0);
        reportRow.setRowStyle(headerCellStyle);

        writeHeaderCell(reimbursement, reportRow);

        // determine report type
        switch (reimbursementType) {
            case PARKING:
                writeParkingReport(reimbursement, reportRow, start, end);
            case FUEL:
                writeFuelReport(reimbursement, reportRow, start, end);
            case MEDICAL:
                writeMedicalReport(reimbursement, reportRow, start, end);
        }

        wb.write(out);
        out.close();

        return utils.getFile(filename);
    }

    private Cell createCell(Row row, int index, String value) {
        Cell cell = row.createCell(index);
        cell.setCellValue(value);

        return cell;
    }

    private Cell createCell(Row row, int index, long value) {
        Cell cell = row.createCell(index);
        cell.setCellValue(value);

        return cell;
    }

    private Cell createCell(Row row, int index, float value) {
        Cell cell = row.createCell(index);
        cell.setCellValue(value);

        return cell;
    }

    private Font stylingHeader(Font headerFont) {
        headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        headerFont.setColor(IndexedColors.LIGHT_GREEN.getIndex());
        headerFont.setFontName("Arial");
        return headerFont;
    }

    // general attribute
    private void writeHeaderCell(Sheet sheet, Row row) {
        int currentIndex = row.getRowNum();
        row = sheet.createRow(++currentIndex);

        log.info(String.format("Write Header Cell in row index %d", row.getRowNum()));
        createCell(row, 0, "No.");
        createCell(row, 1, "Title");
        createCell(row, 2, "Date");
        createCell(row, 3, "Amount");
    }

    private void writeParkingReport(Sheet sheet, Row row, Long start, Long end) throws ReimsException {
        createCell(row, 4, "Hours");

        int totalAmount = 0;
        int sheetIndex = row.getRowNum();

        List<Transaction> parkings = transactionService.getByDateAndType(start, end, Transaction.Category.PARKING);

        /*LOOP*/
        for (Transaction transaction : parkings) {
            Parking parking = (Parking) transaction;
            row = sheet.createRow(++sheetIndex);
            createCell(row, 0, sheetIndex++);
            createCell(row, 1, parking.getTitle());
            createCell(row, 2, DATE_FORMAT.format(new Date(parking.getDate())));
            createCell(row, 3, parking.getAmount());
            createCell(row, 4, parking.getHours());
            totalAmount += parking.getAmount();
        }

        createCell(row, 0, "TOTAL:");
        createCell(row, 3, totalAmount);
    }

    private void writeFuelReport(Sheet sheet, Row row, Long start, Long end) throws ReimsException {
        createCell(row, 4, "Liters");

        int totalAmount = 0;
        int sheetIndex = row.getRowNum();

        List<Transaction> fuels = transactionService.getByDateAndType(start, end, Transaction.Category.FUEL);

        /*LOOP*/
        for (Transaction transaction : fuels) {
            Fuel fuel = (Fuel) transaction;
            row = sheet.createRow(++sheetIndex);
            createCell(row, 0, sheetIndex++);
            createCell(row, 1, fuel.getTitle());
            createCell(row, 2, DATE_FORMAT.format(new Date(fuel.getDate())));
            createCell(row, 3, fuel.getAmount());
            createCell(row, 4, fuel.getLiters());
            totalAmount += fuel.getAmount();
        }

        createCell(row, 0, "TOTAL:");
        createCell(row, 3, totalAmount);
    }

    private void writeMedicalReport(Sheet sheet, Row row, Long start, Long end) throws ReimsException {
        createCell(row, 4, "Patient");

        int totalAmount = 0;
        int currentIndex = row.getRowNum();
        int sheetIndex = 1 ;


        List<Medical> medicals = medicalService.getByDate(start, end);

        /*LOOP*/
        for (Medical medical : medicals) {
            row = sheet.createRow(++currentIndex);
            createCell(row, 0, sheetIndex++);
            createCell(row, 1, medical.getTitle());
            createCell(row, 2, DATE_FORMAT.format(new Date(medical.getDate())));
            createCell(row, 3, medical.getAmount());
            createCell(row, 4, medical.getPatient().getName());
            totalAmount += medical.getAmount();
        }

        row = sheet.createRow(++currentIndex);
        createCell(row, 0, "TOTAL:");
        createCell(row, 3, totalAmount);
    }

}
