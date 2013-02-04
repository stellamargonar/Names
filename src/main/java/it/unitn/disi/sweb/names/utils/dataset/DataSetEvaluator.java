package it.unitn.disi.sweb.names.utils.dataset;

import it.unitn.disi.sweb.names.MisspellingsComparator;
import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.NameElement;
import it.unitn.disi.sweb.names.model.NamedEntity;
import it.unitn.disi.sweb.names.model.TriggerWord;
import it.unitn.disi.sweb.names.model.TriggerWordType;
import it.unitn.disi.sweb.names.service.ElementManager;
import it.unitn.disi.sweb.names.service.EntityManager;
import it.unitn.disi.sweb.names.service.EtypeManager;
import it.unitn.disi.sweb.names.service.NameManager;
import it.unitn.disi.sweb.names.service.NameMatch;
import it.unitn.disi.sweb.names.service.NameSearch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("datasetevaluator")
public class DataSetEvaluator {

	private EntityManager entityManager;
	private NameManager nameManager;
	private ElementManager elementManager;
	private Dataset dataset;
	private NameMatch nameMatch;
	private NameSearch nameSearch;
	private MisspellingsComparator comparator;

	private int testCount;
	private int passedResult;

	private String datasetFile;
	private String resultFile;

	private String externalDatasetFile;
	private String externalResultFile;

	private boolean useExternalDataset;
	private boolean evaluateSearch = false;

	protected static double THRESHOLD;
	static int MAXSEARCHINDEX = 2;

	private Log logger;

	@Resource(name = "${misspellings.comparator}")
	public void setComparator(MisspellingsComparator comparator) {
		this.comparator = comparator;
	}
	@Autowired
	public void setNameSearch(NameSearch nameSearch) {
		this.nameSearch = nameSearch;
	}
	@Autowired
	public void setEtypeManager(EtypeManager etypeManager) {
	}
	@Autowired
	public void setLogger(Log logger) {
		this.logger = logger;
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
	@Value("${threshold}")
	public void setThreshold(double threshold) {
		THRESHOLD = threshold;
	}
	@Value("${dataset.matched.xml}")
	public void setDatasetFile(String datasetFile) {
		this.datasetFile = datasetFile;
	}
	@Value("${dataset.evaluated.xml}")
	public void setResultFile(String resultFile) {
		this.resultFile = resultFile;
	}
	@Value("${dataset.external.matched.xml}")
	public void setExternalDatasetFile(String externalDatasetFile) {
		this.externalDatasetFile = externalDatasetFile;
	}
	@Value("${dataset.external.evaluated.xml}")
	public void setExternalResultFile(String externalResultFile) {
		this.externalResultFile = externalResultFile;
	}
	@Value("${dataset.external.use}")
	public void setUseExternalDataset(boolean useExternalDataset) {
		this.useExternalDataset = useExternalDataset;
	}

	public String getInputFile() {
		if (useExternalDataset) {
			return externalDatasetFile;
		} else {
			return datasetFile;
		}
	}
	public String getOutputFile() {
		if (useExternalDataset) {
			return externalResultFile;
		} else {
			return resultFile;
		}
	}

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"META-INF/applicationContext.xml");
		DataSetEvaluator eval = context.getBean(DataSetEvaluator.class);
		eval.evaluate();
	}

	@Transactional
	public void evaluateWithLevhenstein() {
		dataset = importDataset(getInputFile());
		List<MatchEntry> tests = dataset.getMatchEntries();
		long allTime = 0;
		int tp = 0, tn = 0, fp = 0, fn = 0;

		for (MatchEntry test : tests) {
			String name1 = test.getName1().getName();
			String name2 = test.getName2().getName();

			long start = System.currentTimeMillis();
			double similarity = comparator.getSimilarity(name1, name2);
			long time = System.currentTimeMillis() - start;

			allTime += time;
			if (test.isMatch()) {
				if (similarity > 0.6) {
					tp++;
				} else {
					System.out.println(name1 + " ; " + name2 + " : "
							+ similarity);
					fn++;
				}
			} else if (similarity > 0.6) {
				System.out.println(name1 + " ; " + name2 + " : " + similarity);
				fp++;
			} else {
				tn++;
			}
		}
		System.out.println("True Positive: " + tp);
		System.out.println("True Negative: " + tn);
		System.out.println("False Positive: " + fp);
		System.out.println("False Negative: " + fn);
		System.out.println("TIME: " + allTime);

	}

	@Transactional(rollbackFor = Throwable.class)
	public void evaluate() {
		passedResult = testCount = 0;

		Dataset resultDataset = new Dataset();
		dataset = importDataset(getInputFile());
		List<MatchEntry> tests = dataset.getMatchEntries();

		double startTime = System.nanoTime();
		logger.debug("Start evaluation on file " + getInputFile());
		Calendar now = new GregorianCalendar();
		System.out.println(now.get(Calendar.HOUR_OF_DAY) + ":"
				+ now.get(Calendar.MINUTE));

		if (evaluateSearch) {
			resultDataset = evaluateSearch(resultDataset, tests);
		} else {
			resultDataset = evaluateMatch(resultDataset, tests);
		}
		double time = System.nanoTime() - startTime;

		saveDataset(resultDataset, getOutputFile());
		logger.debug("Passed " + passedResult + " tests on " + testCount
				+ ". Total time: " + time / Math.pow(10, 6) + " millisec");
		now = new GregorianCalendar();
		System.out.println(now.get(Calendar.HOUR_OF_DAY) + ":"
				+ now.get(Calendar.MINUTE));
		summary(resultDataset);
	}
	private Dataset evaluateMatch(Dataset resultDataset, List<MatchEntry> tests) {
		for (MatchEntry test : tests) {
			String name1 = test.getName1().getName();
			String name2 = test.getName2().getName();

			long start = System.currentTimeMillis();
			double similarity = nameMatch.match(name1, name2, test.getEtype());
			long time = System.currentTimeMillis() - start;

			Result result = new Result(test, similarity, time);
			resultDataset.addResult(result);

			testCount++;

			if (result.isPassed()) {
				passedResult++;
			}
		}
		return resultDataset;
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

	private void summary(Dataset results) {
		double time = 0;
		int tp = 0, tn = 0, fp = 0, fn = 0;
		if (evaluateSearch) {
			for (SearchResult r : results.getSearchResult()) {
				time += r.getTime() / Math.pow(10, 6);
				if (r.isExpected()) {
					if (r.isPassed()) {
						tp++;
					}
					fn++;
				} else {
					if (r.isPassed()) {
						tn++;
					} else {
						fp++;
					}
				}
			}
		} else {
			for (Result r : results.getResults()) {
				time += r.getTime() / Math.pow(10, 6);
				if (r.isExpected()) {
					if (r.isMatched()) {
						tp++;
					} else {
						fn++;
						logger.debug("\"" + r.getName1() + "\";\""
								+ r.getName2() + "\";\"" + r.getSimilarity()
								+ "\";");
					}
				} else if (r.isMatched()) {
					logger.debug("\"" + r.getName1() + "\";\"" + r.getName2()
							+ "\";\"" + r.getSimilarity() + "\";");
					fp++;
				} else {

					tn++;
				}
			}
		}

		System.out.println("True Positive: " + tp);
		logger.debug("True Positive: " + tp);
		System.out.println("True Negative: " + tn);
		logger.debug("True Negative: " + tn);
		System.out.println("False Positive: " + fp);
		logger.debug("False Positive: " + fp);
		System.out.println("False Negative: " + fn);
		logger.debug("False Negative: " + fn);

		double accuracy = (double) (tp + tn) / (tp + tn + fp + fn);
		double recall = (double) tp / (tp + fn);
		double precision = (double) tp / (tp + fp);
		double fmeasure = 2 * (precision * recall) / (precision + recall);

		System.out.println("Accuracy: " + accuracy);
		logger.debug("Accuracy: " + accuracy);
		System.out.println("Precision: " + precision);
		logger.debug("Precision: " + precision);
		System.out.println("Recall: " + recall);
		logger.debug("Recall: " + recall);
		System.out.println("F-measure: " + fmeasure);
		logger.debug("F-measure: " + fmeasure);

		System.out.println("Overall time: " + time + " ms");
		logger.debug("Overall time: " + time + " ms");
		System.out.println("Average match time: " + time / testCount + " ms");
		logger.debug("Average match time: " + time / testCount + " ms");

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
					TriggerWord tw = elementManager.findTriggerWord(e.getKey(),
							type);
					result.add(new AbstractMap.SimpleEntry<String, Object>(e
							.getKey(), tw));
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

	public Dataset evaluateSearch(Dataset resultDataset, List<MatchEntry> tests) {
		int i = 0;
		for (MatchEntry test : tests) {
			String query = test.getName1().getName();
			String name = test.getName2().getName();
			System.out.println("-----");
			long start = System.currentTimeMillis();
//			List<Map.Entry<String, Double>> list = nameSearch.nameSearch(query);
			List<Map.Entry<NamedEntity, Double>> list = nameSearch.entityNameSearch(query);

			long time = System.currentTimeMillis() - start;

//			SearchResult res = new SearchResult(test, list, time);
//			resultDataset.addResult(res);


			System.out.println("query: " + query + " - result expt: " + name
					+ " " + test.isMatch() + " " + time + " ms");
//			System.out.println("\tpasseed: " + res.isPassed() + "\t" + time);
//			for (Map.Entry<NamedEntity, Double> e : list) {
//				System.out.println("\t - " + nameManager.find(e.getKey()));

//				if (name.equals(e.getKey())) {
//					System.out.println(">\t > " + e.getKey() + "\t\t"+ e.getValue());
//				} else {
//					System.out.println("\t - " + e.getKey() + "\t\t"+ e.getValue());
//				}
//			}
			i++;
			if (i > 2) {
				break;
			}
		}
		summary(resultDataset);
		return resultDataset;
	}

}
@XmlRootElement(name = "searchResult")
class SearchResult {

	private String query;
	private String name;
	private List<Map.Entry<String, String>> list;
	private long time;

	private boolean expected;
	private boolean passed = false;
	public SearchResult() {
	}

	public SearchResult(MatchEntry test, List<Map.Entry<String, Double>> list,
			long time) {
		query = test.getName1().getName();
		name = test.getName2().getName();
		expected = test.isMatch();

		if (list == null) {
			this.list = null;
		} else {
			this.list = new ArrayList<>();
			for (Map.Entry<String, Double> e : list) {
				this.list.add(new AbstractMap.SimpleEntry<String, String>(e
						.getKey(), e.getValue().toString()));
			}
		}
		this.time = time;
		passed = checkPassed(list);
	}

	private boolean checkPassed(List<Map.Entry<String, Double>> list) {
		if (expected) {
			if (list == null || list.isEmpty()) {
				return false;
			}
			for (int i = 0; i < DataSetEvaluator.MAXSEARCHINDEX; i++) {
				if (list.get(i).getKey().equals(name)) {
					return true;
				}
			}
		} else {
			if (list == null || list.isEmpty()) {
				return true;
			}
			for (int i = 0; i < DataSetEvaluator.MAXSEARCHINDEX; i++) {
				if (list.get(i).getKey().equals(name)) {
					return false;
				}
			}
		}
		return !expected;
	}

	@XmlElement(name = "query")
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	@XmlElement(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@XmlElement(name = "list")
	@XmlJavaTypeAdapter(ListAdapter.class)
	public List<Map.Entry<String, String>> getList() {
		return list;
	}
	public void setList(List<Map.Entry<String, String>> list) {
		this.list = list;
	}
	@XmlElement(name = "expected")
	public boolean isExpected() {
		return expected;
	}
	public void setExpected(boolean expected) {
		this.expected = expected;
	}
	@XmlElement(name = "passed")
	public void setPassed(boolean passed) {
		this.passed = passed;
	}
	public boolean isPassed() {
		return passed;
	}
	@XmlElement(name = "time")
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
}

@XmlRootElement(name = "result")
class Result {
	private MatchEntry test;
	private double similarity;
	private boolean passed;
	private double time;

	private boolean matched;
	private boolean expected;

	public Result() {
	}

	public Result(MatchEntry entry, double similarity, double time) {
		test = entry;
		this.similarity = similarity;
		this.time = time;

		expected = test.isMatch();
		matched = similarity > DataSetEvaluator.THRESHOLD;

		passed = expected && matched || !expected && !matched;
	}

	@XmlElement(name = "expected")
	public boolean isExpected() {
		if (test != null) {
			expected = test.isMatch();
		}
		return expected;
	}
	public void setExpected(boolean expected) {
		this.expected = expected;
	}
	public void setMatched(boolean matched) {
		this.matched = matched;
	}
	@XmlElement(name = "matched")
	public boolean isMatched() {
		matched = similarity > 0.5;
		return matched;
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