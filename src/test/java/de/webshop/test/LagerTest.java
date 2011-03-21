package de.webshop.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.webshop.benutzerverwaltung.domain.AbstractBenutzer;
import de.webshop.bestellungsverwaltung.domain.Bestellposition;
import de.webshop.bestellungsverwaltung.domain.Bestellung;
import de.webshop.bestellungsverwaltung.domain.Bestellung.Bestellstatus;
import de.webshop.bestellungsverwaltung.domain.Bestellung.Zahlungsart;
import de.webshop.lagerverwaltung.domain.Lager;
import de.webshop.test.util.DbReload;

public class LagerTest {

	private static final String PERSISTENCE_UNIT = "swe1Persistence";
	
	private static final Long IDLAGER_VORHANDEN = Long.valueOf(1); 
	private static final Long IDARTIKEL_VORHANDEN = Long.valueOf(1); 
	
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
	public void findLagerByIdVorhanden()
	{
		final Long idLager = IDLAGER_VORHANDEN;
		
		final Lager lager = em.find(Lager.class, idLager);
		assertThat(lager.getIdlager(),is(idLager)); 
	}
	
	@Test
	public void findLagerByNArtikelVorhanden()
	{
		final  Long idArtikel = IDARTIKEL_VORHANDEN ;
		final TypedQuery<Lager> query = em.createNamedQuery(Lager.FIND_LAGER_BY_ARTIKEL_ID, Lager.class);
		query.setParameter("artikel_idartikel", idArtikel);
		final List<Lager> lagerN = query.getResultList();
		
		assertThat(lagerN.isEmpty(), is(false));
		for (Lager l : lagerN) 
		{
			assertThat(l.getArtikel().getIdArtikel(), is(idArtikel));	
		}
	}
}
