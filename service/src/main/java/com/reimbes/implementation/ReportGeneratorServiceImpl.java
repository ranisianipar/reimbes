package com.reimbes.implementation;

import com.reimbes.Fuel;
import com.reimbes.ReimsUser;
import com.reimbes.ReportGeneratorService;
import com.reimbes.Transaction;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service
public class ReportGeneratorServiceImpl implements ReportGeneratorService {

    @Autowired
    private FuelServiceImpl fuelService;


    // belom di filter berdasarkan waktu --> FILTER MONTHLY
    public byte[] getReport(ReimsUser user) throws Exception{

        String filename = "workbook.xls";

        // perlu ga si page?
        List<Fuel> fuels = fuelService.getByUser(user);
        Workbook wb = new HSSFWorkbook();

        CellStyle cellStyleDate = wb.createCellStyle();
        CreationHelper createHelper = wb.getCreationHelper();

        cellStyleDate.setDataFormat(
                createHelper.createDataFormat().getFormat("m/d/yy h:mm"));

        OutputStream out;
        try {
            out = new FileOutputStream(filename);

            Sheet fuel = wb.createSheet("Fuel Report");
            Sheet parking = wb.createSheet("Parking Report");


            // styling header
//            Font headerFont = wb.createFont();
//            headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
//            headerFont.setColor(IndexedColors.LIGHT_GREEN.getIndex());
//            headerFont.setFontName("Arial");
//
//            CellStyle headerCellStyle = wb.createCellStyle();
//            headerCellStyle.setFont(headerFont);

            // first row --> header
            Row rowFuel = fuel.createRow(0);
            Row rowParking = parking.createRow(0);


            Iterator<Fuel> iterator = fuels.iterator();
            Transaction transaction;


            // fuel
            createCell(rowFuel, 0, "ID");
            createCell(rowFuel, 1, "Title");
            createCell(rowFuel, 2, "Date");
            createCell(rowFuel, 3, "Amount");
            createCell(rowFuel, 4, "Image");
            createCell(rowFuel, 5, "Liters");

            int index = 1;
            while (iterator.hasNext()) {
                rowFuel = fuel.createRow(index++);
                transaction = iterator.next();

                if (transaction.getCategory().equals(Transaction.Category.FUEL)) {

                    createCell(rowFuel, 0, transaction.getId());
                    createCell(rowFuel, 1, transaction.getTitle());
                    createDateCell( cellStyleDate, rowFuel, 2, transaction.getDate());

                    createCell(rowFuel, 3, transaction.getAmount());
                    createCell(rowFuel, 4, transaction.getImage());
                    createCell(rowFuel, 5, ((Fuel) transaction).getLiters());
                }
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
