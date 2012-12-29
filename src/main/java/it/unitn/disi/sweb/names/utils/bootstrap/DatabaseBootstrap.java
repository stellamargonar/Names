package it.unitn.disi.sweb.names.utils.bootstrap;

import it.unitn.disi.sweb.names.model.TriggerWord;
import it.unitn.disi.sweb.names.model.TriggerWordType;
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

	private TriggerWordTypeDAO twtDao;

	private TriggerWordDAO twDao;

	public DatabaseBootstrap() {
	}

	private void storePersonTitles(String fileName) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line = null;

		TriggerWordType title = this.twtDao.findByName("Title");

		while ((line = reader.readLine()) != null) {
			String[] token = line.split("\t");
			List<TriggerWord> tws = new ArrayList<>();
			for (String s : token) {
				TriggerWord t = new TriggerWord(s, title);
				tws.add(t);
				this.twDao.save(t);
			}

			if (tws.size() > 1) {
				TriggerWord original = tws.get(0);
				Set<TriggerWord> var = new HashSet<>();
				var.remove(original);
				original.setVariations(var);

				this.twDao.update(original);
			}
		}
		reader.close();
	}

	public void bootstrap() throws IOException {
		// init();

		// storeEtypeList("src/main/resources/INIT-DATA/EtypeList");
		//
		// storeNameFieldList("src/main/resources/INIT-DATA/NameElementList");
		//
		// storeTriggerWordTypeList("src/main/resources/INIT-DATA/TriggerWordTypeList");
		//
		// storeToponymList("src/main/resources/INIT-DATA/ToponymList");

		storePersonTitles("src/main/resources/INIT-DATA/PersonTitles");
	}

	@Autowired
	public void setTwDao(TriggerWordDAO twDao) {
		this.twDao = twDao;
	}

	@Autowired
	public void setTwtDao(TriggerWordTypeDAO twDao) {
		this.twtDao = twDao;
	}
}
