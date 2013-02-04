package it.unitn.disi.sweb.names.utils.dataset;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.NameElement;
import it.unitn.disi.sweb.names.model.TriggerWord;
import it.unitn.disi.sweb.names.service.EtypeManager;
import it.unitn.disi.sweb.names.service.EtypeName;
import it.unitn.disi.sweb.names.service.NameManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

@Service("datasetExtraction")
public class DatasetExtraction {

	private final int SHEETLOCATION = 2;
	private final int SHEETPERSON = 0;
	private final int SHEETORGANIZATION = 1;

	private String inputFile;
	private String externalInputFile;
	private String outputFileXml;
	private String externalOutputFile;
	private Dataset dataset;
	private EtypeName etypeName;

	private NameManager nameManager;
	private EtypeManager etypeManager;
	private boolean extractPerson;
	private boolean extractLocation;
	private boolean extractOrganization;

	private boolean useExternalDataset;

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"META-INF/applicationContext.xml");

		DatasetExtraction datasetExtraction = context
				.getBean(DatasetExtraction.class);
		datasetExtraction.extractAll();
	}

	public void extractAll() {
		dataset = new Dataset();
		if (extractPerson) {
			extractPerson();
		}
		if (extractLocation) {
			extractOrganization();
		}
		if (extractOrganization) {
			extractLocations();
		}
		saveDataset();
	}

	public void extractLocations() {
		if (!useExternalDataset) {
			etypeName = EtypeName.LOCATION;
			HSSFWorkbook workbook = openWorkBook();
			HSSFSheet worksheet = workbook.getSheetAt(SHEETLOCATION);
			extractNames(worksheet);
		}
	}

	public void extractPerson() {
		etypeName = EtypeName.PERSON;
		if (!useExternalDataset) {
			HSSFWorkbook workbook = openWorkBook();
			HSSFSheet worksheet = workbook.getSheetAt(SHEETPERSON);
			extractNames(worksheet);
		} else {
			extractNames(externalInputFile);
		}
	}

	public void extractOrganization() {
		if (!useExternalDataset) {
			etypeName = EtypeName.ORGANIZATION;
			HSSFWorkbook workbook = openWorkBook();
			HSSFSheet worksheet = workbook.getSheetAt(SHEETORGANIZATION);
			extractNames(worksheet);
		}
	}

	private void saveDataset() {
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(Dataset.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(dataset, new File(getOutputFile()));
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * extracts entries from census dataset
	 *
	 * @param filePath
	 */
	private void extractNames(String filePath) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(
					filePath)));
			Map<String, String> aDataset = new HashMap<>();
			Map<String, String> bDataset = new HashMap<>();

			String line = null;
			while ((line = reader.readLine()) != null && line.startsWith("A")) {

				String tokens[] = line.split("\t");
				String tmp = "";
				for (int i = 2; i < tokens.length; i++) {
					tmp += tokens[i] + "\t";
				}
				aDataset.put(tokens[1], tmp);
			}
			while ((line = reader.readLine()) != null) {
				String tokens[] = line.split("\t");
				String tmp = "";
				for (int i = 2; i < tokens.length; i++) {
					tmp += tokens[i] + "\t";
				}
				bDataset.put(tokens[1], tmp);
			}

			// match two dataset
			for (Map.Entry<String, String> e : aDataset.entrySet()) {
				String aName = parse(e.getValue());
				String bName = bDataset.containsKey(e.getKey())
						? parse(bDataset.get(e.getKey()))
						: null;
				dataset.addEntry(createEntry(aName, bName));
			}
			for (Map.Entry<String, String> e : bDataset.entrySet()) {
				if (!aDataset.containsKey(e.getKey())) {
					String name = parse(e.getValue());
					dataset.addEntry(createEntry(name, null));
				}
			}

			reader.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private String parse(String line) {
		System.out.println(line);
		String result = "";
		String[] tokens = line.split("\t");
		result += tokens[0]; // surname
		System.out.println(Arrays.asList(tokens));
		String field = tokens[1];
		if (field.length() == 1) {
			// case initial
			result += ",," + field;
		} else {
			try {
				Integer.parseInt(field);
				return result;
			} catch (NumberFormatException nfe) {
				// name
				result += "," + field;
				field = tokens[2];
				if (field.length() == 1) {
					result += "," + field;
				} else {
					return result;
				}
			}
		}
		return result;
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
		}
	}

	private Entry createEntry(String entry, String variationEntry) {
		String[] elements = entry.split(",");
		String surname = elements[0];
		String name = elements.length > 1 ? elements[1] : null;
		String initial = elements.length > 2 ? elements[2] : null;

		EType etype = etypeManager.getEtype(EtypeName.PERSON);
		Entry e = new Entry(etype);
		Name n = createName(surname, name, initial);
		e.addName(n);

		if (variationEntry != null) {
			elements = variationEntry.split(",");
			surname = elements[0];
			name = elements.length > 1 ? elements[1] : null;
			initial = elements.length > 2 ? elements[2] : null;
			e.addVariation(createName(surname, name, initial));
		}
		return e;
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

	private Name createName(String surname, String name, String initial) {
		String completeName = "";
		if (name != null) {
			completeName += name;
		}
		if (initial != null) {
			completeName += " " + initial;
		}
		if (!completeName.isEmpty()) {
			completeName += " ";
		}
		completeName += surname;

		Name n = new Name(completeName);
		List<java.util.Map.Entry<String, String>> tokens = new ArrayList<>();
		tokens.add(new AbstractMap.SimpleEntry<String, String>(surname,
				"FamilyName"));
		if (name != "") {
			tokens.add(new AbstractMap.SimpleEntry<String, String>(name,
					"GivenName"));
		}
		if (initial != "") {
			tokens.add(new AbstractMap.SimpleEntry<String, String>(initial,
					"MiddleName"));
		}
		n.setTokens(tokens);

		return n;
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
			if (entry.getValue() instanceof TriggerWord) {
				TriggerWord el = (TriggerWord) entry.getValue();
				tokens.add(new AbstractMap.SimpleEntry<String, String>(entry
						.getKey(), el.getType().getType()));
			}
		}
		n.setTokens(tokens);
		return n;
	}

	private HSSFWorkbook openWorkBook() {
		try {
			FileInputStream fileInputStream = new FileInputStream(inputFile);
			HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
			return workbook;
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return null;
	}

	public String getOutputFile() {
		if (useExternalDataset) {
			return externalOutputFile;
		} else {
			return outputFileXml;
		}
	}

	@Autowired
	public void setEtypeManager(EtypeManager etypeManager) {
		this.etypeManager = etypeManager;
	}

	@Autowired
	public void setNameManager(NameManager nameManager) {
		this.nameManager = nameManager;
	}
	@Value("${dataset.extract.location}")
	public void setExtractLocation(boolean extractLocation) {
		this.extractLocation = extractLocation;
	}
	@Value("${dataset.extract.person}")
	public void setExtractPerson(boolean extractPerson) {
		this.extractPerson = extractPerson;
	}
	@Value("${dataset.extract.organization}")
	public void setExtractOrganization(boolean extractOrganization) {
		this.extractOrganization = extractOrganization;
	}
	@Value("${dataset.original.xls}")
	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}
	@Value("${dataset.original.xml}")
	public void setOutputFileXml(String outputFileXml) {
		this.outputFileXml = outputFileXml;
	}
	@Value("${dataset.external.original}")
	public void setExternalInputFile(String externalInputFile) {
		this.externalInputFile = externalInputFile;
	}
	@Value("${dataset.external.xml}")
	public void setExternalOutputFile(String externalOutputFile) {
		this.externalOutputFile = externalOutputFile;
	}
	@Value("${dataset.external.use}")
	public void setUseExternalDataset(boolean useExternalDataset) {
		this.useExternalDataset = useExternalDataset;
	}

}
