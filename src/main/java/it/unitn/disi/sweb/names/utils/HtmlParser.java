package it.unitn.disi.sweb.names.utils;

import it.unitn.disi.sweb.names.model.Translation;
import it.unitn.disi.sweb.names.repository.DictionaryDAO;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class HtmlParser {

	DictionaryDAO dao;

	public void setDictionaryDAO(DictionaryDAO dao) {
		this.dao = dao;
		System.out.println("dao setted " + dao);
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
		// Object result = xPathExpression.evaluate(tidyDOM,
		// XPathConstants.NODESET);
		//
		NodeList nodes = (NodeList) xPathExpression.evaluate(tidyDOM,
				XPathConstants.NODESET);
		List<String> nodesText = new ArrayList<String>();
		for (int i = 0; i < nodes.getLength(); i++) {
			nodesText.add(nodes.item(i).getNodeValue());
		}

		return nodesText;
	}

	private List<Translation> extractXMLElement(String path, String expression)
			throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();

		XMLHandler handler = new XMLHandler();

		saxParser.parse(path, handler);

		return null;
	}

	public Set<String> extractJobList() {
		try { // "/html/body/div/table/tbody/tr/td/div/ul/li/a/text()"
			List<String> result = extractHTMLElement(
					"http://www.bls.gov/soc/2010/soc_alph.htm",
					"//td/div/ul/li/a/text()");
			Set<String> jobs = new HashSet<String>(result.size());
			for (String s : result) {
				String job = s;
				if (s.contains(",")) {
					String[] tokens = s.split(","); // there is always at most 1
													// comma per each row
					tokens[1] = tokens[1].substring(1, tokens[1].length());
					tokens[0] = removePluralS(tokens[0]);
					job = tokens[1] + " " + tokens[0];

					// adds the generic job ('Professor', 'Clerk') to the
					// complete list
					jobs.add(tokens[0]);

				}
				job = removePluralS(job);
				// adds the entire string representing the job ('History
				// Professor', 'Administrative Clerk')
				jobs.add(job);
			}
			return jobs;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void extractTranslations() {
		try {
			extractXMLElement(
					"/Users/stella/Downloads/translationDictionary/dict.xml",
					"transDict/page/*");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String removePluralS(String job) {
		if (job.endsWith("s")) {
			job = job.substring(0, job.length() - 1);
		}
		return job;
	}

	public static void main(String[] args) {

		new HtmlParser().extractTranslations();
	}

	private class XMLHandler extends DefaultHandler {

		boolean bfpage = false;
		boolean bflang = false;

		List<String> list;

		// private List<Translation> translations = new
		// ArrayList<Translation>();

		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {

			if (isPageTag(qName)) {
				bfpage = true;
				list = new ArrayList<String>();
			}
			if (bfpage && !isPageTag(qName))
				bflang = true;
		}

		public void endElement(String uri, String localName, String qName)
				throws SAXException {

			if (isPageTag(qName)) {
				bfpage = false;
				bflang = false;
				// add translations to some external data structure
				for (int i = 0; i < list.size(); i++) {
					String source = list.get(i);
					for (int j = i+1; j < list.size(); j++)
						if (!source.equals(list.get(j))) {
							dao.create(new Translation(source, list.get(j)));
						}
				}
			}
		}

		public void characters(char ch[], int start, int length)
				throws SAXException {
			if (bflang) {
				String s = (new String(ch, start, length));
				if (!s.equals("\n") && !s.equals("\n\t"))
					list.add(s);
			}
		}

		private boolean isPageTag(String qName) {
			return qName.equalsIgnoreCase("page");
		}
	}
}