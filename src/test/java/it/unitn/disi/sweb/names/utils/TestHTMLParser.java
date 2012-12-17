package it.unitn.disi.sweb.names.utils;

import it.unitn.disi.sweb.names.model.Translation;
import it.unitn.disi.sweb.names.repository.DictionaryDAO;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/testApplicationContext.xml" })
public class TestHTMLParser {

	HtmlParser html;

	@Autowired
	DictionaryDAO dao;

	// @Before
	// public void init() {
	// html = new HtmlParser();
	// html.setDictionaryDAO(dao);
	// System.out.println("test dao: " + dao);
	// }

	@Test
	public void testTranslation() {

		html = new HtmlParser();
		html.setDictionaryDAO(dao);
		html.extractTranslations();
	}

	// @Test
	// public void testJobs() {
	// try {
	// Set<String> list = html.extractJobList();
	// for(String s: list)
	// System.out.println(s);
	// }
	// catch (Exception ex) {
	// ex.printStackTrace();
	// }
	// }

}
