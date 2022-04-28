package com.dlink.alert.email;

import com.dlink.alert.AlertException;
import com.dlink.utils.JSONUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ExcelUtils excel工具类
 * @author zhumingye
 * @date: 2022/4/3
 **/
public final class ExcelUtils {
    private static final int XLSX_WINDOW_ROW = 10000;
    private static final Logger logger = LoggerFactory.getLogger(ExcelUtils.class);

    private ExcelUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * generate excel file
     *
     * @param content the content
     * @param title the title
     * @param xlsFilePath the xls path
     */
    public static void genExcelFile(String content, String title, String xlsFilePath) {
        File file = new File(xlsFilePath);
        if (!file.exists() && !file.mkdirs()) {
            logger.error("Create xlsx directory error, path:{}", xlsFilePath);
            throw new AlertException("Create xlsx directory error");
        }

        List<LinkedHashMap> itemsList = JSONUtil.toList(content, LinkedHashMap.class);

        if (CollectionUtils.isEmpty(itemsList)) {
            logger.error("itemsList is null");
            throw new AlertException("itemsList is null");
        }

        LinkedHashMap<String, Object> headerMap = itemsList.get(0);

        List<String> headerList = new ArrayList<>();

        for (Map.Entry<String, Object> en : headerMap.entrySet()) {
            headerList.add(en.getKey());
        }
        try (SXSSFWorkbook wb = new SXSSFWorkbook(XLSX_WINDOW_ROW);
             FileOutputStream fos = new FileOutputStream(String.format("%s/%s.xlsx", xlsFilePath, title))) {
            // declare a workbook
            // generate a table
            Sheet sheet = wb.createSheet();
            Row row = sheet.createRow(0);
            //set the height of the first line
            row.setHeight((short) 500);

            //set Horizontal right
            CellStyle cellStyle = wb.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.RIGHT);

            //setting excel headers
            for (int i = 0; i < headerList.size(); i++) {
                Cell cell = row.createCell(i);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(headerList.get(i));
            }

            //setting excel body
            int rowIndex = 1;
            for (LinkedHashMap<String, Object> itemsMap : itemsList) {
                Object[] values = itemsMap.values().toArray();
                row = sheet.createRow(rowIndex);
                //setting excel body height
                row.setHeight((short) 500);
                rowIndex++;
                for (int j = 0; j < values.length; j++) {
                    Cell cell1 = row.createCell(j);
                    cell1.setCellStyle(cellStyle);
                    if (values[j] instanceof Number) {
                        cell1.setCellValue(Double.parseDouble(String.valueOf(values[j])));
                    } else {
                        cell1.setCellValue(String.valueOf(values[j]));
                    }
                }
            }

            for (int i = 0; i < headerList.size(); i++) {
                sheet.setColumnWidth(i, headerList.get(i).length() * 800);
            }

            //setting file output
            wb.write(fos);
            wb.dispose();
        } catch (Exception e) {
            throw new AlertException("generate excel error", e);
        }
    }

}
