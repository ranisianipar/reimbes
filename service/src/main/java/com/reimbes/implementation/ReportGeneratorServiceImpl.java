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
import java.time.Instant;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static com.reimbes.constant.General.DATE_FORMAT;
import static com.reimbes.constant.General.TIME_ZONE;

@Service
public class ReportGeneratorServiceImpl implements ReportGeneratorService {

    private static Logger log = LoggerFactory.getLogger(ReportGeneratorServiceImpl.class);

    @Autowired
    private TransactionServiceImpl transactionService;


    @Autowired
    public byte[] getReport(ReimsUser user, long start, long end) throws Exception {

        String filename = String.format("%s_%s_%s.xls", user.getUsername(), start+"", end+"");

        List<Transaction> transactions;

        if (start == 0 || end == 0)
            transactions = transactionService.getByUser(user);
        else
            transactions = transactionService.getByUserAndDate(user, start, end);

        Workbook wb = new HSSFWorkbook();

        DATE_FORMAT.setTimeZone(TIME_ZONE);

        OutputStream out;
        try {
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
                    createCell(row, 0, indexFuel);
                    createCell(row, 4, ((Fuel) transaction).getLiters());
                    accumulatedAmountFuel += transaction.getAmount();
                } else {
                    row = parking.createRow(indexParking++);
                    createCell(row, 0, indexParking);
                    createCell(row, 4, ((Parking) transaction).getHours());
                    accumulatedAmountParking += transaction.getAmount();
                }

                createCell(row, 1, transaction.getTitle());
                createCell(row, 2, DATE_FORMAT.format(new Date(transaction.getDate())));
                createCell(row, 3, transaction.getAmount());
            }

            createCell(fuel.createRow(indexFuel), 0, "TOTAL:");
            createCell(fuel.createRow(indexFuel), 2, accumulatedAmountFuel);

            createCell(parking.createRow(indexParking), 0, "TOTAL:");
            createCell(parking.createRow(indexParking), 2, accumulatedAmountParking);

            wb.write(out);

            out.close();
            return Files.readAllBytes(Paths.get(filename));

        }   catch (Exception e) {
            throw e;
        }
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

}
