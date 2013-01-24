package it.unitn.disi.sweb.names.utils.dataset;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class MatchCombinator {

	private Dataset dataset;
	private final double RANDOMPERCENTAGE = 0.4;

	private final String inputFile = "src/test/resources/dataset.xml";
	private final String outputFile = "src/test/resources/datasetMatch.xml";

	public MatchCombinator(Dataset dataset) {
		this.dataset = dataset;
	}
	public MatchCombinator() {

	}

	public void generateMatches() {
		setDataset(readDataset());
		dataset.addAllMatch(generateCorrectMatch());
		dataset.addAllMatch(generateIncorrectMatch());
		saveDataset();
	}

	public List<MatchEntry> generateCorrectMatch() {
		List<MatchEntry> result = new ArrayList<>();
		for (Entry e : dataset.getEntries()) {
			for (Name name : e.getNames()) {
				result.addAll(generateVariationsMatch(name, e));
			}
		}
		return result;
	}

	public List<MatchEntry> generateIncorrectMatch() {
		// generate random match between names in the dataset
		int size = dataset.getEntries().size();
		List<MatchEntry> result = new ArrayList<>();
		Map<Integer, Integer> randomPairs = generateRandomPairs(size);
		Random r = new Random();
		for (Map.Entry<Integer, Integer> e : randomPairs.entrySet()) {
			Entry e1 = dataset.getEntries().get(e.getKey());
			Entry e2 = dataset.getEntries().get(e.getValue());

			List<Name> list1 = e1.getNames();
			list1.addAll(e1.getVariations());

			List<Name> list2 = e2.getNames();
			list2.addAll(e2.getVariations());

			int r1 = r.nextInt(list1.size());
			int r2 = r.nextInt(list2.size());
			MatchEntry me = new MatchEntry(list1.get(r1), list2.get(r2), false);
			result.add(me);
		}

		return result;
	}

	private Map<Integer, Integer> generateRandomPairs(int size) {
		int randomEntries = (int) (RANDOMPERCENTAGE * size);
		HashMap<Integer, Integer> map = new HashMap<>(randomEntries);
		Random r = new Random(System.currentTimeMillis());
		for (int i = 0; i < randomEntries; i++) {
			Integer k;
			do {
				k = r.nextInt(size);
			} while (map.containsKey(k));
			map.put(k, null);
		}

		Set<Integer> old = new HashSet<>(map.keySet());
		for (Integer k : map.keySet()) {
			Integer v;
			do {
				v = r.nextInt(size);
			} while (old.contains(v));
			map.put(k, v);
			old.add(v);
		}
		return map;
	}

	public static void main(String[] args) {
		new MatchCombinator().generateMatches();
	}

	private Collection<MatchEntry> generateVariationsMatch(Name name, Entry e) {
		List<MatchEntry> result = new ArrayList<>();

		// adds match with name and variation (misspellings)
		for (Name v : e.getVariations()) {
			result.add(new MatchEntry(name, v, true));
		}

		// add match with alternative names and translations
		for (Name n : e.getNames()) {
			if (!n.equals(name)) {
				result.add(new MatchEntry(name, n, true));
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
			m.marshal(dataset, new File(outputFile));
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
			FileReader fr = new FileReader(inputFile);
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

	public Dataset getDataset() {
		return dataset;
	}
	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}
}
