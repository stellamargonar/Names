package it.unitn.disi.sweb.names.utils;

import static org.junit.Assert.*;
import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.service.EtypeManager;
import it.unitn.disi.sweb.names.service.EtypeName;
import it.unitn.disi.sweb.names.service.NameMatch;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/testApplicationContext.xml" })
public class TestNameMatch {

	@Autowired
	NameMatch nameMatcher;
	@Autowired
	EtypeManager etypeManager;

	EType etype;

	@Before
	public void setEtype() {
		etype = etypeManager.getEtype(EtypeName.PERSON);
	}

	@Test
	public void testStringEquals() {
		String name1 = "Stella Margonar";
		String name2 = "Stella Margonar";
		double similarity = nameMatcher.stringSimilarity(name1, name2, etype);
		printResult(name1, name2, similarity);
		assertEquals(similarity, 1, 0.1);
	}

	@Test
	public void testStringSimilarity() {
		String name1 = "Stella Margonar";
		String name2 = "stelaa Margar";

		double similarity = nameMatcher.stringSimilarity(name1, name2, etype);

		printResult(name1, name2, similarity);
		assertTrue(similarity > 0.5);
	}

	@Test
	public void testDictionaryExact() {
		String name1 = "Stella Margonar";
		String name2 = "Marietto";

		double similarity = nameMatcher.dictionaryLookup(name1, name2, etype);
		printResult(name1, name2, similarity);
		assertEquals(similarity, 1, 0.1);
	}

	@Test
	public void testDictionaryVariant() {
		String name1 = "Stella Margonar";
		String name2 = "Marietta";

		double similarity = nameMatcher.dictionaryLookup(name1, name2, etype);
		printResult(name1, name2, similarity);
		assertTrue(similarity > 0.5);
	}

	private void printResult(String name1, String name2, double result) {
		System.out.println("f(" + name1 + ", " + name2 + ") = " + result);
	}

}