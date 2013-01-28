package it.unitn.disi.sweb.names.utils.dataset;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.NameElement;
import it.unitn.disi.sweb.names.model.NamedEntity;
import it.unitn.disi.sweb.names.model.TriggerWordType;
import it.unitn.disi.sweb.names.service.ElementManager;
import it.unitn.disi.sweb.names.service.EntityManager;
import it.unitn.disi.sweb.names.service.EtypeManager;
import it.unitn.disi.sweb.names.service.EtypeName;
import it.unitn.disi.sweb.names.service.NameManager;
import it.unitn.disi.sweb.names.service.NameMatch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("datasetevaluator")
public class DataSetEvaluator {

	private NameMatch nameMatch;
	private EntityManager entityManager;
	private NameManager nameManager;
	private ElementManager elementManager;
	private EtypeManager etypeManager;

	private Dataset dataset;

	private int testCount;
	private int passedResult;
	private int totalTests;

	private Log logger;

	@Autowired
	public void setLogger(Log logger) {
		this.logger = logger;
	}
	@Autowired
	public void setEtypeManager(EtypeManager etypeManager) {
		this.etypeManager = etypeManager;
	}
	@Autowired
	public void setNameMatch(NameMatch nameMatch) {
		this.nameMatch = nameMatch;
	}
	@Autowired
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	@Autowired
	public void setNameManager(NameManager nameManager) {
		this.nameManager = nameManager;
	}
	@Autowired
	public void setElementManager(ElementManager elementManager) {
		this.elementManager = elementManager;
	}

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"META-INF/applicationContext.xml");
		DataSetEvaluator eval = context.getBean(DataSetEvaluator.class);
		// eval.evaluate("src/test/resources/datasetMatch.xml",
		// "src/test/resources/datasetResult.xml");
		eval.test();
	}

	public void test() {
		List<Map.Entry<String, Object>> result = nameManager.parseFullName(
				"Turben Jacob", etypeManager.getEtype(EtypeName.PERSON));
		for (Map.Entry e : result) {
			if (e.getValue() instanceof NameElement) {
				System.out.println(e.getKey() + " "
						+ ((NameElement) e.getValue()).getElementName());
			}
			if (e.getValue() instanceof TriggerWordType) {
				System.out.println(e.getKey() + " "
						+ ((TriggerWordType) e.getValue()).getType());
			}

		}
	}
	@Transactional
	public void evaluate(String datasetFile, String resultFile) {
		passedResult = 0;
		testCount = 0;
		totalTests = 0;

		Dataset resultDataset = new Dataset();
		dataset = importDataset(datasetFile);
		List<MatchEntry> tests = dataset.getMatchEntries();

		totalTests = tests.size();
		double startTime = System.currentTimeMillis();
		logger.debug("Start evaluation");
		resultDataset = evaluateMatch(resultDataset, tests);
		saveDataset(resultDataset, resultFile);
		logger.debug("Passed " + passedResult + " tests on " + testCount
				+ ". Total time: " + (startTime - System.currentTimeMillis())
				+ " ms");
	}

	private Dataset evaluateMatch(Dataset resultDataset, List<MatchEntry> tests) {
		for (MatchEntry test : tests) {
			String name1 = test.getName1().getName();
			String name2 = test.getName2().getName();
			long start = System.nanoTime();
			double similarity = nameMatch.match(name1, name2, test.getEtype());
			long time = System.nanoTime() - start;
			Result result = new Result(test, similarity, time);
			resultDataset.addResult(result);

			testCount++;
			if (result.isPassed()) {
				passedResult++;
			}
			logger.debug("test " + testCount + " of " + totalTests
					+ " passed. Time " + time / Math.pow(10, 9) + ". " + name1
					+ " " + name2);
		}
		return resultDataset;
	}

	private Dataset importDataset(String inputFile) {
		// reads dataset from file
		Dataset data = readDataset(inputFile);

		// saves dataset entries in the system db
		for (Entry e : data.getEntries()) {
			EType etype = e.getEtype();
			NamedEntity entity = entityManager.createEntity(etype, e.getNames()
					.get(0).getName());
			for (Name name : e.getNames()) {
				nameManager.createFullName(name.getName(),
						createTokens(name.getTokens(), etype), entity);
			}
		}
		return data;
	}

	/**
	 * transforms the list of <string,string> entries representing <token,
	 * nameelement/triggerwordtype> in the ones with object for nameelement or
	 * triggerwordtype
	 *
	 * @param stringTokens
	 * @param etype
	 * @return
	 */
	private List<Map.Entry<String, Object>> createTokens(
			List<Map.Entry<String, String>> stringTokens, EType etype) {
		List<Map.Entry<String, Object>> result = new ArrayList<>();
		for (Map.Entry<String, String> e : stringTokens) {
			NameElement elem = elementManager.findNameElement(e.getValue(),
					etype);
			if (elem != null) {
				result.add(new AbstractMap.SimpleEntry<String, Object>(e
						.getKey(), elem));
			} else {
				TriggerWordType type = elementManager.findTriggerWordType(
						e.getValue(), etype);
				if (type != null) {
					result.add(new AbstractMap.SimpleEntry<String, Object>(e
							.getKey(), type));
				}
			}
		}
		return result;
	}

	private Dataset readDataset(String inputFile) {
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(Dataset.class);
			Unmarshaller um = context.createUnmarshaller();
			System.out.println(um);
			FileReader fr = new FileReader(inputFile);
			System.out.println(fr);
			Dataset d = (Dataset) um.unmarshal(fr);
			return d;
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void saveDataset(Dataset dataset, String outfile) {
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(Dataset.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(dataset, new File(outfile));
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

@XmlRootElement(name = "result")
class Result {
	private MatchEntry test;
	private double similarity;
	private boolean passed;
	private double time;

	public Result() {
	}

	public Result(MatchEntry entry, double similarity, double time) {
		test = entry;
		this.similarity = similarity;
		this.time = time;

		if (entry.isMatch()) {
			if (similarity > 0.5) {
				passed = true;
			} else {
				passed = false;
			}
		} else if (similarity > 0.5) {
			passed = false;
		} else {
			passed = true;
		}
	}

	@XmlElement(name = "name1")
	public String getName1() {
		return test.getName1().getName();
	}

	@XmlElement(name = "name2")
	public String getName2() {
		return test.getName2().getName();
	}

	@XmlTransient
	public MatchEntry getTest() {
		return test;
	}
	public void setTest(MatchEntry test) {
		this.test = test;
	}
	@XmlElement(name = "result")
	public double getSimilarity() {
		return similarity;
	}
	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}
	@XmlElement(name = "passed")
	public boolean isPassed() {
		return passed;
	}
	public void setPassed(boolean passed) {
		this.passed = passed;
	}
	@XmlElement(name = "time")
	public double getTime() {
		return time;
	}
	public void setTime(double time) {
		this.time = time;
	}

}