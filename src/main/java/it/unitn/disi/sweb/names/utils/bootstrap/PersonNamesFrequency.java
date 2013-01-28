package it.unitn.disi.sweb.names.utils.bootstrap;

import it.unitn.disi.sweb.names.model.NameElement;
import it.unitn.disi.sweb.names.service.ElementManager;
import it.unitn.disi.sweb.names.service.EtypeManager;
import it.unitn.disi.sweb.names.service.EtypeName;
import it.unitn.disi.sweb.names.service.NameManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class PersonNamesFrequency {

	private String givennameFile;
	private String familynameFile;

	private NameManager nameManager;
	private ElementManager elementManager;
	private EtypeManager etypeManager;

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"META-INF/applicationContext.xml");
		PersonNamesFrequency bean = context.getBean(PersonNamesFrequency.class);
//		bean.extractFamilyNames();
		bean.extractGivenNames();

	}

	public void extractFamilyNames() {
		Map<String, String> names = readFromFile(familynameFile);
		storeNames(names, "FamilyName");
	}

	public void extractGivenNames() {
		Map<String, String> names = readFromFile(givennameFile);
		storeNames(names, "GivenName");
	}

	private void storeNames(Map<String, String> names, String nameElement) {
		NameElement el = elementManager.findNameElement(nameElement,
				etypeManager.getEtype(EtypeName.PERSON));

		int count = 0;
		int all = names.size();

		for (Entry<String, String> e : names.entrySet()) {
			String name = e.getKey();
			int frequency = Integer.parseInt(e.getValue());
			nameManager.createIndividualName(name, frequency, el);
			count++;
			if ((double) count * 100 / all % 10 == 0) {
				System.out.println("Stored " + count + " names on " + all);
			}
		}

	}
	private Map<String, String> readFromFile(String fileName) {
		Map<String, String> map = new HashMap<>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line = null;
			while ((line = reader.readLine()) != null) {
				String[] tokens = line.split("\t");
				map.put(tokens[0], tokens[1]);
			}
			reader.close();
		} catch (IOException excep) {
			excep.printStackTrace();
		}
		return map;
	}
	@Autowired
	public void setElementManager(ElementManager elementManager) {
		this.elementManager = elementManager;
	}
	@Autowired
	public void setEtypeManager(EtypeManager etypeManager) {
		this.etypeManager = etypeManager;
	}
	@Autowired
	public void setNameManager(NameManager nameManager) {
		this.nameManager = nameManager;
	}

	@Value("${english.givenname.file}")
	public void setGivennameFile(String givennameFile) {
		this.givennameFile = givennameFile;
	}

	@Value("${english.familyname.file}")
	public void setFamilynameFile(String familynameFile) {
		this.familynameFile = familynameFile;
	}
}
