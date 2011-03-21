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


public class BestellungTest {
	private static final String PERSISTENCE_UNIT = "swe1Persistence";
		
	private static final Long IDBESTELLUNG_VORHANDEN = Long.valueOf(1); 

	private static final Bestellstatus STATUS_VORHANDEN = Bestellstatus.BESTELLT;
	private static final Bestellstatus STATUS_NICHT_VORHANDEN = Bestellstatus.VERSENDET;
	
	private static final Long BENUTZER_ID_VORHANDEN = Long.valueOf(1);
	private static final Long BENUTZER_ID_NICHT_VORHANDEN = Long.valueOf(99);
	
	private static final Long LAGER_ID_VORHANDEN = Long.valueOf(1);
	
	private static final Date NEU_DATUM = new Date(2010-12-11);
			
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
	//funktioniert
	@Test
	public void findBestellungByIdVorhanden()
	{
		final Long idBestellung = IDBESTELLUNG_VORHANDEN;
		
		final Bestellung bestellungen = em.find(Bestellung.class, idBestellung);
		assertThat(bestellungen.getIdbestellung(),is(idBestellung)); 
	}
	
	
	//funktioniert
	@Test
	public void findBestellungByNStatusVorhanden()
	{
		final Bestellstatus status = STATUS_VORHANDEN;
		final TypedQuery<Bestellung> query = em.createNamedQuery(Bestellung.FIND_BESTELLUNG_N_BY_STATUS, Bestellung.class);
		query.setParameter("bestellstatus", status);
		final List<Bestellung> bestellungen = query.getResultList();
		
		assertThat(bestellungen.isEmpty(), is(false));
		for (Bestellung b : bestellungen) 
		{
			assertThat(b.getBestellstatus(), is(status));
		}
	}
	
	//funktioniert
	@Test
	public void findBestellungByNStatusNichtVorhanden()
	{
		final Bestellstatus status = STATUS_NICHT_VORHANDEN;
		final TypedQuery<Bestellung> query = em.createNamedQuery(Bestellung.FIND_BESTELLUNG_N_BY_STATUS, Bestellung.class);
		query.setParameter("bestellstatus", status);
		final List<Bestellung> bestellungen = query.getResultList();
		
		assertThat(bestellungen.isEmpty(), is(true));
	}
	
	//funktioniert
	@Test
	public void findBestellungenByNBenutzerVorhanden()
	{
		final  Long idBenutzer = BENUTZER_ID_VORHANDEN ;
		final TypedQuery<Bestellung> query = em.createNamedQuery(Bestellung.FIND_BESTELLUNGEN_BY_BENUTZER_ID, Bestellung.class);
		query.setParameter("benutzer_idbenutzer", idBenutzer);
		final List<Bestellung> bestellungen = query.getResultList();
		
		assertThat(bestellungen.isEmpty(), is(false));
		for (Bestellung b : bestellungen) 
		{
			assertThat(b.getBenutzer().getIdBenutzer(), is(idBenutzer));	
		}
	}
	
	//funktioniert
	@Test
	public void findBestellungenByNBenutzerNichtVorhanden()
	{
		final  Long idBenutzer = BENUTZER_ID_NICHT_VORHANDEN ;
		final TypedQuery<Bestellung> query = em.createNamedQuery(Bestellung.FIND_BESTELLUNGEN_BY_BENUTZER_ID, Bestellung.class);
		query.setParameter("benutzer_idbenutzer", idBenutzer);
		final List<Bestellung> bestellungen = query.getResultList();
			
		assertThat(bestellungen.isEmpty(), is(true));
	}
	
	@Test
	public void createBestellung()
	{
		final Long idBenutzer = BENUTZER_ID_VORHANDEN ;
		final Long idLager = LAGER_ID_VORHANDEN;
		final Date datum = NEU_DATUM;
		AbstractBenutzer benutzer = em.find(AbstractBenutzer.class, idBenutzer);
		Lager lager = em.find(Lager.class, idLager);
		
		Bestellung newBestellung = new Bestellung();
		
		
		
		Bestellposition eins = new Bestellposition(); 
		eins.setMenge(new Integer(2));
		eins.setLagerArtikel(lager);
		
		
		ArrayList<Bestellposition> positionen = new ArrayList<Bestellposition>();
		positionen.add(eins);
		
		newBestellung.setBestellpositionen(positionen);
		newBestellung.setBestellstatus(Bestellstatus.BESTELLT);
		newBestellung.setZahlungsart(Zahlungsart.RECHNUNG);
		newBestellung.setErstellungsdatum(datum);
		newBestellung.setBenutzer(benutzer);
		
		benutzer.addBestellung(newBestellung);
		
		em.persist(newBestellung);
		
		final TypedQuery<Bestellung> query = em.createNamedQuery(Bestellung.FIND_BESTELLUNG_N_BY_ERSTELLUNGSDATUM, Bestellung.class);
		query.setParameter("erstellungsdatum", datum);
		final List<Bestellung> bestellungen = query.getResultList();
		
		assertThat(bestellungen.size(), is(1));
		
		assertThat(bestellungen.get(0).getErstellungsdatum(), is(newBestellung.getErstellungsdatum()));
	}
}
