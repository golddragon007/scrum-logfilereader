package hu.bme.tmit.agile.logfilereader.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeSet;

import hu.bme.tmit.agile.logfilereader.model.ComponentEvent;
import hu.bme.tmit.agile.logfilereader.model.LogTimestamp;
import hu.bme.tmit.agile.logfilereader.model.Message;
import hu.bme.tmit.agile.logfilereader.model.TimerOperation;
import hu.bme.tmit.agile.logfilereader.model.TtcnEvent;
import hu.bme.tmit.agile.logfilereader.model.VerdictOperation;

public class ParserDAO {

	/**
	 * Insert message into database, with a link to the corresponding sequence.
	 *
	 * @param message
	 *            Message to be inserted.
	 */
	public void saveTtcnEvent(TtcnEvent event) {
		Connection connection = ConnectionUtils.getConnection();
		PreparedStatement pstmt = null;

		if (event instanceof Message) {
			try {
				pstmt = connection.prepareStatement(
						"insert into message_event (name, port, source, destination, param, timestamp, microsec, event_type, filename) values (?,?,?,?,?,?,?,?,?)");
				pstmt.setString(1, ((Message) event).getName());
				pstmt.setString(2, ((Message) event).getPort());
				pstmt.setString(3, ((Message) event).getSender());
				pstmt.setString(4, ((Message) event).getDestination());
				pstmt.setString(5, ((Message) event).getParam());
				// TODO Timestamp conversion
				pstmt.setString(6, ((Message) event).getTimestampString());
				pstmt.setInt(7, ((Message) event).getTimestamp().getMicro());
				pstmt.setString(8, ((Message) event).getEventType().name());
				pstmt.setString(9, ((Message) event).getFileName());

				pstmt.executeUpdate();

			} catch (SQLException e) {
				System.out.println("Error while inserting message into database : " + e.getMessage());
				e.printStackTrace();
			} finally {
				ConnectionUtils.closeResource(pstmt);
				ConnectionUtils.closeResource(connection);
			}
		} else if (event instanceof ComponentEvent) {
			try {
				pstmt = connection.prepareStatement(
						"insert into component_event (event_type, name, process_id, component_ref, testcase_name, timestamp, microsec, filename, component_type) values (?,?,?,?,?,?,?,?,?)");
				pstmt.setString(1, ((ComponentEvent) event).getComponentEventType().name());
				pstmt.setString(2, ((ComponentEvent) event).getSender());
				pstmt.setInt(3, ((ComponentEvent) event).getProcessID());
				// TODO Timestamp conversion

				pstmt.setInt(4, ((ComponentEvent) event).getComponentReference());
				pstmt.setString(5, ((ComponentEvent) event).getTestcaseName());
				pstmt.setString(6, ((ComponentEvent) event).getTimestampString());
				pstmt.setInt(7, ((ComponentEvent) event).getTimestamp().getMicro());
				pstmt.setString(8, ((ComponentEvent) event).getFileName());
				pstmt.setString(9, ((ComponentEvent) event).getComponentType());

				pstmt.executeUpdate();

			} catch (SQLException e) {
				System.out.println("Error while inserting component into database : " + e.getMessage());
				e.printStackTrace();
			} finally {
				ConnectionUtils.closeResource(pstmt);
				ConnectionUtils.closeResource(connection);
			}

		} else if (event instanceof TimerOperation) {
			try {
				pstmt = connection.prepareStatement(
						"insert into timer_event (name, owner, timestamp, microsec, event_type, duration, filename) values (?,?,?,?,?,?,?)");
				pstmt.setString(1, ((TimerOperation) event).getName());
				pstmt.setString(2, ((TimerOperation) event).getSender());
				// TODO Timestamp conversion
				pstmt.setString(3, ((TimerOperation) event).getTimestampString());
				pstmt.setInt(4, ((TimerOperation) event).getTimestamp().getMicro());
				pstmt.setString(5, ((TimerOperation) event).getEventType().name());
				pstmt.setDouble(6, ((TimerOperation) event).getDuration());
				pstmt.setString(7, ((TimerOperation) event).getFileName());

				pstmt.executeUpdate();

			} catch (SQLException e) {
				System.out.println("Error while inserting timer into database : " + e.getMessage());
				e.printStackTrace();
			} finally {
				ConnectionUtils.closeResource(pstmt);
				ConnectionUtils.closeResource(connection);
			}

		} else if (event instanceof VerdictOperation) {
			try {
				pstmt = connection.prepareStatement(
						"insert into verdict_event (port, owner, timestamp, microsec, event_type, filename) values (?,?,?,?,?,?)");
				pstmt.setInt(1, ((VerdictOperation) event).getPortNumber());
				pstmt.setString(2, ((VerdictOperation) event).getSender());
				// TODO Timestamp conversion
				pstmt.setString(3, ((VerdictOperation) event).getTimestampString());
				pstmt.setInt(4, ((VerdictOperation) event).getTimestamp().getMicro());
				pstmt.setString(5, ((VerdictOperation) event).getVerdictType().name());
				pstmt.setString(6, ((VerdictOperation) event).getFileName());

				pstmt.executeUpdate();

			} catch (SQLException e) {
				System.out.println("Error while inserting verdict into database : " + e.getMessage());
				e.printStackTrace();
			} finally {
				ConnectionUtils.closeResource(pstmt);
				ConnectionUtils.closeResource(connection);
			}
		}
	}

	/**
	 * Insert message into database, with a link to the corresponding sequence. (multi-insert mode)
	 *
	 * @param message
	 *            Messages to be inserted.
	 */
	public void saveTtcnEventMulti(TreeSet<TtcnEvent> eventSet) {
		Connection connection = ConnectionUtils.getConnection();
		PreparedStatement pstmt_message = null, pstmt_component = null, pstmt_timer = null, pstmt_verdict = null;
		int batch_size = 100;
		
		try {
			connection.setAutoCommit(false);
			pstmt_message = connection.prepareStatement(
					"insert into message_event (name, port, source, destination, param, timestamp, microsec, event_type, filename) values (?,?,?,?,?,?,?,?,?)");
			pstmt_component = connection.prepareStatement(
					"insert into component_event (event_type, name, process_id, component_ref, testcase_name, timestamp, microsec, filename, component_type) values (?,?,?,?,?,?,?,?,?)");
			pstmt_timer = connection.prepareStatement(
					"insert into timer_event (name, owner, timestamp, microsec, event_type, duration, filename) values (?,?,?,?,?,?,?)");
			pstmt_verdict = connection.prepareStatement(
					"insert into verdict_event (port, owner, timestamp, microsec, event_type, filename) values (?,?,?,?,?,?)");
			
			int i_message = 0, i_component = 0, i_timer = 0, i_verdict = 0;
			
			// Some systems has batch size limitation, so we insert after {batch_size} ea the events. (separatly) 
			for (TtcnEvent event : eventSet) {
				if (event instanceof Message) {
					i_message++;
					
					pstmt_message.setString(1, ((Message) event).getName());
					pstmt_message.setString(2, ((Message) event).getPort());
					pstmt_message.setString(3, ((Message) event).getSender());
					pstmt_message.setString(4, ((Message) event).getDestination());
					pstmt_message.setString(5, ((Message) event).getParam());
					pstmt_message.setString(6, ((Message) event).getTimestampString());
					pstmt_message.setInt(7, ((Message) event).getTimestamp().getMicro());
					pstmt_message.setString(8, ((Message) event).getEventType().name());
					pstmt_message.setString(9, ((Message) event).getFileName());
					pstmt_message.addBatch();
					
					if (i_message % batch_size == 0) {
						pstmt_message.executeBatch();
					}
				} else if (event instanceof ComponentEvent) {
					i_component++;
					
					pstmt_component.setString(1, ((ComponentEvent) event).getComponentEventType().name());
					pstmt_component.setString(2, ((ComponentEvent) event).getSender());
					pstmt_component.setInt(3, ((ComponentEvent) event).getProcessID());
					pstmt_component.setInt(4, ((ComponentEvent) event).getComponentReference());
					pstmt_component.setString(5, ((ComponentEvent) event).getTestcaseName());
					pstmt_component.setString(6, ((ComponentEvent) event).getTimestampString());
					pstmt_component.setInt(7, ((ComponentEvent) event).getTimestamp().getMicro());
					pstmt_component.setString(8, ((ComponentEvent) event).getFileName());
					pstmt_component.setString(9, ((ComponentEvent) event).getComponentType());
					pstmt_component.addBatch();
					
					if (i_component % batch_size == 0) {
						pstmt_component.executeBatch();
					}
				} else if (event instanceof TimerOperation) {
					i_timer++;
					
					pstmt_timer.setString(1, ((TimerOperation) event).getName());
					pstmt_timer.setString(2, ((TimerOperation) event).getSender());
					pstmt_timer.setString(3, ((TimerOperation) event).getTimestampString());
					pstmt_timer.setInt(4, ((TimerOperation) event).getTimestamp().getMicro());
					pstmt_timer.setString(5, ((TimerOperation) event).getEventType().name());
					pstmt_timer.setDouble(6, ((TimerOperation) event).getDuration());
					pstmt_timer.setString(7, ((TimerOperation) event).getFileName());
					pstmt_timer.addBatch();
					
					if (i_timer % batch_size == 0) {
						pstmt_timer.executeBatch();
					}
				} else if (event instanceof VerdictOperation) {
					i_verdict++;
					
					pstmt_verdict.setInt(1, ((VerdictOperation) event).getPortNumber());
					pstmt_verdict.setString(2, ((VerdictOperation) event).getSender());
					pstmt_verdict.setString(3, ((VerdictOperation) event).getTimestampString());
					pstmt_verdict.setInt(4, ((VerdictOperation) event).getTimestamp().getMicro());
					pstmt_verdict.setString(5, ((VerdictOperation) event).getVerdictType().name());
					pstmt_verdict.setString(6, ((VerdictOperation) event).getFileName());
					pstmt_verdict.addBatch();
					
					if (i_verdict % batch_size == 0) {
						pstmt_verdict.executeBatch();
					}
				}
			}
			// insert remainder.
			if (i_message % batch_size != 0) {
				pstmt_message.executeBatch();
			}
			if (i_component % batch_size != 0) {
				pstmt_component.executeBatch();
			}
			if (i_timer % batch_size != 0) {
				pstmt_timer.executeBatch();
			}
			if (i_verdict % batch_size != 0) {
				pstmt_verdict.executeBatch();
			}

			connection.commit();
		} catch (SQLException e) {
			System.out.println("Error while inserting multiple records into database : " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionUtils.closeResource(pstmt_verdict);
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				System.out.println("Error while set back auto commit : " + e.getMessage());
				e.printStackTrace();
			}
			ConnectionUtils.closeResource(connection);
		}
	}

	/**
	 * Get saved file names from component_event table.
	 */
	public Object[] getSavedFileNames() {
		ArrayList<String> l = new ArrayList<String>();
		Connection connection = ConnectionUtils.getConnection();
		PreparedStatement pstmt = null;
		ResultSet res = null;

		try {
			pstmt = connection
					.prepareStatement("SELECT filename FROM component_event GROUP BY filename ORDER BY filename");
			res = pstmt.executeQuery();

			while (res.next()) {
				final ResultSet rs = res;
				l.add(rs.getString("filename"));
			}
		} catch (SQLException e) {
			System.out.println("Error while inserting verdict into database : " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionUtils.closeResource(pstmt);
			ConnectionUtils.closeResource(connection);
		}

		return l.toArray();
	}

	/**
	 * Load message from database.
	 *
	 * @param fileName
	 *            FileName to be loaded.
	 */
	public TreeSet<TtcnEvent> loadTtcnEvent(String fileName) {
		TreeSet<TtcnEvent> eventSet = new TreeSet<TtcnEvent>();

		Connection connection = ConnectionUtils.getConnection();
		PreparedStatement pstmt = null;
		ResultSet res = null;

		// INFO: timestamp WTF: normally it reads 2014-10-24 19:53:04.0, so we
		// need to substring it.

		try {
			pstmt = connection.prepareStatement("SELECT * FROM component_event WHERE filename = ?");
			pstmt.setString(1, fileName);
			res = pstmt.executeQuery();

			while (res.next()) {
				final ResultSet rs = res;
				eventSet.add(new ComponentEvent() {
					{
						setComponentReference(rs.getInt("component_ref"));
						setComponentType(rs.getString("component_type"));
						setTestcaseName(rs.getString("testcase_name"));
						setProcessID(rs.getInt("process_id"));
						setFileName(rs.getString("filename"));
						setSender(rs.getString("name"));
						setTimestamp(
								new LogTimestamp(rs.getString("timestamp").substring(0, 19), rs.getInt("microsec")));
						setComponentEventType(ComponentEventType.fromString(rs.getString("event_type")));
					}
				});
			}
			ConnectionUtils.closeResource(pstmt);

			pstmt = connection.prepareStatement("SELECT * FROM message_event WHERE filename = ?");
			pstmt.setString(1, fileName);
			res = pstmt.executeQuery();

			while (res.next()) {
				final ResultSet rs = res;
				eventSet.add(new Message() {
					{
						setPort(rs.getString("port"));
						setDestination(rs.getString("destination"));
						setParam(rs.getString("param"));
						setSender(rs.getString("source"));
						setFileName(rs.getString("filename"));
						setName(rs.getString("name"));
						setTimestamp(
								new LogTimestamp(rs.getString("timestamp").substring(0, 19), rs.getInt("microsec")));
						setEventType(MessageType.fromString(rs.getString("event_type")));
					}
				});
			}
			ConnectionUtils.closeResource(pstmt);

			pstmt = connection.prepareStatement("SELECT * FROM timer_event WHERE filename = ?");
			pstmt.setString(1, fileName);
			res = pstmt.executeQuery();

			while (res.next()) {
				final ResultSet rs = res;
				eventSet.add(new TimerOperation() {
					{
						setDuration(rs.getDouble("duration"));
						setSender(rs.getString("owner"));
						setFileName(rs.getString("filename"));
						setName(rs.getString("name"));
						setTimestamp(
								new LogTimestamp(rs.getString("timestamp").substring(0, 19), rs.getInt("microsec")));
						setEventType(EventType.fromString(rs.getString("event_type")));
					}
				});
			}
			ConnectionUtils.closeResource(pstmt);

			pstmt = connection.prepareStatement("SELECT * FROM verdict_event WHERE filename = ?");
			pstmt.setString(1, fileName);
			res = pstmt.executeQuery();

			while (res.next()) {
				final ResultSet rs = res;
				eventSet.add(new VerdictOperation() {
					{
						setPortNumber(rs.getInt("port"));
						setFileName(rs.getString("filename"));
						setSender(rs.getString("owner"));
						setTimestamp(
								new LogTimestamp(rs.getString("timestamp").substring(0, 19), rs.getInt("microsec")));
						setVerdictType(VerdictType.fromString(rs.getString("event_type")));
					}
				});
			}
			// Last pstmt closer is in the final!
		} catch (SQLException e) {
			System.out.println("Error while fetching ttcn events into database : " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionUtils.closeResource(pstmt);
			ConnectionUtils.closeResource(connection);
		}

		return eventSet;
	}
	
	/**
	 * Call this if you want to know the current Ttcn exist or not.
	 * 
	 * @param fileName
	 *        File Name to check.
	 * @return
	 * 		  Returns true if exist, returns false if not.
	 */
	public boolean existTtcnEvent(String fileName) throws SQLException {
		Connection connection = ConnectionUtils.getConnection();
		PreparedStatement pstmt = null;
		ResultSet res = null;

		try {
			pstmt = connection.prepareStatement("SELECT count(*) AS db FROM component_event WHERE filename = ?");
			pstmt.setString(1, fileName);
			res = pstmt.executeQuery();

			res.next();
			int db = res.getInt("db");
			
			if (db > 0) {
				return true;
			}
		} finally {
			ConnectionUtils.closeResource(pstmt);
			ConnectionUtils.closeResource(connection);
		}
		
		return false;
	}
	
	
	public void removeTtcnEvent(String fileName) throws SQLException {
		Connection connection = ConnectionUtils.getConnection();
		PreparedStatement pstmt = null;

		try {
			pstmt = connection.prepareStatement("DELETE FROM message_event WHERE filename = ?");
			pstmt.setString(1, fileName);
			pstmt.executeUpdate();
			ConnectionUtils.closeResource(pstmt);
			
			pstmt = connection.prepareStatement("DELETE FROM component_event WHERE filename = ?");
			pstmt.setString(1, fileName);
			pstmt.executeUpdate();
			ConnectionUtils.closeResource(pstmt);
			
			pstmt = connection.prepareStatement("DELETE FROM timer_event WHERE filename = ?");
			pstmt.setString(1, fileName);
			pstmt.executeUpdate();
			ConnectionUtils.closeResource(pstmt);
			
			pstmt = connection.prepareStatement("DELETE FROM verdict_event WHERE filename = ?");
			pstmt.setString(1, fileName);
			pstmt.executeUpdate();
		} finally {
			ConnectionUtils.closeResource(pstmt);
			ConnectionUtils.closeResource(connection);
		}
	}

}
