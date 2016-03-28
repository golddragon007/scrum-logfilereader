package hu.bme.tmit.agile.logfilereader;

import hu.bme.tmit.agile.logfilereader.dao.Connector;

public class App {
	public static void main(String[] args) {
		final String QUERY = "SELECT name FROM message";

		Connector conn = new Connector();
		conn.executeQuery(QUERY);
	}
}
