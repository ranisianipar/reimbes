package com.reimbes.implementation;

import com.reimbes.*;
import com.reimbes.exception.ReimsException;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Date;
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

    // Header sizes
    private final short SUPER_LARGE_TEXT = (short)24;
    private final short LARGE_TEXT = (short)18;
    private final short DEFAULT_TEXT_SIZE = (short)12;

    // Font object
    private Font fontDefault;
    private Font fontDefaultStrong;
    private Font fontHeader1;
    private Font fontHeader2;

    // Cell style
    @Setter @Getter
    private CellStyle tableHeaderCellStyle;
    @Setter @Getter
    private CellStyle defaultCellStyle;
    private CellStyle totalCellStyle;

    @Setter @Getter
    private int currentRowIndex;

    @Override
    public byte[] getReport(ReimsUser user, Long start, Long end, String reimbursementType) throws Exception {

        String filename;

        filename = String.format("%s_%s_%s.xls", user.getUsername(), reimbursementType, "ALL");
        if (start != 0 && end != 0){
            filename = String.format("%s_%s_%s_%s.xls", user.getUsername(), reimbursementType, start+"", end+"");

        }

        /* INIT */
        //create a new workbook
        Workbook wb = new HSSFWorkbook();
        DATE_FORMAT.setTimeZone(TIME_ZONE);

        Sheet sheet = wb.createSheet(String.format("%s report", reimbursementType));

        // init
        setCurrentRowIndex(0);
        initFont(wb);
        initCellStyle(wb);
        initImage(wb, sheet); // end in 5th row, how can get image position dynamically?
        initHeader(wb, sheet);
        initPersonalInfo(wb, sheet);

        // set column width
        sheet.autoSizeColumn(0); // autosize first column, depends on maximum width of applied cells
        sheet.setColumnWidth(1, 20 * 256); // autosize first column, depends on maximum width of applied cells
        sheet.setColumnWidth(2, 12 * 256); // autosize first column, depends on maximum width of applied cells
        sheet.setColumnWidth(3, 12 * 256); // autosize first column, depends on maximum width of applied cells
        sheet.setColumnWidth(4, 15 * 256); // autosize first column, depends on maximum width of applied cells

        // assign user report value
        writeHeaderCell(sheet); // init header cell

        // determine report type
        switch (reimbursementType) {
            case PARKING:
                writeParkingReport(sheet, start, end);
            case FUEL:
                writeFuelReport(sheet, start, end);
            case MEDICAL:
                writeMedicalReport(sheet, start, end);
        }

        if(wb instanceof XSSFWorkbook) filename += "x";

        //save workbook
        OutputStream fileOut = new FileOutputStream(filename);
        wb.write(fileOut);

        fileOut.close();
        return utils.getFile(filename);

    }

    private Cell createCell(Row row, int index, String value) {
        Cell cell = row.createCell(index);
        cell.setCellValue(value);
        cell.setCellStyle(defaultCellStyle);
        return cell;
    }

    private Cell createCell(Row row, int index, long value) {
        Cell cell = row.createCell(index);
        cell.setCellValue(value);
        cell.setCellStyle(defaultCellStyle);
        return cell;
    }

    private Cell createCell(Row row, int index, float value) {
        Cell cell = row.createCell(index);
        cell.setCellValue(value);
        cell.setCellStyle(defaultCellStyle);
        return cell;
    }

    // general attribute
    private void writeHeaderCell(Sheet sheet) {
        Row row = sheet.createRow(getCurrentRowIndex());
        setCurrentRowIndex(getCurrentRowIndex() + 1);
        log.info(String.format("Write Header Cell in row index %d", row.getRowNum()));

        createCell(row, 0, "No.").setCellStyle(getTableHeaderCellStyle());
        createCell(row, 1, "Title").setCellStyle(getTableHeaderCellStyle());
        createCell(row, 2, "Date").setCellStyle(getTableHeaderCellStyle());
        createCell(row, 3, "Amount").setCellStyle(getTableHeaderCellStyle());

    }

    private void initCellStyle(Workbook wb) {
        tableHeaderCellStyle = wb.createCellStyle();
        tableHeaderCellStyle.setFont(fontDefaultStrong);
        tableHeaderCellStyle.setAlignment(CellStyle.ALIGN_CENTER);

        defaultCellStyle = wb.createCellStyle();
        defaultCellStyle.setFont(fontDefault);
        defaultCellStyle.setAlignment(CellStyle.ALIGN_LEFT);

        totalCellStyle = wb.createCellStyle();
        totalCellStyle.setFont(fontDefaultStrong);
        totalCellStyle.setAlignment(CellStyle.ALIGN_LEFT);

    }

    private void writeParkingReport(Sheet sheet, Long start, Long end) throws ReimsException {
        Row row = sheet.createRow(getCurrentRowIndex());
        setCurrentRowIndex(getCurrentRowIndex() + 1);
        createCell(row, 4, "Hours");

        int totalAmount = 0;
        int transactionIndex = 0;

        List<Transaction> parkings = transactionService.getByDateAndType(start, end, Transaction.Category.PARKING);

        /*LOOP*/
        for (Transaction transaction : parkings) {
            Parking parking = (Parking) transaction;
            ++transactionIndex;
            row = sheet.createRow(getCurrentRowIndex());
            setCurrentRowIndex(getCurrentRowIndex() + 1);
            createCell(row, 0, transactionIndex);
            createCell(row, 1, parking.getTitle());
            createCell(row, 2, DATE_FORMAT.format(new Date(parking.getDate())));
            createCell(row, 3, parking.getAmount());
            createCell(row, 4, parking.getHours());
            totalAmount += parking.getAmount();
        }

        // footer row
        row = sheet.createRow(getCurrentRowIndex() + 1);
        setCurrentRowIndex(getCurrentRowIndex() + 2);

        createCell(row, 1, "TOTAL:").setCellStyle(totalCellStyle);

        sheet.addMergedRegion(new CellRangeAddress(
                row.getRowNum(), //first row of header (0-based)
                row.getRowNum(), //last row of header (0-based)
                1, //first column of header (0-based)
                2  //last column of header (0-based)
        ));

        createCell(row, 3, totalAmount);

        setCurrentRowIndex(getCurrentRowIndex() + 1);
    }

    private void writeFuelReport(Sheet sheet, Long start, Long end) throws ReimsException {
        Row row = sheet.createRow(getCurrentRowIndex());
        setCurrentRowIndex(getCurrentRowIndex() + 1);

        createCell(row, 4, "Liters");

        int totalAmount = 0;
        int transactionIndex = 0;

        List<Transaction> fuels = transactionService.getByDateAndType(start, end, Transaction.Category.FUEL);

        /*LOOP*/
        for (Transaction transaction : fuels) {
            Fuel fuel = (Fuel) transaction;
            ++transactionIndex;
            row = sheet.createRow(getCurrentRowIndex());
            setCurrentRowIndex(getCurrentRowIndex() + 1);
            createCell(row, 0, transactionIndex);
            createCell(row, 1, fuel.getTitle());
            createCell(row, 2, DATE_FORMAT.format(new Date(fuel.getDate())));
            createCell(row, 3, fuel.getAmount());
            createCell(row, 4, fuel.getLiters());
            totalAmount += fuel.getAmount();
        }

        // footer row
        row = sheet.createRow(getCurrentRowIndex() + 1);
        setCurrentRowIndex(getCurrentRowIndex() + 2);

        createCell(row, 1, "TOTAL:").setCellStyle(totalCellStyle);

        sheet.addMergedRegion(new CellRangeAddress(
                row.getRowNum(), //first row of header (0-based)
                row.getRowNum(), //last row of header (0-based)
                1, //first column of header (0-based)
                2  //last column of header (0-based)
        ));

        createCell(row, 3, totalAmount);

        setCurrentRowIndex(getCurrentRowIndex() + 1);
    }

    private void writeMedicalReport(Sheet sheet, Long start, Long end) throws ReimsException {
        Row row = sheet.getRow(getCurrentRowIndex() - 1); // write specific attribute based on report type
        createCell(row, 4, "Patient").setCellStyle(getTableHeaderCellStyle());

        int totalAmount = 0;
        int medicalIndex = 0 ;

        List<Medical> medicals = medicalService.getByDate(start, end);

        /*LOOP*/
        for (Medical medical : medicals) {
            ++medicalIndex;
            row = sheet.createRow(getCurrentRowIndex());
            setCurrentRowIndex(getCurrentRowIndex() + 1);
            createCell(row, 0, medicalIndex);
            createCell(row, 1, medical.getTitle());
            createCell(row, 2, DATE_FORMAT.format(new Date(medical.getDate())));
            createCell(row, 3, medical.getAmount());
            createCell(row, 4, medical.getPatient().getName());
            totalAmount += medical.getAmount();
        }

        // footer row
        row = sheet.createRow(getCurrentRowIndex() + 1);
        setCurrentRowIndex(getCurrentRowIndex() + 2);

        createCell(row, 1, "TOTAL:").setCellStyle(totalCellStyle);

        sheet.addMergedRegion(new CellRangeAddress(
                row.getRowNum(), //first row of header (0-based)
                row.getRowNum(), //last row of header (0-based)
                1, //first column of header (0-based)
                2  //last column of header (0-based)
        ));

        createCell(row, 3, totalAmount);

        setCurrentRowIndex(getCurrentRowIndex() + 1);
    }

    // space: 5 rows
    private void initImage(Workbook wb, Sheet sheet) throws IOException {
        // add picture data to this workbook.
        int pictureIdx = wb.addPicture(utils.getFile("image/blibli-logo.png"), Workbook.PICTURE_TYPE_PNG);
        CreationHelper helper = wb.getCreationHelper();

        // Create the drawing patriarch.  This is the top level container for all shapes.
        Drawing drawing = sheet.createDrawingPatriarch();

        // add a picture shape
        ClientAnchor anchor = helper.createClientAnchor();

        // set top-left corner of the picture,
        // subsequent call of Picture#resize() will operate relative to it
        anchor.setCol1(1);
        anchor.setRow1(1);
        Picture pict = drawing.createPicture(anchor, pictureIdx);

        //auto-size picture relative to its top-left corner
        pict.resize(0.25);

        setCurrentRowIndex(getCurrentRowIndex() + 5);
    }

    // need to be refactored!
    // (row) start: 5; end: 6;
    private void initHeader(Workbook wb, Sheet sheet){
        int startColumnIndex = 0;
        int endColumnIndex = 5;

        // prepare cell style
        CellStyle headerStyle1 = wb.createCellStyle();
        headerStyle1.setFont(fontHeader1);
        headerStyle1.setAlignment(CellStyle.ALIGN_CENTER);

        CellStyle headerStyle2 = wb.createCellStyle();
        headerStyle2.setFont(fontHeader2);
        headerStyle2.setAlignment(CellStyle.ALIGN_CENTER);


        // apply style and text in header cell
        Row rowHeader1 = sheet.createRow(getCurrentRowIndex());
        setCurrentRowIndex(getCurrentRowIndex() + 1);

        Cell cellHeader1 = rowHeader1.createCell(0);
        cellHeader1.setCellValue("REKAPITULASI BIAYA");
        cellHeader1.setCellStyle(headerStyle1);


        Row rowHeader2 = sheet.createRow(getCurrentRowIndex());
        setCurrentRowIndex(getCurrentRowIndex() + 1);

        Cell cell2 = rowHeader2.createCell(0);
        cell2.setCellValue("REKAP BIAYA [PARKIR]");
        cell2.setCellStyle(headerStyle2);

        // merge cells
        // 1-st row header
        sheet.addMergedRegion(new CellRangeAddress(
                rowHeader1.getRowNum(), //first row of header (0-based)
                rowHeader1.getRowNum(), //last row of header (0-based)
                startColumnIndex, //first column of header (0-based)
                endColumnIndex  //last column of header (0-based)
        ));
        // 2-nd row header
        sheet.addMergedRegion(new CellRangeAddress(
                rowHeader2.getRowNum(), //first row of header (0-based)
                rowHeader2.getRowNum(), //last row of header (0-based)
                startColumnIndex, //first column of header (0-based)
                endColumnIndex  //last column of header (0-based)
        ));
    }

    private void initPersonalInfo(Workbook wb, Sheet sheet) throws ReimsException {
        ReimsUser user = authService.getCurrentUser();

        // prepare cell style
        CellStyle personalInfoStyle = wb.createCellStyle();
        personalInfoStyle.setFont(fontDefaultStrong);

        // apply style and text in header cell
        Row personalInfoRow = sheet.createRow(getCurrentRowIndex());
        setCurrentRowIndex(getCurrentRowIndex() + 1);
        createCell(personalInfoRow, 0, "Nama").setCellStyle(personalInfoStyle);
        createCell(personalInfoRow, 1, ":").setCellStyle(personalInfoStyle);
        createCell(personalInfoRow, 2, user.getUsername());

        personalInfoRow = sheet.createRow(getCurrentRowIndex());
        setCurrentRowIndex(getCurrentRowIndex() + 1);
        createCell(personalInfoRow, 0, "Divisi").setCellStyle(personalInfoStyle);
        createCell(personalInfoRow, 1, ":").setCellStyle(personalInfoStyle);
        createCell(personalInfoRow, 2, user.getDivision());

        personalInfoRow = sheet.createRow(getCurrentRowIndex());
        setCurrentRowIndex(getCurrentRowIndex() + 1);
        createCell(personalInfoRow, 0, "No. Polisi").setCellStyle(personalInfoStyle);
        createCell(personalInfoRow, 1, ":").setCellStyle(personalInfoStyle);
        createCell(personalInfoRow, 2, user.getLicense());

        personalInfoRow = sheet.createRow(getCurrentRowIndex());
        setCurrentRowIndex(getCurrentRowIndex() + 1);
        createCell(personalInfoRow, 0, "Kendaraan").setCellStyle(personalInfoStyle);
        createCell(personalInfoRow, 1, ":").setCellStyle(personalInfoStyle);
        createCell(personalInfoRow, 2, user.getVehicle());

        setCurrentRowIndex(getCurrentRowIndex() + 2);
    }

    private void initFont(Workbook wb) {
        // Create a new font and alter it.
        fontDefault = wb.createFont();
        fontDefault.setFontName("Courier New");
        fontDefault.setFontHeightInPoints(DEFAULT_TEXT_SIZE);

        fontDefaultStrong = wb.createFont();
        fontDefaultStrong.setFontName(fontDefault.getFontName());
        fontDefaultStrong.setFontHeightInPoints(fontDefault.getFontHeightInPoints());
        fontDefaultStrong.setBoldweight(Font.BOLDWEIGHT_BOLD);

        fontHeader1 = wb.createFont();
        fontHeader1.setFontName(fontDefault.getFontName());
        fontHeader1.setFontHeightInPoints(SUPER_LARGE_TEXT);
        fontHeader1.setBoldweight(Font.BOLDWEIGHT_BOLD);

        fontHeader2 = wb.createFont();
        fontHeader2.setFontName(fontDefault.getFontName());
        fontHeader2.setFontHeightInPoints(LARGE_TEXT);
        fontHeader2.setBoldweight(Font.BOLDWEIGHT_BOLD);

    }

}
