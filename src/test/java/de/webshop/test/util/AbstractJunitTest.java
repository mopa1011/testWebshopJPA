package de.webshop.test.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

public abstract class AbstractJunitTest {
	
	protected static final String PERSISTENCE_UNIT = "swe1Persistence";
	
	protected static EntityManagerFactory emf;
	protected EntityManager em;
	protected EntityTransaction trans;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@BeforeClass
	public static void init() throws Exception {
		emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
		DbReload.reload();
	}
	
	@AfterClass
	public static void shutdown() {
		if (emf != null) {
			emf.close();
		}
	}
	
	@Before
	public void beginTransaction() {
		em = emf.createEntityManager();
		trans = em.getTransaction();
		trans.begin();
	}

	@After
	public void endTransaction() {
		if (trans != null) {
			trans.commit();
			trans = null;
		}
		
		if (em != null) {
			em.close();
			em = null;
		}
	}
}
