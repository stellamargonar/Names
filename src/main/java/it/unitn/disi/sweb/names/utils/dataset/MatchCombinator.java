package it.unitn.disi.sweb.names.utils.dataset;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

@Component("matchGenerator")
public class MatchCombinator {

	private Dataset dataset;

	private double RANDOMPERCENTAGE;
	private String inputFile;
	private String outputFile;

	private String inputExternalFile;
	private String outputExternalFile;
	private boolean useExternalDataset;
	public MatchCombinator(Dataset dataset) {
		this.dataset = dataset;
	}
	public MatchCombinator() {

	}

	/**
	 * generates and saves a list of pair (matches) from the entries stored in
	 * the dataset. Generates both pair of names that are expected to match (
	 * {@link #generateCorrectMatch() generateCorrectMatch} and that should not
	 * match {@link #generateIncorrectMatch() generateIncorrectMatch}
	 */
	public void generateMatches() {
		System.out.println(useExternalDataset);
		System.out.println(getInputFile());
		System.out.println(getOutputFile());
		// initialize the dataset with the one read from inputfile
		setDataset(readDataset());

		// add correct matches (names and variations from the same entity)
		dataset.addAllMatch(generateCorrectMatch());

		// generate incorrect matches (names or variations from different
		// entities)
		dataset.addAllMatch(generateIncorrectMatch());

		System.out.println(dataset.getMatchEntries().size());

		// saves entries and dataset in the outputFile
		saveDataset();
	}

	/**
	 * generates a list of pair of names which are expected to match. Those
	 * pairs are formed by names of the same entity
	 *
	 * @return list of correct match
	 */
	public List<MatchEntry> generateCorrectMatch() {
		List<MatchEntry> result = new ArrayList<>();

		// for each entity in the dataset
		for (Entry e : dataset.getEntries()) {
			for (Name name : e.getNames()) {
				// generate match with name and variations
				result.addAll(generateVariationsMatch(name, e));
			}
		}
		System.out.println("generated " + result.size() + " correct matches");

		return result;
	}

	/**
	 * generate a list of pair of names from different entries that should not
	 * match.
	 *
	 * The number of pairs is proportional to the size of the entry list and
	 * determined by {@literal #RANDOMPERCENTAGE} property setted in
	 * project.properties file.
	 *
	 * The pair are randomly generated.
	 *
	 * @return list of incorrect match
	 */
	public List<MatchEntry> generateIncorrectMatch() {
		int size = dataset.getEntries().size();
		List<MatchEntry> result = new ArrayList<>();

		// generate a number of random pairs of entries
		List<Map.Entry<Integer, Integer>> randomPairs = generateRandomPairs(size);

		Random r = new Random();

		for (Map.Entry<Integer, Integer> e : randomPairs) {
			Entry e1 = dataset.getEntries().get(e.getKey());
			Entry e2 = dataset.getEntries().get(e.getValue());

			if (e1.getEtype().equals(e2.getEtype())) {
				List<Name> list1 = new ArrayList<>(e1.getNames());
				if (e1.getVariations() != null) {
					list1.addAll(e1.getVariations());
				}

				List<Name> list2 = new ArrayList<>(e2.getNames());
				if (e2.getVariations() != null) {
					list2.addAll(e2.getVariations());
				}

				int r1 = r.nextInt(list1.size());
				int r2 = r.nextInt(list2.size());
				MatchEntry me = new MatchEntry(list1.get(r1), list2.get(r2),
						false, e1.getEtype());
				result.add(me);
			}
		}
		return result;
	}
	/**
	 * generate a number of pairs of integer, where those integer stand for the
	 * index of entries in the dataset entries list.
	 *
	 * Generates a
	 *
	 * @param size
	 * @return
	 */
	private List<Map.Entry<Integer, Integer>> generateRandomPairs(int size) {
		// number of entries that will be generated
		int allPossibleCombination = size * size - size;
		int randomEntries = (int) (RANDOMPERCENTAGE * allPossibleCombination);

		// shuffle list of entries (represented by their indexes)
		List<Integer> indexes = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			indexes.add(i);
		}

		List<Map.Entry<Integer, Integer>> map = new ArrayList<>(randomEntries);
		Random r = new Random(System.currentTimeMillis());

		for (int i = 0; i < randomEntries; i++) {
			int k = 0, v = 0;
			while (k == v) {
				k = r.nextInt(size);
				Collections.shuffle(indexes);
				v = indexes.get(k);
			}
			map.add(new AbstractMap.SimpleEntry<Integer, Integer>(k, v));
		}
		return map;
	}

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"META-INF/applicationContext.xml");
		MatchCombinator comb = context.getBean(MatchCombinator.class);
		comb.generateMatches();
	}

	/**
	 * creates a list of pairs formed by the original name and each possible
	 * name or variation in its original Entry.
	 *
	 * Those matches are expected to be correct
	 *
	 * @param name
	 *            original name
	 * @param e
	 *            dataset entry
	 * @return list of match
	 */
	private Collection<MatchEntry> generateVariationsMatch(Name name, Entry e) {
		List<MatchEntry> result = new ArrayList<>();

		// adds match with name and variation (misspellings)
		if (e.getVariations() != null) {
			for (Name v : e.getVariations()) {
				result.add(new MatchEntry(v, name, true, e.getEtype()));
			}
		}
		// add match with alternative names and translations
		for (Name n : e.getNames()) {
			if (!n.equals(name)) {
				System.out
						.println(" add " + name.getName() + " " + n.getName());
				result.add(new MatchEntry(n, name, true, e.getEtype()));
			}
		}
		return result;
	}

	private void saveDataset() {
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(Dataset.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(dataset, new File(getOutputFile()));
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Dataset readDataset() {
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(Dataset.class);
			Unmarshaller um = context.createUnmarshaller();
			System.out.println(um);
			FileReader fr = new FileReader(getInputFile());
			System.out.println(fr);
			Dataset d = (Dataset) um.unmarshal(fr);
			return d;
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String getInputFile() {
		if (useExternalDataset) {
			return inputExternalFile;
		} else {
			return inputFile;
		}
	}

	public String getOutputFile() {
		if (useExternalDataset) {
			return outputExternalFile;
		} else {
			return outputFile;
		}

	}

	public Dataset getDataset() {
		return dataset;
	}
	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}
	@Value("${dataset.original.xml}")
	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}
	@Value("${dataset.matched.xml}")
	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}
	@Value("${dataset.matched.randompercentage}")
	public void setRandomPercentage(double r) {
		RANDOMPERCENTAGE = r;
	}

	@Value("${dataset.external.xml}")
	public void setInputExternalFile(String inputExternalFile) {
		this.inputExternalFile = inputExternalFile;
	}
	@Value("${dataset.external.matched.xml}")
	public void setOutputExternalFile(String outputExternalFile) {
		this.outputExternalFile = outputExternalFile;
	}
	@Value("${dataset.external.use}")
	public void setUseExternalDataset(boolean useExternalDataset) {
		this.useExternalDataset = useExternalDataset;
	}

}
