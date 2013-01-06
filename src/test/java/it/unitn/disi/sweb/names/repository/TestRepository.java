package it.unitn.disi.sweb.names.repository;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({TestEntityDAO.class, TestEtypeDAO.class, TestFullNameDAO.class,
		TestIndividualNameDAO.class, TestNameElementDAO.class,
		TestNameStatisticDAO.class, TestNameTokenDAO.class,
		TestPrefixDAO.class, TestTriggerWordDAO.class,
		TestTriggerWordStatisticDAO.class, TestTriggerWordTokenDAO.class,
		TestTriggerWordTypeDAO.class, TestUsageStatisticDAO.class})
public class TestRepository {

}
