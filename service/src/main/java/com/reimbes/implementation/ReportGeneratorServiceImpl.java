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
import java.util.UUID;

@Service
public class ReportGeneratorServiceImpl implements ReportGeneratorService {

    private static Logger log = LoggerFactory.getLogger(ReportGeneratorServiceImpl.class);

    @Autowired
    private FuelServiceImpl fuelService;

    @Autowired
    private TransactionServiceImpl transactionService;


    // belom di filter berdasarkan waktu --> FILTER MONTHLY
    public byte[] getReport(ReimsUser user) throws Exception{

        String filename = user.getUsername()+".xls";

        List<Transaction> transactions = transactionService.getByUser(user);
        Workbook wb = new HSSFWorkbook();

        CellStyle cellStyleDate = wb.createCellStyle();
        CreationHelper createHelper = wb.getCreationHelper();

        cellStyleDate.setDataFormat(
                createHelper.createDataFormat().getFormat("d/m/yy h:mm"));

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
            createCell(rowFuel, 0, "ID");
            createCell(rowParking, 0, "ID");
            createCell(rowFuel, 1, "Title");
            createCell(rowParking, 1, "Title");
            createCell(rowFuel, 2, "Date");
            createCell(rowParking, 2, "Date");
            createCell(rowFuel, 3, "Amount");
            createCell(rowParking, 3, "Amount");
            createCell(rowFuel, 4, "Image");
            createCell(rowParking, 4, "Image");


            createCell(rowFuel, 5, "Liters");
            createCell(rowParking, 5, "Hours");

            int indexFuel = 1;
            int indexParking = 1;

            Row row;
            while (iterator.hasNext()) {
                transaction = iterator.next();

                if (transaction.getCategory().equals(Transaction.Category.FUEL)) {
                    row = fuel.createRow(indexFuel++);
                    createCell(row, 5, ((Fuel) transaction).getLiters());
                } else {

                    row = parking.createRow(indexParking++);
                    createCell(row, 5, ((Parking) transaction).getHours());
                }

                createCell(row, 0, transaction.getId());
                createCell(row, 1, transaction.getTitle());
                createDateCell( cellStyleDate, row, 2, transaction.getDate());
                createCell(row, 3, transaction.getAmount());
                createCell(row, 4, transaction.getImage());
            }

            wb.write(out);

            out.close();
            return Files.readAllBytes(Paths.get(filename));

        }   catch (Exception e) {
            throw e;
        }
    }

    public byte[] getReport(ReimsUser user, Date start, Date end) throws Exception{

        String filename = String.format("%s_%s.xls", user.getUsername(), UUID.randomUUID());


//        List<Transaction> transactions = transactionService.getByUser(user);
        List<Transaction> transactions = transactionService.getByUserAndDate(user, start, end);

        Workbook wb = new HSSFWorkbook();

        CellStyle cellStyleDate = wb.createCellStyle();
        CreationHelper createHelper = wb.getCreationHelper();

        cellStyleDate.setDataFormat(
                createHelper.createDataFormat().getFormat("d/m/yy h:mm"));

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
            createCell(rowFuel, 0, "ID");
            createCell(rowParking, 0, "ID");
            createCell(rowFuel, 1, "Title");
            createCell(rowParking, 1, "Title");
            createCell(rowFuel, 2, "Date");
            createCell(rowParking, 2, "Date");
            createCell(rowFuel, 3, "Amount");
            createCell(rowParking, 3, "Amount");
            createCell(rowFuel, 4, "Image");
            createCell(rowParking, 4, "Image");


            createCell(rowFuel, 5, "Liters");
            createCell(rowParking, 5, "Hours");

            int indexFuel = 1;
            int indexParking = 1;

            Row row;
            while (iterator.hasNext()) {
                transaction = iterator.next();

                if (transaction.getCategory().equals(Transaction.Category.FUEL)) {
                    row = fuel.createRow(indexFuel++);
                    createCell(row, 5, ((Fuel) transaction).getLiters());
                } else {

                    row = parking.createRow(indexParking++);
                    createCell(row, 5, ((Parking) transaction).getHours());
                }

                createCell(row, 0, transaction.getId());
                createCell(row, 1, transaction.getTitle());
                createDateCell( cellStyleDate, row, 2, transaction.getDate());
                createCell(row, 3, transaction.getAmount());
                createCell(row, 4, transaction.getImage());
            }

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

    private Cell createDateCell(CellStyle cellStyle, Row row, int index, Date date) {
        if (date == null) return null;
        Cell cell = row.createCell(index);
        cell.setCellValue(date);
        cell.setCellStyle(cellStyle);

        return cell;
    }

}
