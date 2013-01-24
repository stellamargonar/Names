package it.unitn.disi.sweb.names.service;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.NamedEntity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testApplicationContext.xml"})
@Transactional(rollbackFor = Throwable.class)
public class TestDataSet extends TestCase {

	@Autowired
	EtypeManager etypeManager;
	@Autowired
	EntityManager entityManager;
	@Autowired
	NameManager nameManager;

	@Autowired
	NameMatch nameMatch;

	private final String inputFile = "src/test/resources/dataset.txt";
	private List<TestEntry> entries;
	private EType etype;

	@Override
	@Before
	public void setUp() throws Exception {
		etype = etypeManager.getEtype(EtypeName.PERSON);
		entries = readDataSet(inputFile);
	}

	private List<TestEntry> readDataSet(String inputFile2) throws IOException {
		FileInputStream in = new FileInputStream(inputFile);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		List<TestEntry> entries = new ArrayList<>();
		String line;
		while ((line = reader.readLine()) != null) {
			NamedEntity en = entityManager.createEntity(etype, line);
			TestEntry entry = new TestEntry();

			// header line
			String tokens[] = line.split("\t");
			for (String tok : tokens) {
				FullName name = nameManager.createFullName(tok, en);
				if (name != null) {
					entry.addName(name);
				}
			}

			while (!(line = reader.readLine()).contains("-------")) {
				entry.addVariation(line);
			}
			entries.add(entry);
		}
		reader.close();
		return entries;
	}
	@Override
	@After
	public void tearDown() throws Exception {
	}

	// @Test
	public void testVariants() {
		for (TestEntry e : entries) {
			FullName original = e.names.get(0);
			for (FullName name : e.names) {
				testMatch(original.getName(), name.getName(), e);
			}
		}
	}

	@Test
	public void testVariations() {
		for (TestEntry e : entries) {
			String original = e.names.get(0).getName();
			for (String name : e.variations) {
				testMatch(original, name, e);
			}
		}
	}

//	@Test
	public void partialTest() {
		testMatch("Alberto Asor Rosa", "Albert Rosa",null);
	}

	private void testMatch(String original, String name, TestEntry e) {
		double similarity = nameMatch.match(original, name, etype);
		if (similarity <= 0.5) {
			System.out.println(original + ", " + name + ": " + similarity);
		}
		// assertTrue(similarity > 0.5);

	}

	private class TestEntry {
		List<FullName> names;
		List<String> variations;

		void addName(FullName name) {
			if (names == null) {
				names = new ArrayList<>();
			}
			names.add(name);
		}
		void addVariation(String variation) {
			if (variations == null) {
				variations = new ArrayList<>();
			}
			variations.add(variation);
		}

		@Override
		public String toString() {
			String r = "";
			for (FullName f : names) {
				r += f.getName() + " ";
			}
			for (String v : variations) {
				r += v + " ";
			}
			return r + "\n";
		}
	}
}
