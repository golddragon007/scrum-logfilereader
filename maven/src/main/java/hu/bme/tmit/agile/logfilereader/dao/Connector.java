package hu.bme.tmit.agile.logfilereader.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class Connector {
	private static final String CONFIG_PROPERTIES = "config.properties";

	private static final String PASSWORD_PROPERTY = "password";
	private static final String USER_PROPERTY = "user";
	private static final String DATABASE_PROPERTY = "database";

	private static final String COLUMN_NAME = "name";

	private static final String DATABASE_USER_SEPARATOR = "?";
	private static final String USER_PASSWORD_SEPARATOR = "&";

	public void executeQuery(String query) {

		Properties properties = getProperties();

		String database = properties.getProperty(DATABASE_PROPERTY);
		String user = properties.getProperty(USER_PROPERTY);
		String password = properties.getProperty(PASSWORD_PROPERTY);

		Statement stmt = null;
		ResultSet rs = null;

		try {
			Connection conn = getConnection(database, user, password);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);

			while (rs.next()) {
				String name = rs.getString(COLUMN_NAME);
				System.out.println(name);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
				}
				rs = null;
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException ex) {
				}
				stmt = null;
			}
		}
	}

	private Connection getConnection(String database, String user, String password) throws SQLException {
		Connection connection = DriverManager.getConnection("jdbc:mysql://" + database + DATABASE_USER_SEPARATOR
				+ "user=" + user + USER_PASSWORD_SEPARATOR + "password=" + password);
		return connection;
	}

	private Properties getProperties() {
		Properties properties = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(CONFIG_PROPERTIES);
			properties.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return properties;
	}
}
