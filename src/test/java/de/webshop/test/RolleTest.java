package de.webshop.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Type;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.webshop.benutzerverwaltung.domain.Rolle;
import de.webshop.test.util.DbReload;
import de.webshop.util.NotFoundException;

public class RolleTest {

	private static final String PERSISTENCE_UNIT = "swe1Persistence";
	
	private static final Long ID_VORHANDEN = Long.valueOf(1);
	private static final Long ID_NICHT_VORHANDEN = Long.valueOf(5);
	
	private static EntityManagerFactory emf;
	private EntityManager em;
	private EntityTransaction trans;
	
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
	
	@Test
	public void findRolleById(){
		final Long idRolle = ID_VORHANDEN;
		
		//final Rolle rolle = em.find(Rolle.class, idRolle);
		final TypedQuery<Rolle> query = em.createNamedQuery(Rolle.FIND_ROLLE_BY_ID, Rolle.class);
		query.setParameter("idRolle", idRolle);
		
		final Rolle rolle = query.getSingleResult();
		
		assertThat(rolle.getIdrolle(), is(idRolle));
	}
	
	
	@Test
	public void findRolleByIDNOT(){
		final Long idRolle = ID_NICHT_VORHANDEN;
		
		final TypedQuery<Rolle> query = em.createNamedQuery(Rolle.FIND_ROLLE_BY_ID, Rolle.class);
		query.setParameter("idRolle", idRolle);
		
		thrown.expect(NoResultException.class);
		query.getSingleResult();
		
		
	}
	
	
}