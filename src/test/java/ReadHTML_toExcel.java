import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ReadHTML_toExcel {

    public static void main(String[] args) {
        String htmlFilePath = "./newman_report.html"; // Path to your HTML file
        String excelFilePath = "./output"+new SimpleDateFormat("hhmmssa").format(new Date())+".xlsx"; // Path to save the Excel file

        try {
            // Parse the HTML file
            Document doc = Jsoup.parse(new File(htmlFilePath), "UTF-8");
            Elements requestBlocks = doc.selectXpath("//*[@id='execution-data']/*");

            // Set up Excel workbook and sheet
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("API Details");
            String[] columns = {"Description", "RequestType", "EndPoint", "ReponseStatusCode", "Pass%", "Request Headers", "Response Headers", "Request Body",  "Response Body"};

            // Create header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Populate Excel rows
            String value = "";
            int rowNum = 1, colCount;
            for (Element block : requestBlocks) {
            	colCount = 0;
                Row row = sheet.createRow(rowNum++);

                Element endpointElement = block.selectFirst("a");
                value = (endpointElement != null) ? endpointElement.text() : "";
                row.createCell(colCount++).setCellValue(value);
                
                endpointElement = block.selectFirst("strong:contains(Request Method:)");
                value = (endpointElement != null) ? endpointElement.nextElementSibling().text() : "";
                row.createCell(colCount++).setCellValue(value);
                
                // Extract and populate Endpoint
                endpointElement = block.selectFirst("strong:contains(Request URL:)");
                value = (endpointElement != null) ? endpointElement.nextElementSibling().text() : "";
                row.createCell(colCount++).setCellValue(value);
                
                endpointElement = block.selectFirst("strong:contains(Response Code:)");
                value = (endpointElement != null) ? endpointElement.nextElementSibling().text() : "";
                row.createCell(colCount++).setCellValue(value);
                
                endpointElement = block.selectFirst(".progress-bar");
                value = (endpointElement != null) ? endpointElement.text() : "";
                row.createCell(colCount++).setCellValue(value);

                Element reqHeaderElement = block.selectFirst("h5:contains(Request Headers) + .table-responsive table");
                value = (reqHeaderElement != null) ? reqHeaderElement.text() : "";
                row.createCell(colCount++).setCellValue(value);

                Element resHeaderElement = block.selectFirst("h5:contains(Response Headers) + .table-responsive table");
                value = (resHeaderElement != null) ? resHeaderElement.text() : "";
                row.createCell(colCount++).setCellValue(value);

                Element responseBodyElement = block.selectFirst("h5:contains(Request Body) + .dyn-height code");
                value = (responseBodyElement != null) ? responseBodyElement.text() : "";
                row.createCell(colCount++).setCellValue(value);
                
                responseBodyElement = block.selectFirst("h5:contains(Response Body) + .dyn-height code");
                value = (responseBodyElement != null) ? responseBodyElement.text() : "";
                if (value.length()>32767)
                	value = value.substring(0, 32766);
                row.createCell(colCount++).setCellValue(value);
            }

            // Auto-size columns for readability
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write output to Excel file
            FileOutputStream fileOut = new FileOutputStream(excelFilePath);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();

            System.out.println("Data extracted and saved to " + excelFilePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
