package it.unitn.disi.sweb.names.utils;

import it.unitn.disi.sweb.names.utils.bootstrap.DatabaseBootstrap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testApplicationContext.xml"})
public class TestDbBootstrap {

	@Autowired
	DatabaseBootstrap db;

	@Test
	public void nothing() {

	}

	 @Test
	public void testBootstrap() {
		try {
			 db.bootstrapAll();
//			db.bootstrapEtype();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
