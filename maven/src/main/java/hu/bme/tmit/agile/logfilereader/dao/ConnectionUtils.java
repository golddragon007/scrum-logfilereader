package hu.bme.tmit.agile.logfilereader.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import util.PropertyHandler;

public class ConnectionUtils {
	private static final String DRIVER = "com.mysql.jdbc.Driver";

	private static final String CONFIG_PROPERTIES = "config.properties";
	private static final String PASSWORD_PROPERTY = "password";
	private static final String USER_PROPERTY = "user";
	private static final String DATABASE_PROPERTY = "database";
	private static final String DATABASE_USER_SEPARATOR = "?";
	private static final String USER_PASSWORD_SEPARATOR = "&";

	static {
		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e) {
			System.out.println("Unable to locate JDBC driver ==> exiting the program");
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static Connection getConnection() {
		Connection connection = null;
		try {
			PropertyHandler ph = new PropertyHandler();
			Properties properties = ph.getProperties(CONFIG_PROPERTIES);

			String database = properties.getProperty(DATABASE_PROPERTY);
			String user = properties.getProperty(USER_PROPERTY);
			String password = properties.getProperty(PASSWORD_PROPERTY);

			connection = DriverManager.getConnection("jdbc:mysql://" + database + DATABASE_USER_SEPARATOR + "user="
					+ user + USER_PASSWORD_SEPARATOR + "password=" + password);
		} catch (SQLException e) {
			System.out.println("Unable to get a connection : " + e.getMessage());
			e.printStackTrace();
			System.exit(2);
		}
		return connection;
	}

	public static void closeResource(AutoCloseable resource) {
		if (resource != null) {
			try {
				resource.close();
			} catch (Exception e) {
				System.out.println("Failed at closing the resource : " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
