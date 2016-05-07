package hu.bme.tmit.agile.logfilereader.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeSet;

import hu.bme.tmit.agile.logfilereader.model.LogTimestamp;
import hu.bme.tmit.agile.logfilereader.model.TimerOperation;
import hu.bme.tmit.agile.logfilereader.model.TimerOperation.EventType;
import hu.bme.tmit.agile.logfilereader.model.TtcnEvent;
import util.DatabaseColumnName;

public class DatabaseLoader {

	private Connector conn = new Connector();

	private final String QUERY = "SELECT * FROM timer_event WHERE filename='";

	public TreeSet<TtcnEvent> getTimerOperations(String fileName) throws SQLException {
		ResultSet rs = conn.executeQuery(QUERY + fileName + "'");
		TreeSet<TtcnEvent> ts = new TreeSet<TtcnEvent>();

		if (rs != null) {
			while (rs.next()) {
				String name = rs.getString(DatabaseColumnName.NAME);
				String owner = rs.getString(DatabaseColumnName.TIMER_OWNER);
				int microsec = rs.getInt(DatabaseColumnName.MICROSEC);
				double duration = rs.getDouble(DatabaseColumnName.TIMER_DURATION);
				String eventTypeString = rs.getString(DatabaseColumnName.TIMER_EVENT_TYPE);
				String tss = rs.getString(DatabaseColumnName.TIMESTAMP);
				System.out.println(tss);

				TimerOperation to = getTimerOperation(fileName, name, owner, microsec, duration, eventTypeString);
				ts.add(to);

				System.out.println(to.getName() + " " + to.getSender() + " " + to.getTimestamp().getMicro() + " "
						+ to.getDuration() + " " + to.getEventType() + " " + to.getFileName());
			}

			try {
				rs.close();
			} catch (SQLException ex) {
			}
			rs = null;
		}

		return ts;
	}

	private TimerOperation getTimerOperation(String fileName, String name, String owner, int microsec, double duration,
			String eventTypeString) {
		TimerOperation to = new TimerOperation();
		to.setName(name);
		to.setSender(owner);
		to.setDuration(duration);
		to.setEventType(getEventType(eventTypeString));
		to.setFileName(fileName);

		LogTimestamp lt = new LogTimestamp();
		lt.setMicro(microsec);
		to.setTimestamp(lt);
		return to;
	}

	private EventType getEventType(String eventTypeString) {
		if (eventTypeString.equals("start")) {
			return EventType.Start;
		}
		if (eventTypeString.equals("stop")) {
			return EventType.Stop;
		}
		return EventType.Timeout;
	}
}
