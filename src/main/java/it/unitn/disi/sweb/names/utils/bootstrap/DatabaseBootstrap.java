package it.unitn.disi.sweb.names.utils.bootstrap;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.NameElement;
import it.unitn.disi.sweb.names.model.TriggerWord;
import it.unitn.disi.sweb.names.model.TriggerWordType;
import it.unitn.disi.sweb.names.repository.ETypeDAO;
import it.unitn.disi.sweb.names.repository.NameElementDAO;
import it.unitn.disi.sweb.names.repository.TriggerWordDAO;
import it.unitn.disi.sweb.names.repository.TriggerWordTypeDAO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("dbBootstrap")
public class DatabaseBootstrap {

	private NameElementDAO nameDao;
	private TriggerWordTypeDAO twtDao;
	private ETypeDAO etypeDao;
	private TriggerWordDAO twDao;

	public DatabaseBootstrap() {
	}

	private void storeEtypeList(String fileName) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line = null;
		while ((line = reader.readLine()) != null) {
			EType e = new EType();
			e.setEtype(line);
			etypeDao.save(e);
		}
		reader.close();
	}

	private void storeNameFieldList(String fileName) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line = null;
		while ((line = reader.readLine()) != null) {
			String[] token = line.split("\t");

			EType e = etypeDao.findByName(token[1]);
			NameElement ne = new NameElement();
			ne.setElementName(token[0]);
			ne.setEtype(e);

			nameDao.save(ne);
		}
		reader.close();
	}

	private void storeTriggerWordTypeList(String fileName) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line = null;
		List<TriggerWordType> list = new ArrayList<TriggerWordType>();
		while ((line = reader.readLine()) != null) {
			String[] token = line.split("\t");

			EType e = etypeDao.findByName(token[1]);
			TriggerWordType t = new TriggerWordType();
			t.setType(token[0]);
			t.seteType(e);
			t.setComparable(token[2].equals("0") ? false : true);

			twtDao.save(t);
		}
		reader.close();
	}

	private void storeToponymList(String fileName) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line = null;
		TriggerWordType top = twtDao.findByName("Toponym");
		while ((line = reader.readLine()) != null) {
			String[] token = line.split("\t");

			TriggerWord t = new TriggerWord(token[0], top);
			TriggerWord abbr = new TriggerWord(token[1], top);

			t = twDao.save(t);
			abbr = twDao.save(abbr);

			t.addVariation(abbr);
			abbr.addVariation(t);

			twDao.update(t);
			twDao.update(abbr);
		}
		reader.close();
	}

	private void storePersonTitles(String fileName) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line = null;

		TriggerWordType title = twtDao.findByName("Title");

		while ((line = reader.readLine()) != null) {
			String[] token = line.split("\t");
			List<TriggerWord> tws = new ArrayList<>();
			for (String s : token) {
				TriggerWord t = new TriggerWord(s, title);
				tws.add(t);
				twDao.save(t);
			}

			if (tws.size() > 1) {
				TriggerWord original = tws.get(0);
				Set<TriggerWord> var = new HashSet<>();
				var.remove(original);
				original.setVariations(var);

				twDao.update(original);
			}
		}
		reader.close();
	}

	public void bootstrap() throws IOException {
//		init();

//		storeEtypeList("src/main/resources/INIT-DATA/EtypeList");
//
//		storeNameFieldList("src/main/resources/INIT-DATA/NameElementList");
//
//		storeTriggerWordTypeList("src/main/resources/INIT-DATA/TriggerWordTypeList");
//
//		storeToponymList("src/main/resources/INIT-DATA/ToponymList");

		storePersonTitles("src/main/resources/INIT-DATA/PersonTitles");
	}

	private void init() {
		etypeDao.deleteAll();
		nameDao.deleteAll();
		twtDao.deleteAll();
		twDao.deleteAll();
	}

	@Autowired
	public void setTwDao(TriggerWordDAO twDao) {
		this.twDao = twDao;
	}

	@Autowired
	public void setEtypeDao(ETypeDAO etypeDao) {
		this.etypeDao = etypeDao;
	}

	@Autowired
	public void setNameDao(NameElementDAO nameDao) {
		this.nameDao = nameDao;
	}

	@Autowired
	public void setTwtDao(TriggerWordTypeDAO twDao) {
		this.twtDao = twDao;
	}
}
