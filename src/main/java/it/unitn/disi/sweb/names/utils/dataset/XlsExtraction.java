package it.unitn.disi.sweb.names.utils.dataset;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.NameElement;
import it.unitn.disi.sweb.names.model.TriggerWordType;
import it.unitn.disi.sweb.names.service.EtypeManager;
import it.unitn.disi.sweb.names.service.EtypeName;
import it.unitn.disi.sweb.names.service.NameManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

@Service("datasetExtraction")
public class XlsExtraction {

	private final int SHEETLOCATION = 2;
	private final int SHEETPERSON = 0;
	private final int SHEETORGANIZATION = 1;

	private final String outputFile = "src/test/resources/dataset.txt";
	private final String outputFileXml = "src/test/resources/dataset.xml";
	private Dataset dataset;
	private EtypeName etypeName;

	private NameManager nameManager;
	private EtypeManager etypeManager;

	public void extractLocations() {
		etypeName = EtypeName.LOCATION;
		HSSFWorkbook workbook = openWorkBook();
		HSSFSheet worksheet = workbook.getSheetAt(SHEETLOCATION);
		extractNames(worksheet);
	}

	public void extractPerson() {
		etypeName = EtypeName.PERSON;
		HSSFWorkbook workbook = openWorkBook();
		HSSFSheet worksheet = workbook.getSheetAt(SHEETPERSON);
		extractNames(worksheet);
	}

	public void extractOrganization() {
		etypeName = EtypeName.ORGANIZATION;
		HSSFWorkbook workbook = openWorkBook();
		HSSFSheet worksheet = workbook.getSheetAt(SHEETORGANIZATION);
		extractNames(worksheet);
	}

	public void extractAll() {
		init();
		extractPerson();
		// extractOrganization();
		// extractLocations();
		saveDataset();
	}

	private void saveDataset() {
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(Dataset.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(dataset, new File(outputFileXml));
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"META-INF/applicationContext.xml");

		XlsExtraction datasetExtraction = context.getBean(XlsExtraction.class);
		// datasetExtraction.setEtypeManager(manager);
		datasetExtraction.extractAll();
	}

	private void init() {
		try {
			dataset = new Dataset();
			FileWriter writer = new FileWriter(new File(outputFile), false);
			writer.write("");
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void extractNames(HSSFSheet worksheet) {
		int rowCounter = worksheet.getLastRowNum();
		EType etype = etypeManager.getEtype(etypeName);
		String type = "";
		for (int i = 1; i < rowCounter; i++) {
			HSSFRow row = worksheet.getRow(i);
			if (i + 1 < rowCounter) {
				String next = worksheet.getRow(i + 1).getCell(1)
						.getStringCellValue();
				if (next == null || next.isEmpty()) {
					rowCounter = i;
				}
			}

			// type
			HSSFCell typeCell = row.getCell(0);
			String tmp = typeCell.getStringCellValue();
			if (tmp != null && !tmp.isEmpty()) {
				type = tmp;
			}

			// name
			String originalName = row.getCell(1).getStringCellValue();
			// translations
			List<String> translations = new ArrayList<>();
			translations.add(row.getCell(2).getStringCellValue());
			translations.add(row.getCell(3).getStringCellValue());
			translations.add(row.getCell(4).getStringCellValue());
			translations.add(row.getCell(5).getStringCellValue());
			translations.add(row.getCell(6).getStringCellValue());

			// variations
			List<String> variations = new ArrayList<>();
			for (int j = 7; j < 15; j++) {
				String value = row.getCell(j).getStringCellValue();
				if (value != null && !value.isEmpty()) {
					variations.add(value);
				}
			}

			// alternative names
			List<String> variants = new ArrayList<>();
			for (int j = 16; j < 23; j++) {
				HSSFCell cell = row.getCell(j);
				if (cell != null) {
					String value = cell.getStringCellValue();
					if (value != null && !value.isEmpty()) {
						variants.add(value);
					}
				}
			}
			dataset.addEntry(createEntry(originalName, translations,
					variations, variants, etype));
			System.out.println("saved entry " + originalName);
		}
	}

	private Entry createEntry(String originalName, List<String> translations,
			List<String> variations, List<String> variants, EType etype) {
		Entry e = new Entry(etype);
		e.setEtype(etype);
		List<String> names = new ArrayList<>();
		names.add(originalName);
		names.addAll(translations);
		names.addAll(variants);

		for (String n : names) {
			Name name = createName(n, etype);
			e.addName(name);
		}

		for (String n : variations) {
			Name name = createName(n, etype);
			e.addVariation(name);
		}
		return e;
	}

	private Name createName(String name, EType etype) {
		if (name == null || name.isEmpty()) {
			return null;
		}

		Name n = new Name(name);
		List<java.util.Map.Entry<String, Object>> parseResult = nameManager
				.parseFullName(name, etype);

		List<java.util.Map.Entry<String, String>> tokens = new ArrayList<>();
		for (java.util.Map.Entry<String, Object> entry : parseResult) {
			if (entry.getValue() instanceof NameElement) {
				NameElement el = (NameElement) entry.getValue();
				tokens.add(new AbstractMap.SimpleEntry<String, String>(entry
						.getKey(), el.getElementName()));
			}
			if (entry.getValue() instanceof TriggerWordType) {
				TriggerWordType el = (TriggerWordType) entry.getValue();
				tokens.add(new AbstractMap.SimpleEntry<String, String>(entry
						.getKey(), el.getType()));
			}
		}
		n.setTokens(tokens);
		return n;
	}

	private HSSFWorkbook openWorkBook() {
		try {
			FileInputStream fileInputStream = new FileInputStream(
					"src/main/resources/DataSet.xls");
			HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
			return workbook;
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return null;
	}

	@Autowired
	public void setEtypeManager(EtypeManager etypeManager) {
		this.etypeManager = etypeManager;
	}

	@Autowired
	public void setNameManager(NameManager nameManager) {
		this.nameManager = nameManager;
	}

}
