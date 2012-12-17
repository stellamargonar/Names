package it.unitn.disi.sweb.names.utils.bootstrap;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.NameElement;
import it.unitn.disi.sweb.names.model.TriggerWordType;
import it.unitn.disi.sweb.names.repository.ETypeDAO;
import it.unitn.disi.sweb.names.repository.NameElementDAO;
import it.unitn.disi.sweb.names.repository.TriggerWordTypeDAO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("dbBootstrap")
public class DatabaseBootstrap {

	private NameElementDAO nameDao;
	private TriggerWordTypeDAO twDao;
	private ETypeDAO etypeDao;

	public DatabaseBootstrap() {
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
	public void setTwDao(TriggerWordTypeDAO twDao) {
		this.twDao = twDao;
	}

	private List<EType> getEtypeList(String fileName) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line = null;
		List<EType> list = new ArrayList<EType>();
		while ((line = reader.readLine()) != null) {
			EType e = new EType();
			e.setEtype(line);
			list.add(e);
		}
		reader.close();

		return list;
	}

	private List<NameElement> getNameFieldList(String fileName)
			throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line = null;
		List<NameElement> list = new ArrayList<NameElement>();
		while ((line = reader.readLine()) != null) {
			String[] token = line.split("\t");

			System.out.println("etype:" + token[1]);
			System.out.println("name element:" + token[0]);

			EType e = etypeDao.findByName(token[1]);
			NameElement ne = new NameElement();
			ne.setElementName(token[0]);
			ne.setEtype(e);

			list.add(ne);
		}
		reader.close();
		return list;
	}

	private List<TriggerWordType> getTriggerWordTypeList(String fileName)
			throws IOException {
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

			list.add(t);
		}
		reader.close();
		return list;
	}

	public void bootstrap() throws IOException {
		etypeDao.deleteAll();
		nameDao.deleteAll();
		twDao.deleteAll();

		List<EType> etypeList = getEtypeList("src/main/resources/INIT-DATA/EtypeList");
		for (EType e : etypeList)
			etypeDao.save(e);

		List<NameElement> fieldList = getNameFieldList("src/main/resources/INIT-DATA/NameElementList");
		for (NameElement ne : fieldList)
			nameDao.save(ne);

		List<TriggerWordType> twlist = getTriggerWordTypeList("src/main/resources/INIT-DATA/TriggerWordTypeList");
		for (TriggerWordType t : twlist)
			twDao.save(t);

		System.out.println(etypeDao.findAll());
		System.out.println(nameDao.findAll());
		System.out.println(twDao.findAll());
	}

}
