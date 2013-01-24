package it.unitn.disi.sweb.names.utils.dataset;

import it.unitn.disi.sweb.names.service.NameMatch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class DataSetEvaluator {

	private NameMatch nameMatch;
	private Dataset dataset;

	@Autowired
	public void setNameMatch(NameMatch nameMatch) {
		this.nameMatch = nameMatch;
	}

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"META-INF/applicationContext.xml");
		DataSetEvaluator eval = context.getBean(DataSetEvaluator.class);
		eval.evaluate("src/test/resources/datasetMatch.xml",
				"src/test/resources/datasetResult.xml");
	}

	public void evaluate(String datasetFile, String resultFile) {
		Dataset resultDataset = new Dataset();
		dataset = readDataset(datasetFile);
		List<MatchEntry> tests = dataset.getMatchEntries();
		resultDataset = evaluateMatch(resultDataset, tests);
		saveDataset(resultDataset, resultFile);
	}

	private Dataset evaluateMatch(Dataset resultDataset, List<MatchEntry> tests) {
		for (MatchEntry test : tests) {
			double similarity = nameMatch.match(test.getName1().getName(), test
					.getName1().getName(), test.getEtype());
			resultDataset.addResult(new Result(test, similarity, 0.0));
		}
		return resultDataset;
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

	// c'e' qualcosa che non va....
	// tutti i test tornano sim = 1.0
	// probabilmente problema con il db
	// manca anche una funzione dove il dataset importato viene salvato nel db
	// in modo da avere un entita' per ogni entry
	// e quindi la correlazione tra i nomi, variant e translations

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