package Utility;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;

import static io.restassured.RestAssured.given;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GenerateExcel {

	protected Workbook workbook = new XSSFWorkbook();
	protected Sheet sheet = workbook.createSheet("Sheet1");

	
	public void excelGenerator() {

		Row headerRow1 = sheet.createRow(0);
		Row headerRow2 = sheet.createRow(1);
		headerRow1.createCell(0).setCellValue("Sr");
		headerRow1.createCell(1).setCellValue("Set Id");
		headerRow1.createCell(4).setCellValue("Data from FDA API");
		headerRow2.createCell(2).setCellValue("Brand name");
		headerRow2.createCell(3).setCellValue("Generic name");
		headerRow2.createCell(4).setCellValue("Manufacturer");
		headerRow2.createCell(5).setCellValue("Version");
		headerRow2.createCell(6).setCellValue("Effective date");
		headerRow1.createCell(7).setCellValue("Data in PhactMI PI Index");
		headerRow2.createCell(7).setCellValue("Brand name");
		headerRow2.createCell(8).setCellValue("Generic name");
		headerRow2.createCell(9).setCellValue("Manufacturer");
		headerRow2.createCell(10).setCellValue("Version");
		headerRow2.createCell(11).setCellValue("Effective date");
		headerRow1.createCell(12).setCellValue("Status");
		headerRow1.createCell(13).setCellValue("Remark");

		CellRangeAddress mergedRowRegion1 = new CellRangeAddress(0, 1, 0, 0);
		sheet.addMergedRegion(mergedRowRegion1);

		CellRangeAddress mergedRowRegion2 = new CellRangeAddress(0, 1, 1, 1);
		sheet.addMergedRegion(mergedRowRegion2);

		CellRangeAddress mergedColumnsRegion = new CellRangeAddress(0, 0, 2, 6);
		sheet.addMergedRegion(mergedColumnsRegion);

		CellRangeAddress mergedColumnsRegion2 = new CellRangeAddress(0, 0, 7, 11);
		sheet.addMergedRegion(mergedColumnsRegion2);

		CellRangeAddress mergedRowRegion3 = new CellRangeAddress(0, 1, 12, 12);
		sheet.addMergedRegion(mergedRowRegion3);

		CellRangeAddress mergedRowRegion4 = new CellRangeAddress(0, 1, 13, 13);
		sheet.addMergedRegion(mergedRowRegion4);
	}

	public void closeWorkbook() {

		try {
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
}
