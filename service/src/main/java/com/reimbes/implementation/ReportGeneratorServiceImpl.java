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
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service
public class ReportGeneratorServiceImpl implements ReportGeneratorService {

    @Autowired
    private FuelServiceImpl fuelService;


    // belom di filter berdasarkan waktu --> FILTER MONTHLY
    public OutputStream getReport(ReimsUser user) throws Exception{

        // perlu ga si page?
        List<Fuel> fuels = fuelService.getByUser(user,null);
        Workbook wb = new HSSFWorkbook();

        OutputStream out;
        try {
            out = new FileOutputStream("workbook.xls");

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


            Iterator<Fuel> iterator = fuels.iterator();
            Transaction transaction;


            // fuel
            Cell cellFuel = rowFuel.createCell(0);
            cellFuel.setCellValue("Transaction ID");
            cellFuel = rowFuel.createCell(1);
            cellFuel.setCellValue("Title");

//            CellStyle cellStyle = wb.createCellStyle();
//            cellStyle.setDataFormat(
//                    createHelper.createDataFormat().getFormat("m/d/yy h:mm"));
            cellFuel = rowFuel.createCell(2);
            cellFuel.setCellValue("Date");
//            cellFfuel.setCellStyle(cellStyle);


            cellFuel = rowFuel.createCell(3);
            cellFuel.setCellValue("Amount");
            cellFuel = rowFuel.createCell(4);
            cellFuel.setCellValue("Image");
            cellFuel = rowFuel.createCell(5);

            cellFuel.setCellValue("Liters");

            int index = 0;
            while (iterator.hasNext()) {
                rowFuel = fuel.createRow(index++);
                transaction = iterator.next();

                if (transaction.getCategory().equals(Transaction.Category.FUEL)) {

                    cellFuel = rowFuel.createCell(0);
                    cellFuel.setCellValue(transaction.getId());
                    cellFuel = rowFuel.createCell(1);
                    cellFuel.setCellValue(transaction.getTitle());

                    cellFuel = rowFuel.createCell(2);
                    try {
                        cellFuel.setCellValue(transaction.getDate());
                    }   catch (NullPointerException n) {
                        cellFuel.setCellValue(new Date());
                    }
                    cellFuel = rowFuel.createCell(3);
                    cellFuel.setCellValue(transaction.getAmount());
                    cellFuel = rowFuel.createCell(4);
                    cellFuel.setCellValue(transaction.getImage());
                    cellFuel = rowFuel.createCell(5);
                    cellFuel.setCellValue(((Fuel) transaction).getLiters());

                }

            }

            wb.write(out);

            return out;

        }   catch (Exception e) {
            throw e;
        }
    }

}
