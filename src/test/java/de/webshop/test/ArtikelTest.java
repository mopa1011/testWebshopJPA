package de.webshop.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
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

import de.webshop.artikelverwaltung.domain.Artikel;
import de.webshop.artikelverwaltung.domain.Kategorie;
import de.webshop.artikelverwaltung.domain.KategorieHasArtikel;
import de.webshop.lagerverwaltung.domain.Lager.Farbe;
import de.webshop.test.util.DbReload;

public class ArtikelTest 
{
	private static final String PERSISTENCE_UNIT = "swe1Persistence";

	private static final Long IDARTIKEL_VORHANDEN = Long.valueOf(1);
	private static final Long IDARTIKEL_NICHT_VORHANDEN = Long.valueOf(999);
	private static final String BEZEICHNUNG_VORHANDEN = "hose";
	private static final String BEZEICHNUNG_NICHT_VORHANDEN = "nasenfahrrad";
	private static final String KATEGORIE_VORHANDEN = "Mann";
	private static final String KATEGORIE_NICHT_VORHANDEN = "Hund";
	private static final Farbe FARBE_VORHANDEN = Farbe.BLAU;
	private static final String GROESSE_VORHANDEN = "L";
	private static final Integer BESTAND_VORHANDEN = 50;
	
	private static final Date NEU_DATUM = new Date(2010-12-11);
	private static final String NEU_BEZEICHNUNG = "Wifebeater";
	private static final Double NEU_PREIS = 11.99;
	private static final Boolean NEU_SORTIMENT = true;
	
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
	public void findArtikelByIdVorhanden() {
		final Long idartikel = IDARTIKEL_VORHANDEN;
		
		final TypedQuery<Artikel> query = em.createNamedQuery(Artikel.FIND_ARTIKEL_BY_ID, Artikel.class);
		query.setParameter("idartikel", idartikel);
		final List<Artikel> artikel = query.getResultList();
		
		assertThat(artikel.isEmpty(), is(false));
		for(Artikel a : artikel){
			assertThat(a.getIdArtikel(), is(idartikel));
		}
	}
	
	@Test
	public void FindArtikelByIdNichtVorhanden() {
		final Long idartikel = IDARTIKEL_NICHT_VORHANDEN;
		
		final TypedQuery<Artikel> query = em.createNamedQuery(Artikel.FIND_ARTIKEL_BY_ID, Artikel.class);
		query.setParameter("idartikel", idartikel);
		final List<Artikel> artikel = query.getResultList();
		
		assertThat(artikel.isEmpty(), is(true));
	}

	@Test
	public void FindArtikelByBezeichnungVorhanden() {
		final String bezeichnung = BEZEICHNUNG_VORHANDEN;
		
		final TypedQuery<Artikel> query = em.createNamedQuery(Artikel.FIND_ARTIKEL_BY_BEZEICHNUNG, Artikel.class);
		query.setParameter("bezeichnung", bezeichnung);
		final List<Artikel> artikel = query.getResultList();
		
		assertThat(artikel.isEmpty(), is(false));
		for(Artikel a : artikel){
			assertThat(a.getBezeichnung(), is(bezeichnung));
		}
	}
	
	@Test
	public void FindArtikelByBezeichnungNichtVorhanden() {
		final String bezeichnung = BEZEICHNUNG_NICHT_VORHANDEN;
		
		final TypedQuery<Artikel> query = em.createNamedQuery(Artikel.FIND_ARTIKEL_BY_BEZEICHNUNG, Artikel.class);
		query.setParameter("bezeichnung", bezeichnung);
		final List<Artikel> artikel = query.getResultList();
		
		assertThat(artikel.isEmpty(), is(true));
	}
	
	@Test
	public void FindArtikelByKategorieVorhanden() {
		final String kategorie = KATEGORIE_VORHANDEN;
		Boolean foundkategorie = false;
		
		final TypedQuery<Artikel> query = em.createNamedQuery(Artikel.FIND_ARTIKEL_BY_KATEGORIE, Artikel.class);
		query.setParameter("kategorie", kategorie);
		final List<Artikel> artikel = query.getResultList();
		
		assertThat(artikel.isEmpty(), is(false));
		for(Artikel a : artikel){
			for(KategorieHasArtikel k : a.getKategorien()){
				if(k.getKategorie().getBezeichnung() == kategorie)
					foundkategorie = true;						
			}
		assertThat(foundkategorie, is(true));
		foundkategorie = false;
		}
	}
	
	@Test
	public void FindArtikelByKategorieNichtVorhanden() {
		final String kategorie = KATEGORIE_NICHT_VORHANDEN;
		
		final TypedQuery<Artikel> query = em.createNamedQuery(Artikel.FIND_ARTIKEL_BY_KATEGORIE, Artikel.class);
		query.setParameter("kategorie", kategorie);
		final List<Artikel> artikel = query.getResultList();
		
		assertThat(artikel.isEmpty(), is(true));
	}
	
	@Test
	public void FindAnzahlArtikelAufLagerByIdVorhanden() {
		final Long idartikel = IDARTIKEL_VORHANDEN;
		final Farbe farbe = FARBE_VORHANDEN;
		final String groesse = GROESSE_VORHANDEN;
		final Integer bestand = BESTAND_VORHANDEN;
		
		final TypedQuery<Integer> query = em.createNamedQuery(Artikel.FIND_ANZAHL_ARTIKEL_AUF_LAGER_BY_ID, Integer.class );
		query.setParameter(Artikel.PARAM_ID, idartikel);
		query.setParameter(Artikel.PARAM_FARBE, farbe);
		query.setParameter(Artikel.PARAM_GROESSE, groesse);
		
		final Integer lagerbestand = query.getSingleResult();
		assertThat(lagerbestand, is(bestand));

	}
	
	@Test
	public void CreateArtikel(){
	Artikel artikel = new Artikel();
	
	artikel.setErstellungsdatum(NEU_DATUM);
	artikel.setBezeichnung(NEU_BEZEICHNUNG);
	artikel.setPreis(NEU_PREIS);
	artikel.setImsortiment(NEU_SORTIMENT);
	Long id1 = Long.valueOf(1);
	Long id2 = Long.valueOf(2);
	
	final Kategorie kategorie1 = em.find(Kategorie.class, id1);
	final Kategorie kategorie2 = em.find(Kategorie.class, id2);
		
	KategorieHasArtikel kha1 = new KategorieHasArtikel();
	kha1.setKategorie(kategorie1);
	artikel.addKategorieHasArtikel(kha1);
	
	KategorieHasArtikel kha2 = new KategorieHasArtikel();
	kha2.setKategorie(kategorie2);
	artikel.addKategorieHasArtikel(kha2);
	
	em.persist(artikel);
	
	final TypedQuery<Artikel> query = em.createNamedQuery(Artikel.FIND_ARTIKEL_BY_BEZEICHNUNG, Artikel.class);
	query.setParameter(Artikel.PARAM_BEZEICHNUNG, NEU_BEZEICHNUNG);
	
	final List<Artikel> artikel_gelesen  = query.getResultList();
	assertThat(artikel_gelesen.size(), is(1));
	assertThat(artikel_gelesen.get(0).getIdArtikel().longValue() > 0, is(true));
	
	final List<KategorieHasArtikel> kats = artikel_gelesen.get(0).getKategorien();
	
	assertThat(kats.get(0).getKategorie(), is(kategorie1));
	assertThat(kats.get(1).getKategorie(), is(kategorie2));

			
	
	}
	
	
	
}

