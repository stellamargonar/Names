package it.unitn.disi.sweb.names.utils;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.service.EtypeManager;
import it.unitn.disi.sweb.names.service.EtypeName;
import it.unitn.disi.sweb.names.service.NameMatch;

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

	@Test
	public void testStringEquals() {
		EType etype = etypeManager.getEtype(EtypeName.PERSON);

		String name1 = "Stella Margonar";
		String name2 = "Stella Margonar";

		nameMatcher.stringSimilarity(name1, name2, etype);
	}
	
	
	@Test
	public void testStringSimilarity() {
		EType etype = etypeManager.getEtype(EtypeName.PERSON);

		String name1 = "Stella Margonar";
		String name2 = "Dott.ssa Stella Margonar";

		nameMatcher.stringSimilarity(name1, name2, etype);
	}
	
	@Test
	public void testDictionaryExact() {
		EType etype = etypeManager.getEtype(EtypeName.PERSON);

		String name1 = "Stella Margonar";
		String name2 = "Marietto";

		nameMatcher.dictionaryLookup(name1, name2, etype);
	}
	
	@Test
	public void testDictionaryVariant() {
		EType etype = etypeManager.getEtype(EtypeName.PERSON);

		String name1 = "Stella Margonar";
		String name2 = "Marietta";

		nameMatcher.dictionaryLookup(name1, name2, etype);
	}
	

}