package PIReport;

import org.apache.poi.ss.usermodel.*;
import org.testng.annotations.Test;

import Utility.GenerateExcel;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;

import static io.restassured.RestAssured.given;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class GeneratePIReport extends GenerateExcel {

	static int  skipCount;
	static int rowIndex = 2;
	static int rowIndexPI = 2;
	int Total = 0;
	
	@Test
	public void PIReport() throws IOException { 
		int start = 0; 
		int count = 100;
		String FDAsetID = ""; 
		String FDAbrandName = ""; 
		String FDAgenericName = ""; 
		String FDAmanufacturerName = ""; 
		String FDAversion = ""; 
		String FDAeffectiveTime = "";
		String brandName = "";
		String genericName = "";
		String manufacturerName = "";
		String version = "";
		String effectiveTime = "";

		excelGenerator();

		do {
			RestAssured.baseURI = "https://api.fda.gov";
			String searchTerm1 = decodeValue("openfda.is_original_packager:true+AND+openfda.product_type:\"HUMAN%20PRESCRIPTION%20DRUG\"");
			String searchTerm2 = decodeValue("set_id:asc");
			String response2 = given().header("Content-Type",
					"application/json").header("Content-Type","charset=utf-8").header("api_key",
							"WFHY1r0AJiKgUefFo2bHng55OvreOpAyfIMMHn4A").queryParam("search",""+searchTerm1
									+"").queryParam("limit",
											"100").queryParam("sort", ""+searchTerm2+"").queryParam("skip",
													""+skipCount+"").when().get("/drug/label.json").then().assertThat().
					statusCode(200).extract().asString();

			JsonPath js2 = new JsonPath(response2);
			
			Total = js2.getInt("meta.results.total");

			ArrayList<String> FDAlistOfIDs = new ArrayList<String>();
			ArrayList<String> FDAlistOfBrandName = new ArrayList<String>();
			ArrayList<String> FDAlistOfGenericName = new ArrayList<String>();
			ArrayList<String> FDAlistOfManufacturerName = new ArrayList<String>();
			ArrayList<String> FDAlistOfVersion = new ArrayList<String>();
			ArrayList<String> FDAlistOfEffectiveTime = new ArrayList<String>();

			for(int i=start; i<count; i++){
				Row dataRow = sheet.createRow(rowIndex++);
				dataRow.createCell(0).setCellValue(rowIndex - 2);
				FDAsetID = js2.getString("results["+i+"].set_id");
				FDAlistOfIDs.add(FDAsetID);
				dataRow.createCell(1).setCellValue(FDAlistOfIDs.get(i));
				if(js2.getString("results["+i+"].openfda.brand_name") == null) {
					FDAbrandName = js2.getString("results["+i+"].openfda.brand_name");
					FDAlistOfBrandName.add(FDAbrandName);
					dataRow.createCell(2).setCellValue(FDAlistOfBrandName.get(i));
				}
				else {
					FDAbrandName = js2.getString("results["+i+"].openfda.brand_name").replace('[', ' ').replace(']', ' ');
					FDAlistOfBrandName.add(FDAbrandName);
					dataRow.createCell(2).setCellValue(FDAlistOfBrandName.get(i));
				}
				if(js2.getString("results["+i+"].openfda.generic_name") == null ) {
					FDAgenericName = js2.getString("results["+i+"].openfda.generic_name");
					FDAlistOfGenericName.add(FDAgenericName);	
					dataRow.createCell(3).setCellValue(FDAlistOfGenericName.get(i));
				}
				else {
					FDAgenericName = js2.getString("results["+i+"].openfda.generic_name").replace('[', ' ').replace(']', ' ');
					FDAlistOfGenericName.add(FDAgenericName);	
					dataRow.createCell(3).setCellValue(FDAlistOfGenericName.get(i));
				}
				if(js2.getString("results["+i+"].openfda.manufacturer_name") == null ) {
					FDAmanufacturerName = js2.getString("results["+i+"].openfda.manufacturer_name");
					FDAlistOfManufacturerName.add(FDAmanufacturerName);
					dataRow.createCell(4).setCellValue(FDAlistOfManufacturerName.get(i));
				}
				else {
					FDAmanufacturerName = js2.getString("results["+i+"].openfda.manufacturer_name").replace('[', ' ').replace(']', ' ');
					FDAlistOfManufacturerName.add(FDAmanufacturerName);
					dataRow.createCell(4).setCellValue(FDAlistOfManufacturerName.get(i));
				}
				FDAversion = js2.getString("results["+i+"].version");
				FDAlistOfVersion.add(FDAversion);
				dataRow.createCell(5).setCellValue(FDAlistOfVersion.get(i));
				FDAeffectiveTime = js2.getString("results["+i+"].effective_time");
				FDAlistOfEffectiveTime.add(FDAeffectiveTime);
				dataRow.createCell(6).setCellValue(FDAlistOfEffectiveTime.get(i));
			}

			try (FileOutputStream fileOut = new FileOutputStream("PI Report.xlsx"))
			{
				workbook.write(fileOut);
			} catch(IOException e)
			{
				e.printStackTrace();
			}

			for(int n= start; n<count; n++) {
				RestAssured.baseURI =
						"https://tzky1yfg0l.execute-api.us-east-1.amazonaws.com";
				String response = given().queryParam("documentId", ""+FDAlistOfIDs.get(n)+"")
						.when().get("/dev/api/service/label-search/label")
						.then().assertThat().statusCode(200).extract().asString();

				JsonPath js = new JsonPath(response);

				Row dataRow = sheet.getRow(rowIndexPI++);
				if(js.getString("data._source.openfda.brand_name") == null) {
					brandName = js.getString("data._source.openfda.brand_name");
					dataRow.createCell(7).setCellValue(brandName);
				}
				else {
					brandName = js.getString("data._source.openfda.brand_name").replace('[', ' ').replace(']', ' ');
					dataRow.createCell(7).setCellValue(brandName);
				}
				if(js.getString("data._source.openfda.generic_name") == null) {
					genericName = js.getString("data._source.openfda.generic_name");
					dataRow.createCell(8).setCellValue(genericName);
				}
				else {
					genericName = js.getString("data._source.openfda.generic_name").replace('[', ' ').replace(']', ' ');
					dataRow.createCell(8).setCellValue(genericName);
				}
				if(js.getString("data._source.openfda.manufacturer_name") == null) {
					manufacturerName = js.getString("data._source.openfda.manufacturer_name");
					dataRow.createCell(9).setCellValue(manufacturerName);
				}
				else {
					manufacturerName = js.getString("data._source.openfda.manufacturer_name").replace('[', ' ').replace(']', ' ');
					dataRow.createCell(9).setCellValue(manufacturerName);
				}
				version = js.getString("data._source.version");
				dataRow.createCell(10).setCellValue(version);
				effectiveTime = js.getString("data._source.effective_time");
				dataRow.createCell(11).setCellValue(effectiveTime);
					
				  if(FDAlistOfBrandName.get(n) == null && FDAlistOfGenericName.get(n) == null && FDAlistOfManufacturerName.get(n) == null && FDAlistOfVersion.get(n) == null && FDAlistOfEffectiveTime.get(n) == null) {
					  dataRow.createCell(12).setCellValue("Warning");
					  dataRow.createCell(13).setCellValue("Product not found in FDA");
					}else if(brandName == null && genericName == null && manufacturerName == null && version == null && effectiveTime == null) {
						  dataRow.createCell(12).setCellValue("Error");
						  dataRow.createCell(13).setCellValue("Product label missing in PhactMI Index");
					}else if(FDAlistOfBrandName.get(n).equals(brandName) && FDAlistOfGenericName.get(n).equals(genericName) && FDAlistOfManufacturerName.get(n).equals(manufacturerName) && FDAlistOfVersion.get(n).equals(version) && FDAlistOfEffectiveTime.get(n).equals(effectiveTime)) {
						  dataRow.createCell(12).setCellValue("Success");
						  dataRow.createCell(13).setCellValue("-");
					}else {
						if(!FDAlistOfBrandName.get(n).equals(brandName)){
							  dataRow.createCell(12).setCellValue("Error");
							  dataRow.createCell(13).setCellValue("Brand Name Mismatch");
						}else if(!FDAlistOfGenericName.get(n).equals(genericName)) {
							dataRow.createCell(12).setCellValue("Error");
							dataRow.createCell(13).setCellValue("Generic Name Mismatch");
						}else if(!FDAlistOfManufacturerName.get(n).equals(manufacturerName)) {
							dataRow.createCell(12).setCellValue("Error");
							dataRow.createCell(13).setCellValue("Manufacturer Name Mismatch");
						}else if(!FDAlistOfVersion.get(n).equals(version)){
							dataRow.createCell(12).setCellValue("Error");
							dataRow.createCell(13).setCellValue("Version Mismatch");
						}else if(!FDAlistOfEffectiveTime.get(n).equals(effectiveTime)){
							dataRow.createCell(12).setCellValue("Error");
							dataRow.createCell(13).setCellValue("Effective Date Mismatch");
						}else {
							dataRow.createCell(12).setCellValue("Error");
							dataRow.createCell(13).setCellValue("Data Mismatch");
						} 
					}  
			}			 
			
			skipCount = skipCount + 100;

		}while(skipCount < Total);

		try (FileOutputStream fileOut = new FileOutputStream("PI Report.xlsx"))
		{
			workbook.write(fileOut);
		} catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	private static String decodeValue(String value) {
		try { 
			
			return URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
		} catch(UnsupportedEncodingException ex){
			throw new RuntimeException(ex.getCause());
		} 
	}

}
