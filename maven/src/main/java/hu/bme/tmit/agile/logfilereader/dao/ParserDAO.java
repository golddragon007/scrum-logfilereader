package hu.bme.tmit.agile.logfilereader.dao;


import java.util.List;

import org.joda.time.DateTime;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import hu.bme.tmit.agile.logfilereader.model.CreatedTerminatedComponent.ComponentType;
import hu.bme.tmit.agile.logfilereader.model.CreatedTerminatedComponent;
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
   *          Message to be inserted.
*/
public void saveTtcnEvent(TtcnEvent event) {
	Connection connection = ConnectionUtils.getConnection();
	PreparedStatement pstmt = null;
	
	if (event instanceof Message) {
		try {
			pstmt = connection.prepareStatement("insert into message_event (name, port, source, destination, param, timestamp, microsec, event_type, filename) values (?,?,?,?,?,?,?,?,?)");
			pstmt.setString(1, ((Message) event).getName());
			pstmt.setString(2, ((Message) event).getPort());
			pstmt.setString(3, ((Message) event).getSender());
			pstmt.setString(4, ((Message) event).getDestination());
			pstmt.setString(5, ((Message) event).getParam());
			//TODO Timestamp conversion
			pstmt.setDate(6,new java.sql.Date(20));
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
	}else if(event instanceof CreatedTerminatedComponent) {
		try {
			pstmt = connection.prepareStatement("insert into component_event (event_type, process_id, component_ref, testcase_name, timestamp, filename) values (?,?,?,?,?,?)");
			pstmt.setString(1, ((CreatedTerminatedComponent) event).getCompType().name());
			System.out.println(((CreatedTerminatedComponent) event).getCompType().name());
			pstmt.setInt(2, ((CreatedTerminatedComponent) event).getProcessID());
			//TODO Timestamp conversion
			
			pstmt.setInt(3, ((CreatedTerminatedComponent) event).getComponentReference());
			pstmt.setString(4, ((CreatedTerminatedComponent) event).getTestcaseName());
			pstmt.setDate(5, new java.sql.Date(20));
			pstmt.setString(6, ((CreatedTerminatedComponent) event).getFileName());
			
			pstmt.executeUpdate();
		
		} catch (SQLException e) {
			System.out.println("Error while inserting component into database : " + e.getMessage());
		    e.printStackTrace();
		} finally {
			ConnectionUtils.closeResource(pstmt);
			ConnectionUtils.closeResource(connection);
		}

	}else if(event instanceof TimerOperation) {
		try {
			pstmt = connection.prepareStatement("insert into timer_event (name, owner, timestamp, event_type, duration, filename) values (?,?,?,?,?,?)");
			pstmt.setString(1, ((TimerOperation) event).getName());
			pstmt.setString(2, ((TimerOperation) event).getSender());
			//TODO Timestamp conversion
			pstmt.setDate(3,new java.sql.Date(20));
			pstmt.setString(4, ((TimerOperation) event).getEventType().name());
			pstmt.setDouble(5, ((TimerOperation) event).getDuration());
			pstmt.setString(6, ((TimerOperation) event).getFileName());
			
			pstmt.executeUpdate();
		
		} catch (SQLException e) {
			System.out.println("Error while inserting timer into database : " + e.getMessage());
		    e.printStackTrace();
		} finally {
			ConnectionUtils.closeResource(pstmt);
			ConnectionUtils.closeResource(connection);
		}

	}else if(event instanceof VerdictOperation) {
		try {
			pstmt = connection.prepareStatement("insert into verdict_event (port, owner, timestamp, event_type, filename) values (?,?,?,?,?)");
			pstmt.setInt(1, ((VerdictOperation) event).getPortNumber());
			pstmt.setString(2, ((VerdictOperation) event).getSender());
			//TODO Timestamp conversion
			pstmt.setDate(3,new java.sql.Date(20));
			pstmt.setString(4, ((VerdictOperation) event).getVerdictType().name());
			pstmt.setString(5, ((VerdictOperation) event).getFileName());
			
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
  
	
}

