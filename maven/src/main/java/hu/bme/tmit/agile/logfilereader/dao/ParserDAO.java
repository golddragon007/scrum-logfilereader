package hu.bme.tmit.agile.logfilereader.dao;

import java.awt.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

//import com.mysql.jdbc.Statement;

import hu.bme.tmit.agile.logfilereader.model.ComponentEvent;
import hu.bme.tmit.agile.logfilereader.model.ComponentEvent.ComponentEventType;
import hu.bme.tmit.agile.logfilereader.model.LogTimestamp;
import hu.bme.tmit.agile.logfilereader.model.Message.MessageType;
import hu.bme.tmit.agile.logfilereader.model.Message;
import hu.bme.tmit.agile.logfilereader.model.TimerOperation;
import hu.bme.tmit.agile.logfilereader.model.TimerOperation.EventType;
import hu.bme.tmit.agile.logfilereader.model.TtcnEvent;
import hu.bme.tmit.agile.logfilereader.model.VerdictOperation;
import hu.bme.tmit.agile.logfilereader.model.VerdictOperation.VerdictType;
import util.Utils;

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

	public void loadMessage() {
		Statement stmt = null;
		Connection connection = ConnectionUtils.getConnection();
		try {
			stmt = connection.createStatement();
			String sql = "SELECT * FROM message_event";
		    ResultSet rs = stmt.executeQuery(sql);
		    ArrayList<Message> mlist = new ArrayList<Message>();
		    while(rs.next()) {
		    	Message temp = new Message();
		         temp.setId(rs.getInt("id"));
		         temp.setName(rs.getString("name"));
		         temp.setPort(rs.getString("port"));
		         temp.setSender(rs.getString("source"));
		         temp.setDestination(rs.getString("destination"));
		         temp.setParam(rs.getString("param"));
		        // temp.setTimestamp((LogTimestamp) rs.getObject("timestamp"));
		         temp.setMicro(rs.getInt("microsec"));
		         String a = rs.getString("event_type");
		         if (a.equals("receive"))
		        	 temp.setEventType(MessageType.Receive);
		         else if (a.equals("send"))
		        	 temp.setEventType(MessageType.Send);
		         
		         temp.setFileName(rs.getString("filename"));
		         System.out.println(temp.getEventType());
		         mlist.add(temp);
		      }
		      rs.close();
			
		} catch (SQLException e) {
			System.out.println("Error while selecting message from database : " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionUtils.closeResource(stmt);
			ConnectionUtils.closeResource(connection);
		}
		}
	public void loadComponent() {
		Statement stmt = null;
		Connection connection = ConnectionUtils.getConnection();
		try {
			stmt = connection.createStatement();
			String sql = "SELECT * FROM component_event";
		    ResultSet rs = stmt.executeQuery(sql);
		    ArrayList<ComponentEvent> clist = new ArrayList<ComponentEvent>();
		    while(rs.next()) {
		    	ComponentEvent temp = new ComponentEvent();
		    	 temp.setId(rs.getInt("id"));
		    	 temp.setName(rs.getString("name"));
		         temp.setComponentEventType((ComponentEventType) rs.getObject("event_type"));
		         temp.setProcessID(rs.getInt("process_id"));
		         temp.setComponentReference(rs.getInt("component_ref"));
		         temp.setTestcaseName(rs.getString("testcase_name"));
		         //temp.setTimestamp((LogTimestamp) rs.getObject("timestamp"));
		         temp.setMicro(rs.getInt("microsec"));
		         temp.setFileName("filename");
		         String a = rs.getString("event_type");
		         if (a.equals("create"))
		        	 temp.setComponentEventType(ComponentEventType.Create);
		         else if (a.equals("terminate"))
		        	 temp.setComponentEventType(ComponentEventType.Terminate);
		         
		         clist.add(temp);
		      }
		      rs.close();
			
		} catch (SQLException e) {
			System.out.println("Error while selecting component from database : " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionUtils.closeResource(stmt);
			ConnectionUtils.closeResource(connection);
		}
		}
	public void loadTimer() {
		Statement stmt = null;
		Connection connection = ConnectionUtils.getConnection();
		try {
			stmt = connection.createStatement();
			String sql = "SELECT * FROM timer_event";
		    ResultSet rs = stmt.executeQuery(sql);
		    ArrayList<TimerOperation> tlist = new ArrayList<TimerOperation>();
		    while(rs.next()) {
		    	TimerOperation temp = new TimerOperation();
		    	 temp.setId(rs.getInt("id"));
		    	 temp.setName(rs.getString("name"));
		         temp.setOwner( rs.getString("owner"));
		         //temp.setTimestamp((LogTimestamp) rs.getObject("timestamp"));
		         temp.setMicro(rs.getInt("microsec"));
		         String a = rs.getString("event_type");
		         if (a.equals("start"))
		        	 temp.setEventType(EventType.Start);
		         else if (a.equals("stop"))
		        	 temp.setEventType(EventType.Stop);
		         else if (a.equals("timeout"))
		        	 temp.setEventType(EventType.Timeout);
		         temp.setDuration(rs.getDouble("duration"));
		         temp.setFileName("filename");
		         tlist.add(temp);
		      }
		      rs.close();
			
		} catch (SQLException e) {
			System.out.println("Error while selecting timer from database : " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionUtils.closeResource(stmt);
			ConnectionUtils.closeResource(connection);
		}
		}
	public void loadVerdict() {
		Statement stmt = null;
		Connection connection = ConnectionUtils.getConnection();
		try {
			stmt = connection.createStatement();
			String sql = "SELECT * FROM verdict_event";
		    ResultSet rs = stmt.executeQuery(sql);
		    ArrayList<VerdictOperation> vlist = new ArrayList<VerdictOperation>();
		    while(rs.next()) {
		    	VerdictOperation temp = new VerdictOperation();
		    	 temp.setId(rs.getInt("id"));
		    	// temp.setTimestamp((LogTimestamp) rs.getObject("timestamp"));
		         temp.setMicro(rs.getInt("microsec"));
		         temp.setPortNumber(rs.getInt("port"));
		         temp.setOwner( rs.getString("owner"));
		         String a = rs.getString("event_type");
		         if (a.equals("pass"))
		        	 temp.setVerdictType(VerdictType.Pass);
		         else if (a.equals("fail"))
		        	 temp.setVerdictType(VerdictType.Fail);
		         else if (a.equals("inconclusive"))
		        	 temp.setVerdictType(VerdictType.Inconclusive);
		         temp.setFileName("filename");
		         vlist.add(temp);
		         
		      }
		      rs.close();
			
		} catch (SQLException e) {
			System.out.println("Error while selecting verdict from database : " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionUtils.closeResource(stmt);
			ConnectionUtils.closeResource(connection);
		}
		}	
	
}
