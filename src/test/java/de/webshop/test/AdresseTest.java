package de.webshop.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.webshop.test.util.DbReload;
import de.webshop.benutzerverwaltung.domain.AbstractAdresse;
import de.webshop.benutzerverwaltung.domain.AbstractBenutzer;
import de.webshop.benutzerverwaltung.domain.Lieferadresse;
import de.webshop.benutzerverwaltung.domain.Rechnungsadresse;

public class AdresseTest {

	private static final String PERSISTENCE_UNIT = "swe1Persistence";

	//private static final String ID_VORHANDEN = "10";
	//private static final String ID_NICHT_VORHANDEN = "0";
	private static final Long ID_VORHANDEN = Long.valueOf(1); //id direkt eintragen??
	private static final Long ID_USER_VORHANDEN = Long.valueOf(1); //id direkt eintragen??
	private static final Long ID_USER_NICHT_VORHANDEN = Long.valueOf(0); //id direkt eintragen??
	private static final String PLZ_NEU = "66999";
	private static final String ORT_NEU = "Hinterweidenthal";
	private static final String STRASSE_NEU = "Hauptstrasse";
	private static final String HAUSNR_NEU = "20";
	
	
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
	
	//@Ignore
	@Test
	public void findAdresseByIdVorhanden() {
		
		//Angepasster Code aus dem Beispiel
		final Long id = ID_VORHANDEN;
		
		final AbstractAdresse adresse = em.find(AbstractAdresse.class, id);
		
		assertThat(adresse.getIdadresse(), is(id));

		//Sollte auch funktionieren.
		/*final String idAdresse = ID_VORHANDEN;
		
		final TypedQuery<AbstractAdresse> query = em.createNamedQuery(AbstractAdresse.FIND_ADRESSE_BY_ID, AbstractAdresse.class);
		query.setParameter("idadresse", idAdresse);
		AbstractAdresse adresse = getSingleResult();
		
		assertThat(adresse.getIdadresse(), is(idAdresse));
		*/
	}
	
	//@Ignore
	@Test
	public void findAdresseByUserIdVorhanden() {

		final Long idUser = ID_USER_VORHANDEN;
		
		final TypedQuery<AbstractAdresse> query = em.createNamedQuery(AbstractAdresse.FIND_ADRESSE_BY_USER_ID, AbstractAdresse.class);
		query.setParameter("benutzer_idbenutzer", idUser);
		//AbstractAdresse adresse = query.getSingleResult();
		final List<AbstractAdresse> adresse = query.getResultList();
		//assertThat(adresse.getBenutzer_idbenutzer().getIdBenutzer(), is(idUser));
		assertThat(adresse.isEmpty(),is(false));
	}
	
	//@Ignore
	@Test
	public void findAdresseByUserIdNichtVorhanden() {
		
		final Long idUser = ID_USER_NICHT_VORHANDEN;
					
		final TypedQuery<AbstractAdresse> query = em.createNamedQuery(AbstractAdresse.FIND_ADRESSE_BY_USER_ID, AbstractAdresse.class);
		query.setParameter("benutzer_idbenutzer", idUser);
		
		thrown.expect(NoResultException.class);
		query.getSingleResult();
	}

	// CreateMethode nur für Lieferadresse
	//@Ignore
	@Test
	public void createAdresse(){
		final Lieferadresse adresse = new Lieferadresse();
		adresse.setPlz(PLZ_NEU);
		adresse.setOrt(ORT_NEU);
		adresse.setStrasse(STRASSE_NEU);
		adresse.setHausnummer(HAUSNR_NEU);
		
		
		final Long idBenutzer = ID_USER_VORHANDEN;
	    final AbstractBenutzer benutzer = em.find(AbstractBenutzer.class, idBenutzer);
	    adresse.setVorname(benutzer.getVorname());
	    adresse.setName(benutzer.getNachname());
	    benutzer.setLieferadresse((Lieferadresse) adresse);
	    
	    adresse.setBenutzer(benutzer);
		em.persist(adresse);

		//kann ich hier auf den Benutzer Test zugreifen und einem Benutzer diese Adresse hinzufï¿½gen???
	}

}
