package hu.bme.tmit.agile.logfilereader.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import util.PropertyHandler;

public class Connector {
	private static final String CONFIG_PROPERTIES = "config.properties";

	private static final String PASSWORD_PROPERTY = "password";
	private static final String USER_PROPERTY = "user";
	private static final String DATABASE_PROPERTY = "database";

	private static final String DATABASE_USER_SEPARATOR = "?";
	private static final String USER_PASSWORD_SEPARATOR = "&";

	public ResultSet executeQuery(String query) {

		PropertyHandler ph = new PropertyHandler();
		Properties properties = ph.getProperties(CONFIG_PROPERTIES);

		String database = properties.getProperty(DATABASE_PROPERTY);
		String user = properties.getProperty(USER_PROPERTY);
		String password = properties.getProperty(PASSWORD_PROPERTY);

		Statement stmt = null;
		ResultSet rs = null;

		try {
			Connection conn = getConnection(database, user, password);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return rs;
	}

	private Connection getConnection(String database, String user, String password) throws SQLException {
		Connection connection = DriverManager.getConnection("jdbc:mysql://" + database + DATABASE_USER_SEPARATOR
				+ "user=" + user + USER_PASSWORD_SEPARATOR + "password=" + password);
		return connection;
	}
}
