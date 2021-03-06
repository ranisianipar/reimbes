package com.reimbes.implementation;

import com.reimbes.*;
import com.reimbes.exception.ReimsException;
import com.reimbes.interfaces.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import static com.reimbes.constant.General.*;
import static com.reimbes.constant.UrlConstants.GDN_LOGO_PATH;

@Service
public class ReportGeneratorServiceImpl implements ReportGeneratorService {

    private static Logger log = LoggerFactory.getLogger(ReportGeneratorServiceImpl.class);

    // Header sizes
    private final short SUPER_LARGE_TEXT = (short) 24;

    private final short LARGE_TEXT = (short) 18;

    private final short DEFAULT_TEXT_SIZE = (short) 12;

    private final int startColumn = 0;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private MedicalService medicalService;

    @Autowired
    private AuthService authService;

    @Autowired
    private UtilsService utilsService;

    // Font object
    private Font fontDefault;

    private Font fontDefaultStrong;

    private Font fontHeader1;

    private Font fontHeader2;

    // Cell style
    @Setter
    @Getter
    private CellStyle tableHeaderCellStyle;

    private CellStyle defaultCellStyle;

    private CellStyle totalCellStyle;

    @Setter
    @Getter
    private int currentRowIndex;

    @Override
    public String getReport(ReimsUser user, Long start, Long end, String reimbursementType) throws Exception {
        /* INIT */
        //create a new workbook
        XSSFWorkbook wb = new XSSFWorkbook();
        DATE_FORMAT.setTimeZone(TIME_ZONE);

        Sheet sheet = wb.createSheet(String.format("%s report", reimbursementType));

        // init
        setCurrentRowIndex(0);
        initFont(wb);
        initCellStyle(wb);
        initImage(wb, sheet); // end in 5th row, how can get attachments position dynamically?
        initHeader(wb, sheet, reimbursementType);
        initPersonalInfo(wb, sheet);

        // set column width
        sheet.autoSizeColumn(0); // autosize first column, depends on maximum width of applied cells [No.]
        sheet.setColumnWidth(1, getSizeOf(20)); // [Date]
        sheet.setColumnWidth(2, getSizeOf(25)); // [Title]
        sheet.setColumnWidth(3, getSizeOf(15)); //
        sheet.setColumnWidth(4, getSizeOf(15)); //
        sheet.setColumnWidth(5, getSizeOf(15)); //

        // determine report type
        switch (reimbursementType) {
            case PARKING_VALUE:
                writeParkingReport(sheet, start, end);
                break;
            case FUEL_VALUE:
                writeFuelReport(sheet, start, end);
                break;
            case MEDICAL_VALUE:
                writeMedicalReport(sheet, start, end);
        }

        initFooter(sheet);

        ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
        wb.write(fileOut);
        fileOut.close();
        byte[] file = fileOut.toByteArray();
        return Base64.getEncoder().encodeToString(file);
    }

    private int getSizeOf(int pixel) {
        return pixel * 256;
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
        log.info(String.format("Write Header Cell in row index %d", row.getRowNum()));

        createCell(row, 0, "No.").setCellStyle(getTableHeaderCellStyle());
        createCell(row, 1, "Date").setCellStyle(getTableHeaderCellStyle());
        createCell(row, 2, "Title").setCellStyle(getTableHeaderCellStyle());

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
        totalCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);

    }

    private void writeParkingReport(Sheet sheet, Long start, Long end) throws ReimsException {
        writeHeaderCell(sheet); // init header cell

        // init more specific column [HEADER]
        Row row = sheet.getRow(getCurrentRowIndex());
        createCell(row, 3, "Amount").setCellStyle(getTableHeaderCellStyle());

        setCurrentRowIndex(getCurrentRowIndex() + 1);

        int totalAmount = 0;
        int transactionIndex = 0;

        List<Transaction> parkings = transactionService.getByDateAndCategory(start, end, Transaction.Category.PARKING);

        /*LOOP*/
        for (Transaction transaction : parkings) {
            Parking parking = (Parking) transaction;
            ++transactionIndex;
            row = sheet.createRow(getCurrentRowIndex());
            setCurrentRowIndex(getCurrentRowIndex() + 1);
            createCell(row, 0, transactionIndex).setCellStyle(tableHeaderCellStyle);
            createCell(row, 1, DATE_FORMAT.format(new Date(parking.getDate())));
            createCell(row, 2, parking.getTitle());
            createCell(row, 3, convertAmountInString(parking.getAmount()));
            totalAmount += parking.getAmount();
        }
        // footer row
        row = sheet.createRow(getCurrentRowIndex() + 1);
        setCurrentRowIndex(getCurrentRowIndex() + 2);

        createCell(row, startColumn, "TOTAL:").setCellStyle(totalCellStyle);
        sheet.addMergedRegion(new CellRangeAddress(
                row.getRowNum(), //first row of header (0-based)
                row.getRowNum(), //last row of header (0-based)
                startColumn, //first column of header (0-based)
                2  //last column of header (0-based)
        ));

        createCell(row, 3, convertAmountInString(totalAmount));
        sheet.autoSizeColumn(3); // autosize 'amount' column
        setCurrentRowIndex(getCurrentRowIndex() + 1);
    }

    private void writeFuelReport(Sheet sheet, Long start, Long end) throws ReimsException {
        writeHeaderCell(sheet); // init header cell

        // write more specific column [HEADER]
        Row row = sheet.getRow(getCurrentRowIndex());
        createCell(row, 3, "Kilometers").setCellStyle(tableHeaderCellStyle);
        createCell(row, 4, "Liters").setCellStyle(tableHeaderCellStyle);
        createCell(row, 5, "Amount").setCellStyle(tableHeaderCellStyle);

        setCurrentRowIndex(getCurrentRowIndex() + 1);

        int totalAmount = 0;
        int transactionIndex = 0;

        List<Transaction> fuels = transactionService.getByDateAndCategory(start, end, Transaction.Category.FUEL);
        /*LOOP*/
        for (Transaction transaction : fuels) {
            Fuel fuel = (Fuel) transaction;
            ++transactionIndex;
            row = sheet.createRow(getCurrentRowIndex());
            createCell(row, 0, transactionIndex).setCellStyle(tableHeaderCellStyle);
            createCell(row, 1, DATE_FORMAT.format(new Date(fuel.getDate())));
            createCell(row, 2, fuel.getTitle());
            createCell(row, 3, fuel.getKilometers());
            createCell(row, 4, fuel.getLiters());
            createCell(row, 5, convertAmountInString(fuel.getAmount()));
            totalAmount += fuel.getAmount();
            setCurrentRowIndex(getCurrentRowIndex() + 1);
        }

        // footer row
        row = sheet.createRow(getCurrentRowIndex() + 1);
        setCurrentRowIndex(getCurrentRowIndex() + 2);

        createCell(row, startColumn, "TOTAL:").setCellStyle(totalCellStyle);

        sheet.addMergedRegion(new CellRangeAddress(
                row.getRowNum(), //first row of header (0-based)
                row.getRowNum(), //last row of header (0-based)
                startColumn, //first column of header (0-based)
                4  //last column of header (0-based)
        ));

        createCell(row, 5, convertAmountInString(totalAmount));
        sheet.autoSizeColumn(5); // autosize 'amount' column
        setCurrentRowIndex(getCurrentRowIndex() + 1);
    }

    private void writeMedicalReport(Sheet sheet, Long start, Long end) throws ReimsException {
        writeHeaderCell(sheet); // init header cell

        // write more specific column [HEADER]
        Row row = sheet.getRow(getCurrentRowIndex());
        createCell(row, 3, "Patient").setCellStyle(getTableHeaderCellStyle());
        createCell(row, 4, "Amount").setCellStyle(getTableHeaderCellStyle());

        setCurrentRowIndex(getCurrentRowIndex() + 1);
        int totalAmount = 0;
        int medicalIndex = 0;

        List<Medical> medicals = medicalService.getByDate(start, end);

        /*LOOP*/
        for (Medical medical : medicals) {
            ++medicalIndex;
            row = sheet.createRow(getCurrentRowIndex());
            createCell(row, 0, medicalIndex).setCellStyle(tableHeaderCellStyle);
            createCell(row, 1, DATE_FORMAT.format(new Date(medical.getDate())));
            createCell(row, 2, medical.getTitle());
            createCell(row, 3, medical.getPatient().getName());
            createCell(row, 4, convertAmountInString(medical.getAmount()));
            totalAmount += medical.getAmount();

            setCurrentRowIndex(getCurrentRowIndex() + 1);
        }

        // footer row
        row = sheet.createRow(getCurrentRowIndex() + 1);
        setCurrentRowIndex(getCurrentRowIndex() + 2);

        createCell(row, startColumn, "TOTAL:").setCellStyle(totalCellStyle);

        sheet.addMergedRegion(new CellRangeAddress(
                row.getRowNum(), //first row of header (0-based)
                row.getRowNum(), //last row of header (0-based)
                startColumn, //first column of header (0-based)
                3  //last column of header (0-based)
        ));

        createCell(row, 4, convertAmountInString(totalAmount));
        sheet.autoSizeColumn(4); // autosize 'amount' column

        setCurrentRowIndex(getCurrentRowIndex() + 1);
    }

    // space: 5 rows
    private void initImage(Workbook wb, Sheet sheet) throws IOException {
        // add picture data to this workbook.
        byte[] image = utilsService.getFile(GDN_LOGO_PATH);
        int pictureIdx = wb.addPicture(image, Workbook.PICTURE_TYPE_PNG);
        CreationHelper helper = wb.getCreationHelper();

        // Create the drawing patriarch.  This is the top level container for all shapes.
        Drawing drawing = sheet.createDrawingPatriarch();

        // add a picture shape
        ClientAnchor anchor = helper.createClientAnchor();

        // set top-left corner of the picture,
        // subsequent call of Picture#resize() will operate relative to it
        anchor.setCol1(0);
        anchor.setRow1(1);
        Picture pict = drawing.createPicture(anchor, pictureIdx);

        //auto-size picture relative to its top-left corner
        pict.resize(0.5);

        setCurrentRowIndex(getCurrentRowIndex() + 5);
    }

    private String convertAmountInString(long amount) {
        return String.format("Rp. %d", amount);
    }

    // need to be refactored!
    // (row) start: 5; end: 6;
    private void initHeader(Workbook wb, Sheet sheet, String reimbursementType) {
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
        cell2.setCellValue(String.format("REKAP BIAYA %s", reimbursementType.toUpperCase()));
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
        createCell(personalInfoRow, 0, "Nama :").setCellStyle(personalInfoStyle);
        createCell(personalInfoRow, 1, user.getUsername());

        personalInfoRow = sheet.createRow(getCurrentRowIndex());
        setCurrentRowIndex(getCurrentRowIndex() + 1);
        createCell(personalInfoRow, 0, "Divisi :").setCellStyle(personalInfoStyle);
        createCell(personalInfoRow, 1, user.getDivision());

        personalInfoRow = sheet.createRow(getCurrentRowIndex());
        setCurrentRowIndex(getCurrentRowIndex() + 1);
        createCell(personalInfoRow, 0, "No. Polisi :").setCellStyle(personalInfoStyle);
        createCell(personalInfoRow, 1, user.getLicense());

        personalInfoRow = sheet.createRow(getCurrentRowIndex());
        setCurrentRowIndex(getCurrentRowIndex() + 1);
        createCell(personalInfoRow, 0, "Kendaraan :").setCellStyle(personalInfoStyle);
        createCell(personalInfoRow, 1, user.getVehicle());

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

    private void initFooter(Sheet sheet) {
        Row row = sheet.createRow(getCurrentRowIndex());

        String date = DATE_FORMAT_2.format(new Date());
        String place = DEFAULT_PLACE;

        createCell(row, 0, String.format("%s, %s", place, date)).setCellStyle(tableHeaderCellStyle);

        sheet.addMergedRegion(new CellRangeAddress(
                row.getRowNum(), //first row of header (0-based)
                row.getRowNum(), //last row of header (0-based)
                0, //first column of header (0-based)
                3  //last column of header (0-based)
        ));

        setCurrentRowIndex(getCurrentRowIndex() + 1);

        row = sheet.createRow(getCurrentRowIndex());

        createCell(row, 0, "Dibuat secara otomatis oleh Reims.").setCellStyle(tableHeaderCellStyle);
        sheet.addMergedRegion(new CellRangeAddress(
                row.getRowNum(), //first row of header (0-based)
                row.getRowNum(), //last row of header (0-based)
                0, //first column of header (0-based)
                3  //last column of header (0-based)
        ));
    }

}
