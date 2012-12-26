package it.unitn.disi.sweb.names.service.impl;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.IndividualName;
import it.unitn.disi.sweb.names.model.NameElement;
import it.unitn.disi.sweb.names.model.NameToken;
import it.unitn.disi.sweb.names.model.NamedEntity;
import it.unitn.disi.sweb.names.model.TriggerWord;
import it.unitn.disi.sweb.names.model.TriggerWordToken;
import it.unitn.disi.sweb.names.repository.EntityDAO;
import it.unitn.disi.sweb.names.repository.FullNameDAO;
import it.unitn.disi.sweb.names.repository.IndividualNameDAO;
import it.unitn.disi.sweb.names.repository.NameElementDAO;
import it.unitn.disi.sweb.names.repository.NameTokenDAO;
import it.unitn.disi.sweb.names.repository.TriggerWordDAO;
import it.unitn.disi.sweb.names.service.NameManager;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("nameManager")
public class NameManagerImpl implements NameManager {

	@Autowired
	FullNameDAO fullnameDao;
	@Autowired
	IndividualNameDAO nameDao;
	@Autowired
	TriggerWordDAO twDao;
	@Autowired
	NameElementDAO nameElementDao;
	@Autowired
	EntityDAO entityDao;
	@Autowired
	NameTokenDAO nameTokenDao;

	@Override
	public FullName createFullName(String name, NamedEntity en) {
		if (en == null || name == null || name.equals(""))
			return null;

		List<FullName> foundList = fullnameDao.findByNameToCompare(name);

		for (FullName f : foundList) {
			if (en.equals(f.getEntity()))
				return f;
		}
		FullName fullname = new FullName();
		fullname.setName(name);
		fullname.setEntity(en);
//		en.addFullName(fullname);
		
		fullname = parse(fullname, en.getEType());
		fullname.setNameToCompare(getNameToCompare(fullname));
		fullname.setNameNormalized(getNameNormalized(fullname));

		FullName returned = fullnameDao.update(fullname);

		return returned;
	}

	/**
	 * generate the nameToCompare field for the FullName. The string is compute
	 * deleting the tokens (some trigger words) that should not be considered in
	 * the comparison (see TriggerWordType)
	 * 
	 * @param fullname
	 * @return
	 */
	private String getNameToCompare(FullName fullname) {
		return getNameModified(fullname, false, true);
	}

	/**
	 * generate the normalized field for the FullName. In this implementation
	 * the normalized name cosists in the tokens in lexical graphic order
	 * 
	 * @param fullname
	 * @return
	 */
	private String getNameNormalized(FullName fullname) {
		return getNameModified(fullname, true, false);
	}

	private String getNameModified(FullName fullname, boolean normalized,
			boolean compare) {
		String name = "";
		Collection a = fullname.getNameTokens();
		Collection b = fullname.getTriggerWordTokens();
		int max = 0;
		if (a != null) {
			max = a.size();
			if (b != null)
				max += b.size();
		}
		String[] tokenArray = new String[max];
		for (NameToken n : fullname.getNameTokens()) {
			tokenArray[n.getPosition()] = n.getIndividualName().getName();
		}
		if (fullname.getTriggerWordTokens() != null)
			for (TriggerWordToken t : fullname.getTriggerWordTokens())
				if ((compare && t.getTriggerWord().getType().isComparable())
						|| !compare)
					tokenArray[t.getPosition()] = t.getTriggerWord()
							.getTriggerWord();

		List<String> tokens = Arrays.asList(tokenArray);

		if (normalized)
			Collections.sort(tokens);

		for (String s : tokens)
			if (s != null)
				name += s + " ";

		return name;
	}

	protected FullName parse(FullName fullname, EType eType) {
		String name = fullname.getName();

		String[] tokens = name.split(" ");
		int position = 0;
		for (String s : tokens) {
			if (!s.equals(" ")) {
				// check if it is a known name
				List<IndividualName> listName = nameDao.findByNameEtype(s,
						eType);
				if (listName != null && listName.size() > 0) {
					NameToken nt = new NameToken();
					nt.setFullName(fullname);
					nt.setIndividualName(listName.get(0));
					nt.setPosition(position);
					fullname.addNameToken(nt);
				} else {
					// check if it is a known triggerword
					List<TriggerWord> listTW = twDao.findByTriggerWordEtype(s,
							eType);
					System.out.println(s + " " + listTW);
					if (listTW != null && listTW.size() > 0) {
						TriggerWordToken twt = new TriggerWordToken();
						twt.setFullName(fullname);
						twt.setPosition(position);
						twt.setTriggerWord(listTW.get(0));
						fullname.addTriggerWordToken(twt);
					} else {
						// add not existing token
						NameToken nt = new NameToken();
						nt.setIndividualName(addNewIndividualName(s, eType,
								position));
						nt.setFullName(fullname);
						nt.setPosition(position);
						fullname.addNameToken(nt);
					}
				}
				position++;
			}
		}

		/* DEBUG */
		// System.out.println("Name Created: ");
		// for (NameToken n : fullname.getNameTokens())
		// System.out.println(n.getIndividualName().getNameElement()
		// .getElementName()
		// + ": " + n.getIndividualName().getName());
		// if (fullname.getTriggerWordTokens() != null)
		// for (TriggerWordToken t : fullname.getTriggerWordTokens())
		// System.out.println(t.getTriggerWord().getType().getType()
		// + ": " + t.getTriggerWord().getTriggerWord());

		return fullname;
	}

	private IndividualName addNewIndividualName(String s, EType eType,
			int position) {
		IndividualName name = new IndividualName();
		name.setName(s);
		name.setNameElement(getNewNameElement(eType, position));
		nameDao.save(name);
		return name;
	}

	private NameElement getNewNameElement(EType eType, int position) {
		switch (eType.getEtype()) {
		case "Location":
		case "Organization":
			return nameElementDao.findByNameEType("ProperNoun", eType);
		case "Person":
			switch (position) {
			case 0:
				return nameElementDao.findByNameEType("GivenName", eType);
			case 2:
				return nameElementDao.findByNameEType("MiddleName", eType);
			default:
				return nameElementDao.findByNameEType("FamilyName", eType);
			}
		default:
			return null;
		}
	}

	@Override
	public void createIndividualName(String string, NameElement el) {
		IndividualName name = new IndividualName();
		name.setName(string);
		name.setNameElement(el);
		nameDao.save(name);

		NameToken nt = new NameToken();
		nt.setFullName(fullnameDao.findById(1650));
		nt.setIndividualName(name);
		nt.setPosition(0);
		nameTokenDao.save(nt);

	}

}
