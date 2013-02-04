package it.unitn.disi.sweb.names.utils.dataset;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class LogToXls {

	@Value("${log.evaluate.file}")
	String logFile;
	@Value("${result.xls}")
	String outputFile;

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"META-INF/applicationContext.xml");
		LogToXls logToXls = context.getBean(LogToXls.class);
		 logToXls.resultToXls();
//		logToXls.resultToCsv();
	}

	public void resultToCsv() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(
					logFile)));
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					outputFile)));

			String line = null;
			boolean startNewLine = true;
			int rowNum = 0;

			String writeLine[] = null;
			while ((line = reader.readLine()) != null) {
				if (startNewLine) {
					writeLine = new String[5];
					rowNum++;
					startNewLine = false;
				}

				String cell = null;
				String prefix = null;
				int cellIndex = -1;
				if (line.startsWith("String")) {
					prefix = "StringSimilarity ";
					cellIndex = 1;

				}
				if (line.startsWith("Dict")) {
					prefix = "DictionaryLookup: ";
					cellIndex = 2;
				}
				if (line.startsWith("Token")) {
					prefix = "TokenAnalysis ";
					cellIndex = 3;
				}
				if (line.startsWith("Overall")) {
					prefix = "OverallTime ";
					cellIndex = 4;
					startNewLine = true;
				}
				if (line.startsWith("Similarity ")) {
					prefix = "Similarity";
					cellIndex = 0;
				}
				if (cellIndex != -1) {
					String tok[] = line.split(prefix);
					cell = tok.length > 1 ? tok[1] : "";
					writeLine[cellIndex] = cell;
					if (startNewLine) {
						StringBuffer buffer = new StringBuffer();
						for (String s : writeLine) {
							if (s == null) {
								s = "";
							}
							if (s.startsWith(" ")) {
								s = s.substring(1);
							}
							buffer.append("\"" + s + "\",");
						}
						buffer.append("\n");
						writer.write(buffer.toString());
					}
				}
				// add case with name
			}
			reader.close();
			writer.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	public void resultToXls() {
		try {
			HSSFWorkbook myWorkBook = new HSSFWorkbook();
			HSSFSheet mySheet = myWorkBook.createSheet();
			HSSFRow myRow = null;
			HSSFCell myCell = null;

			BufferedReader reader = new BufferedReader(new FileReader(new File(
					logFile)));
			String line = null;
			boolean startNewLine = true;
			int rowNum = 0;

			while ((line = reader.readLine()) != null) {
				if (startNewLine) {
					myRow = mySheet.createRow(rowNum);
					rowNum++;
					startNewLine = false;
				}

				String cell = null;
				String prefix = null;
				int cellIndex = -1;

				if (line.startsWith("Similarity")) {
					prefix = "Similarity ";
					cellIndex = 0;
				}
				if (line.startsWith("String")) {
					prefix = "StringSimilarity ";
					cellIndex = 1;
				}
				if (line.startsWith("Dict")) {
					prefix = "DictionaryLookup: ";
					cellIndex = 2;
				}
				if (line.startsWith("Token")) {
					prefix = "TokenAnalisys ";
					cellIndex = 3;
				}
				if (line.startsWith("Overall")) {
					prefix = "OverallTime ";
					cellIndex = 4;
					startNewLine = true;
				}

				if (cellIndex != -1) {
					myCell = myRow.createCell(cellIndex);
					String tok[] = line.split(prefix);
					cell = tok.length > 1 ? tok[1] : "";
					double nr = cell.equals("")?0:Double.parseDouble(cell);
					myCell.setCellValue(nr);
				}
				// add case with name
			}
			reader.close();

			FileOutputStream out = new FileOutputStream(outputFile);
			myWorkBook.write(out);
			out.close();

			System.out.println("Wrote " + rowNum + " rows");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
