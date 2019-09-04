package com.reimbes.implementation;

import com.reimbes.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static com.reimbes.constant.General.DATE_FORMAT;
import static com.reimbes.constant.General.TIME_ZONE;

@Service
public class ReportGeneratorServiceImpl implements ReportGeneratorService {

    private static Logger log = LoggerFactory.getLogger(ReportGeneratorServiceImpl.class);

    @Autowired
    private TransactionServiceImpl transactionService;

    @Autowired
    private Utils utils;

    @Override
    public byte[] getReport(ReimsUser user, Long start, Long end) throws Exception {

        String filename;

        List<Transaction> transactions;

        if (start == null || end == null){
            filename = String.format("%s_%s.xls", user.getUsername(), "ALL");
            transactions = transactionService.getByUser(user);

        } else {
            transactions = transactionService.getByUserAndDate(user, start, end);
            filename = String.format("%s_%s_%s.xls", user.getUsername(), start+"", end+"");
        }

        Workbook wb = new HSSFWorkbook();

        DATE_FORMAT.setTimeZone(TIME_ZONE);

        OutputStream out;

        out = new FileOutputStream(filename);

        Sheet fuel = wb.createSheet("Fuel Report");
        Sheet parking = wb.createSheet("Parking Report");


        // styling header
        Font headerFont = wb.createFont();
        headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        headerFont.setColor(IndexedColors.LIGHT_GREEN.getIndex());
        headerFont.setFontName("Arial");

        CellStyle headerCellStyle = wb.createCellStyle();
        headerCellStyle.setFont(headerFont);

        // first row --> header
        Row rowFuel = fuel.createRow(0);
        Row rowParking = parking.createRow(0);

        rowFuel.setRowStyle(headerCellStyle);
        rowParking.setRowStyle(headerCellStyle);

        Iterator<Transaction> iterator = transactions.iterator();
        Transaction transaction;


        // fuel
        createCell(rowFuel, 0, "No.");
        createCell(rowParking, 0, "No.");
        createCell(rowFuel, 1, "Title");
        createCell(rowParking, 1, "Title");
        createCell(rowFuel, 2, "Date");
        createCell(rowParking, 2, "Date");
        createCell(rowFuel, 3, "Amount");
        createCell(rowParking, 3, "Amount");

        createCell(rowFuel, 4, "Liters");
        createCell(rowParking, 4, "Hours");

        int indexFuel = 1;
        int indexParking = 1;

        int accumulatedAmountParking = 0;
        int accumulatedAmountFuel = 0;


        Row row;
        while (iterator.hasNext()) {
            transaction = iterator.next();

            if (transaction.getCategory().equals(Transaction.Category.FUEL)) {
                row = fuel.createRow(indexFuel++);
                createCell(row, 0, indexFuel-1);
                createCell(row, 4, ((Fuel) transaction).getLiters());
                accumulatedAmountFuel += transaction.getAmount();
            } else {
                row = parking.createRow(indexParking++);
                createCell(row, 0, indexParking-1);
                createCell(row, 4, ((Parking) transaction).getHours());
                accumulatedAmountParking += transaction.getAmount();
            }

            createCell(row, 1, transaction.getTitle());
            createCell(row, 2, DATE_FORMAT.format(new Date(transaction.getDate())));
            createCell(row, 3, transaction.getAmount());
        }

        indexFuel++;
        row = fuel.createRow(indexFuel);
        createCell(row, 0, "TOTAL:");
        createCell(row, 3, accumulatedAmountFuel);

        indexParking++;
        row = parking.createRow(indexParking);
        createCell(row, 0, "TOTAL:");
        createCell(row, 3, accumulatedAmountParking);

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

}
