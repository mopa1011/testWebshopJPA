package de.webshop.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.webshop.artikelverwaltung.domain.Artikel;
import de.webshop.artikelverwaltung.domain.Produktbewertung;
import de.webshop.benutzerverwaltung.domain.AbstractBenutzer;
import de.webshop.test.util.DbReload;

public class ProduktbewertungTest {

	private static final String PERSISTENCE_UNIT = "swe1Persistence";
	
	private static final Long ID_VORHANDEN = Long.valueOf(1);
	private static final String KOMMENTAR_VORHANDEN = "%OK%";
	private static final String TEST_KOMMENTAR = "AbCdEfGhI 123456";
	private static final Integer BEWERT = new Integer(3);
	private static final Long ARTIKEL_ID = Long.valueOf(1);
	private static final Long BENUTZER_ID= Long.valueOf(3);
	
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
	public void findBewertungByBenutzerIDVorhanden(){
		
		final Long idbenutzer = ID_VORHANDEN;
		
		final TypedQuery<Produktbewertung> query = em.createNamedQuery(Produktbewertung.FIND_BEWERTUNGEN_BY_BENUTZER_ID, Produktbewertung.class);
		query.setParameter(Produktbewertung.PARAM_BENUTZER_ID, idbenutzer);
		final List<Produktbewertung> bewertung = query.getResultList();
		
		assertThat(bewertung.isEmpty(), is(false));
		
	}
	
	@Test
	public void findBewertungMitKommentar(){
		final TypedQuery<Produktbewertung> query = em.createNamedQuery(Produktbewertung.FIND_BEWERTUNGEN_BY_KOMMENTAR_EXISTS, Produktbewertung.class);
		final List<Produktbewertung> bewertung = query.getResultList();
		
		assertThat(bewertung.isEmpty(),is(false));
	}
	
	@Test
	public void findBewertungByKommentar(){
		final String kommentar = KOMMENTAR_VORHANDEN;
		
		final TypedQuery<Produktbewertung> query = em.createNamedQuery(Produktbewertung.FIND_BEWERTUNGEN_BY_KOMMENTAR,Produktbewertung.class);
		query.setParameter(Produktbewertung.PARAM_KOMMENTAR, kommentar);
		final List<Produktbewertung> bewertung = query.getResultList();
		
		assertThat(bewertung.isEmpty(),is(false));
		
	}
	
	@Test
	public void createBewertung(){
		
		final Long idBenutzer = BENUTZER_ID;
	    final AbstractBenutzer benutzer = em.find(AbstractBenutzer.class, idBenutzer);
		assertThat(benutzer.getIdBenutzer(),is(idBenutzer));

		
		final Long idArtikel = ARTIKEL_ID;
		final Artikel artikel = em.find(Artikel.class, idArtikel);
		assertThat(artikel.getIdArtikel(),is(idArtikel));
		
		final Produktbewertung bewertung = new Produktbewertung();
		bewertung.setArtikel(artikel);
		bewertung.setBenutzer(benutzer);
		bewertung.setBewertung(BEWERT);
		bewertung.setKommentar(TEST_KOMMENTAR);
		bewertung.setErstellungsdatum(new Date());
		
		assertThat(bewertung.getArtikel(),is(artikel));
		assertThat(bewertung.getBenutzer(),is(benutzer));
		
		benutzer.addProduktbewertung(bewertung);
		artikel.addProduktbewertung(bewertung);
		
		em.persist(bewertung);
		
		final TypedQuery<Produktbewertung> query = em.createNamedQuery(Produktbewertung.FIND_BEWERTUNGEN_BY_KOMMENTAR, Produktbewertung.class);
		query.setParameter(Produktbewertung.PARAM_KOMMENTAR, TEST_KOMMENTAR);
		final Produktbewertung bewertungDB = query.getSingleResult();
		
		assertThat(bewertungDB.getKommentar(), is(TEST_KOMMENTAR));	
	}
}
