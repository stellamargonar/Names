package it.unitn.disi.sweb.names.utils.bootstrap;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.NameElement;
import it.unitn.disi.sweb.names.model.TriggerWord;
import it.unitn.disi.sweb.names.model.TriggerWordType;
import it.unitn.disi.sweb.names.repository.ETypeDAO;
import it.unitn.disi.sweb.names.repository.NameElementDAO;
import it.unitn.disi.sweb.names.repository.TriggerWordDAO;
import it.unitn.disi.sweb.names.repository.TriggerWordTypeDAO;
import it.unitn.disi.sweb.names.service.EtypeManager;
import it.unitn.disi.sweb.names.service.EtypeName;
import it.unitn.disi.sweb.names.utils.StringCompareUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("dbBootstrap")
public class DatabaseBootstrap {

	private TriggerWordTypeDAO twtDao;

	private TriggerWordDAO twDao;

	private ETypeDAO etypeDao;
	private NameElementDAO nameDao;
	private EtypeManager etypeManager;

	public DatabaseBootstrap() {
	}

	void storePersonTitles(String fileName) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line = null;

		TriggerWordType title = twtDao.findByNameEType("Title",
				etypeManager.getEtype(EtypeName.PERSON));

		while ((line = reader.readLine()) != null) {
			String[] token = line.split("\t");
			List<TriggerWord> tws = new ArrayList<>();
			for (String s : token) {
				TriggerWord t = new TriggerWord(s, title);
				t.setnGramCode(StringCompareUtils.computeNGram(s));
				t = twDao.save(t);
				tws.add(t);
			}

			if (tws.size() > 1) {
				for (int i = 0; i < tws.size(); i++) {
					for (int j = 0; j < tws.size(); j++) {
						if (i != j) {
							tws.get(i).addVariation(tws.get(j));
						}
						tws.set(i, twDao.update(tws.get(i)));
					}
				}
			}
		}
		reader.close();
	}

	void storeEtypeList(String fileName) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line = null;
		while ((line = reader.readLine()) != null) {
			EType e = new EType();
			e.setEtype(line);
			etypeDao.save(e);
		}
		reader.close();
	}
	void storeNameFieldList(String fileName) throws IOException {
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

	void storeTriggerWordTypeList(String fileName) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line = null;
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

	void storeToponymList(String fileName) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line = null;
		TriggerWordType top = twtDao.findByNameEType("Toponym",
				etypeManager.getEtype(EtypeName.LOCATION));
		while ((line = reader.readLine()) != null) {
			String[] token = line.split("\t");

			TriggerWord t = new TriggerWord(token[0], top);
			t.setnGramCode(StringCompareUtils.computeNGram(token[0]));
			t = twDao.save(t);
			if (token.length > 1) {
				TriggerWord abbr = new TriggerWord(token[1], top);
				abbr.setnGramCode(StringCompareUtils.computeNGram(token[1]));
				abbr = twDao.save(abbr);

				t.addVariation(abbr);
				abbr.addVariation(t);
				twDao.update(t);
				twDao.update(abbr);
			}

		}
		reader.close();
	}
	public void bootstrapAll() throws IOException {

		storeEtypeList("src/main/resources/INIT-DATA/EtypeList");

		storeNameFieldList("src/main/resources/INIT-DATA/NameElementList");

		storeTriggerWordTypeList("src/main/resources/INIT-DATA/TriggerWordTypeList");

		storeToponymList("src/main/resources/INIT-DATA/ToponymList");

		storePersonTitles("src/main/resources/INIT-DATA/PersonTitles");
	}

	public void bootstrapEtype() {
		try {
			storeEtypeList("src/main/resources/INIT-DATA/EtypeList");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void bootstrapNameElement() {
		try {
			storeNameFieldList("src/main/resources/INIT-DATA/NameElementList");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void bootstrapTriggerType() {
		try {
			storeTriggerWordTypeList("src/main/resources/INIT-DATA/TriggerWordTypeList");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void bootstrapToponym() {
		try {
			storeToponymList("src/main/resources/INIT-DATA/ToponymList");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Autowired
	public void setTwDao(TriggerWordDAO twDao) {
		this.twDao = twDao;
	}

	@Autowired
	public void setTwtDao(TriggerWordTypeDAO twDao) {
		twtDao = twDao;
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
	public void setEtypeMaanger(EtypeManager etypeMaanger) {
		etypeManager = etypeMaanger;
	}

}
