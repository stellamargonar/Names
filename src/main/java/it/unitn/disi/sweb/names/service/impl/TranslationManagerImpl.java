package it.unitn.disi.sweb.names.service.impl;

import it.unitn.disi.sweb.names.repository.DictionaryDAO;
import it.unitn.disi.sweb.names.service.TranslationManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

@Component("translationManager")
public class TranslationManagerImpl implements TranslationManager {

	private DictionaryDAO dao;

	@Override
	public List<String> findTranslation(String name) {
		List<String> translations = findDB(name);
		if (translations == null || translations.isEmpty()) {
			translations = findWeb(name);
		}
		return translations;
	}

	private List<String> findWeb(String name) {
		List<String> result = null;
		try {
			Resource resource = new ClassPathResource(
					"behindthename.properties");
			Properties props = PropertiesLoaderUtils.loadProperties(resource);
			String url = props.getProperty("url")
					+ props.getProperty("paramName") + "=" + name.toLowerCase();
			result = extractHTMLElement(url, props.getProperty("xpathExpr"));
		} catch (Exception ex) {
			result = new ArrayList<>();
		}
		return result;
	}

	private List<String> findDB(String name) {
		return dao.findTranslations(name);
	}

	private List<String> extractHTMLElement(String url, String path)
			throws XPathExpressionException, IOException {
		URL oracle = new URL(url);
		URLConnection yc = oracle.openConnection();
		InputStream is = yc.getInputStream();
		is = oracle.openStream();
		Tidy tidy = new Tidy();
		tidy.setQuiet(true);
		tidy.setShowWarnings(false);
		Document tidyDOM = tidy.parseDOM(is, null);
		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xPath = xPathFactory.newXPath();
		String expression = path;
		XPathExpression xPathExpression = xPath.compile(expression);

		NodeList nodes = (NodeList) xPathExpression.evaluate(tidyDOM,
				XPathConstants.NODESET);
		List<String> nodesText = new ArrayList<String>();
		for (int i = 0; i < nodes.getLength(); i++) {
			nodesText.add(nodes.item(i).getNodeValue());
		}

		return nodesText;
	}

	@Autowired
	public void setDao(DictionaryDAO dao) {
		this.dao = dao;
	}

}
