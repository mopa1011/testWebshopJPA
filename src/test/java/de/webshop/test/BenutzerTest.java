package de.webshop.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.webshop.benutzerverwaltung.domain.AbstractBenutzer;
import de.webshop.benutzerverwaltung.domain.Rechnungsadresse;
import de.webshop.test.util.AbstractJunitTest;
import de.webshop.test.util.DbReload;

public class BenutzerTest extends AbstractJunitTest {

	private static final String NACHNAME_VORHANDEN = "Sauer";
	private static final String NACHNAME_NICHT_VORHANDEN = "NIX";
	private static final Long ID_VORHANDEN = Long.valueOf(1);
	private static final String EMAIL_VORHANDEN = "stephan_sauer@gmx.de";
	private static final String EMAIL_NICHT_VORHANDEN = "Nicht";
	private static final String PLZ_VORHANDEN = "673";
	
	private static final String NACHNAME_NEU = "Mustermann";
	private static final String VORNAME_NEU = "Max";
	private static final String EMAIL_NEU = "Max@Mustermann.de";
	private static final String PLZ_NEU = "12345";
	private static final String ORT_NEU = "Musterstadt";
	private static final String STRASSE_NEU = "Musterstr.";
	private static final String HAUSNR_NEU = "1";
	private static final boolean NEWSLETTER = false;
	
	@Test
	public void findBenutzerByID() {
		AbstractBenutzer b = em.find(AbstractBenutzer.class, new Long(1));
		assertThat(b.getIdBenutzer(), is(1L));
	}
	
	@Test
	public void findBenutzerByNachname() {
		final TypedQuery<AbstractBenutzer> query = em.createNamedQuery(AbstractBenutzer.FIND_BENUTZER_N_BY_NACHNAME, AbstractBenutzer.class);
		query.setParameter(AbstractBenutzer.PARAM_NACHNAME, NACHNAME_VORHANDEN);
		List<AbstractBenutzer> res = query.getResultList();
		for(AbstractBenutzer b : res) {
			assertThat(b.getNachname(), is(NACHNAME_VORHANDEN));
		}
	}
	
	@Test
	public void findBenutzerByNachnameNichtVorhanden() {
		final TypedQuery<AbstractBenutzer> query = em.createNamedQuery(AbstractBenutzer.FIND_BENUTZER_N_BY_NACHNAME, AbstractBenutzer.class);
		query.setParameter(AbstractBenutzer.PARAM_NACHNAME, NACHNAME_NICHT_VORHANDEN);
		List<AbstractBenutzer> res = query.getResultList();
		for(AbstractBenutzer b : res) {
			assertThat(b.getNachname(), is(nullValue()));
		}
	}
	
	@Test
	public void findBenutzerByPLZ() {
		final TypedQuery<AbstractBenutzer> query = em.createNamedQuery(AbstractBenutzer.FIND_BENUTZER_N_BY_PLZ, AbstractBenutzer.class);
		query.setParameter(AbstractBenutzer.PARAM_PLZ, "%"+PLZ_VORHANDEN+"%");
		List<AbstractBenutzer> res = query.getResultList();
		for(AbstractBenutzer b : res) {
			assertThat(b.getRechnungsadresse().getPlz().indexOf(PLZ_VORHANDEN)>-1, is(true));
		}
	}
	
	@Test
	public void createBenutzer() {
		AbstractBenutzer newBenutzer = new AbstractBenutzer();
		newBenutzer.setNachname(NACHNAME_NEU);
		newBenutzer.setVorname(VORNAME_NEU);
		newBenutzer.setBankleitzahl(null);
		newBenutzer.setEmail(EMAIL_NEU);
		newBenutzer.setErstellungsdatum(new Date());
		newBenutzer.setKontonummer(null);
		newBenutzer.setPasswort("FranzEder");
		newBenutzer.setNewsletter(NEWSLETTER);
		
		Rechnungsadresse rechnungsadresse = new Rechnungsadresse();
		rechnungsadresse.setStrasse(STRASSE_NEU);
		rechnungsadresse.setHausnummer(HAUSNR_NEU);
		rechnungsadresse.setOrt(ORT_NEU);
		rechnungsadresse.setPlz(PLZ_NEU);
		rechnungsadresse.setBenutzer(newBenutzer);
		
		newBenutzer.setRechnungsadresse(rechnungsadresse);
		
		em.persist(newBenutzer);
		
		final TypedQuery<AbstractBenutzer> query = em.createNamedQuery(AbstractBenutzer.FIND_BENUTZER_N_BY_NACHNAME, AbstractBenutzer.class);
		query.setParameter(AbstractBenutzer.PARAM_NACHNAME, NACHNAME_NEU);
		final List<AbstractBenutzer> benutzer = query.getResultList();
		
		assertThat(benutzer.size(), is(1));
		
		assertThat(benutzer.get(0).getNachname(), is(newBenutzer.getNachname()));
		assertThat(benutzer.get(0).getEmail(), is(newBenutzer.getEmail()));
	}
}
