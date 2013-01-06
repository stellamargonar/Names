package it.unitn.disi.sweb.names.service;

import it.unitn.disi.sweb.names.model.EType;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testApplicationContext.xml"})
public class TestEtypeManager extends TestCase {

	@Autowired
	EtypeManager manager;

	@Override
	@Before
	public void setUp() throws Exception {
	}

	@Override
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testGetEtype() {
		EType etype = manager.getEtype(EtypeName.PERSON);
		assertEquals("Person", etype.getEtype());

		etype = manager.getEtype(EtypeName.LOCATION);
		assertEquals("Location", etype.getEtype());

		etype = manager.getEtype(EtypeName.ORGANIZATION);
		assertEquals("Organization", etype.getEtype());


		etype = manager.getEtype(null);
		assertNull(etype);
	}

}
