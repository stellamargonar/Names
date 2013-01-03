package it.unitn.disi.sweb.names.repository;

import junit.framework.TestSuite;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({TestEntityDAO.class, TestEtypeDAO.class,
		TestIndividualNameDAO.class})
public class TestRepository {

	public static Test suite() {
		TestSuite suite = new TestSuite("Tutti i test per it.babel.test");

		suite.addTestSuite(TestEntityDAO.class);
		suite.addTestSuite(TestEtypeDAO.class);
		suite.addTestSuite(TestIndividualNameDAO.class);

		return (Test) suite;
	}

}
