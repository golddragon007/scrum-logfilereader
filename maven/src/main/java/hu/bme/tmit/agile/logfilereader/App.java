package hu.bme.tmit.agile.logfilereader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import hu.bme.tmit.agile.logfilereader.controller.Parser;
import hu.bme.tmit.agile.logfilereader.dao.Connector;

public class App {
	public static void main(String[] args) {
		final String QUERY = "SELECT name FROM message";

		Connector conn = new Connector();
		conn.executeQuery(QUERY);
		
		Parser parser = new Parser();
		parser.parse("logs/WCG100200010.txt");
	}
}
