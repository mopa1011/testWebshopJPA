package de.webshop.test.util;

import static org.dbunit.operation.DatabaseOperation.CLEAN_INSERT;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.hsqldb.HsqldbConnection;
import org.dbunit.ext.mssql.InsertIdentityOperation;
import org.dbunit.ext.mysql.MySqlConnection;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;


public final class DbReload {
	private static boolean reloaded = false;
	
	private static final String PROPS_FILE = "db.properties";
	
	private static final String URL;
	private static final String URL_DEFAULT = "jdbc:postgresql:jbossdb";
	
	private static final String USERNAME;
	private static final String USERNAME_DEFAULT = "webshop";
	
	private static final String PASSWORD;
	
	private static final String XML_DATASET;
	private static final String XML_DATASET_DEFAULT ="db.xml";

	private static final String SCHEMA;                          // nicht benoetigt fuer MySQL
	private static final String SCHEMA_DEFAULT = USERNAME_DEFAULT;
	
	static {
		// Properties-Datei einlesen
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		final InputStream inputStream = classLoader.getResourceAsStream(PROPS_FILE);
		Properties props = new Properties();
		try {
			props.load(inputStream);
		}
		catch (IOException e) {
			throw new IllegalStateException(e);
		}
		finally {
			try {
				if (inputStream != null)
					inputStream.close();
			}
			catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
		
		// Properties fuer DB-Connection einlesen
		URL = props.getProperty("url", URL_DEFAULT);
		SCHEMA = props.getProperty("schema", SCHEMA_DEFAULT);
		USERNAME = props.getProperty("username", USERNAME_DEFAULT);
		PASSWORD = props.getProperty("password");
		XML_DATASET = props.getProperty("xmlDataset", XML_DATASET_DEFAULT);
		
		// Name der Treiber-Klasse setzen
		String driver;
		if (URL.contains("postgres")) {
			driver = "org.postgresql.Driver"; 
		}
		else if (URL.contains("mysql")) {
			driver = "com.mysql.jdbc.Driver"; 
		}
//		else if (URL.contains("oracle")) {
//			driver = "oracle.jdbc.OracleDriver";
//		}
//		else if (URL.contains("sqlserver")) {
//			driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
//		}
//		else if (URL.contains("db2")) {
//			driver = "com.ibm.db2.jcc.DB2Driver";
//		}
		else if (URL.contains("jdbc:hsqldb")) {
			driver = "org.hsqldb.jdbcDriver";
		}
		else {
			throw new IllegalStateException("Die Datenbank-URL " + URL + " wird nicht unterstuetzt");
		}
		
		// Treiber-Klasse laden, um spaeter eine JDBC-Verbindung zu oeffnen
		try {
			Class.forName(driver);
		}
		catch (ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}
	
	
	/**
	 * Private Konstruktor, damit kein Objekt erzeugt werden kann
	 */
	private DbReload() {
	}

	
	/**
	 * DB neu laden
	 */
	public static void reload() throws Exception {
		if (reloaded == true)
			return;
		
		System.out.println(URL + " wird neu geladen ...");

		Connection jdbcConn = null;
		IDatabaseConnection dbunitConn = null;
		try {
			jdbcConn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
		
			boolean caseSensitiveTableNames = false;
			if (URL.contains("postgres")) {
				dbunitConn = new DatabaseConnection(jdbcConn, SCHEMA);
				dbunitConn.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new PostgresqlDataTypeFactory());
				caseSensitiveTableNames = true;
			}
			else if (URL.contains("mysql")) {
				dbunitConn = new MySqlConnection(jdbcConn, null);
				if (System.getProperty("os.name").contains("Linux")) {
					caseSensitiveTableNames = true;
				}
			}
//			else if (URL.contains("oracle")) {
//				dbunitConn = new OracleConnection(jdbcConn, SCHEMA);
//			}
//			else if (URL.contains("sqlserver")) {
//				dbunitConn = new MsSqlConnection(jdbcConn, SCHEMA);
//			}
//			else if (URL.contains("db2")) {
//				dbunitConn = new Db2Connection(jdbcConn, SCHEMA);
//				if (System.getProperty("os.name").contains("Linux")) {
//					caseSensitiveTableNames = true;
//				}
//			}
			else if (URL.contains("jdbc:hsqldb:")) {
				dbunitConn = new HsqldbConnection(jdbcConn, SCHEMA);
			}
			else {
				throw new IllegalStateException("Die Datenbank-URL " + URL + " wird nicht unterstuetzt");
			}
			
			final File xmlFile = new File(XML_DATASET);
			final FlatXmlDataSetBuilder flatXmlDataSetBuilder = new FlatXmlDataSetBuilder();
			flatXmlDataSetBuilder.setCaseSensitiveTableNames(caseSensitiveTableNames);
			final FlatXmlDataSet xmlDataset = flatXmlDataSetBuilder.build(xmlFile);
			final DatabaseOperation dbOp = URL.contains("sqlserver") ?
												// Fuer SQL Server ist ein spezieller INSERT-Modus notwendig,
												// damit IDENTITY-Spalten eingefuegt werden koennen
										   InsertIdentityOperation.CLEAN_INSERT :
										   CLEAN_INSERT;
			dbOp.execute(dbunitConn, xmlDataset);
		}
		finally {
			if (dbunitConn != null)
				dbunitConn.close();
			if (jdbcConn != null)
				jdbcConn.close();
		}
		reloaded = true;
		
		System.out.println("... fertig");
	}
}
