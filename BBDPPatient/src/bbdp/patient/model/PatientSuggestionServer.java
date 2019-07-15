package bbdp.patient.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PatientSuggestionServer {
	public static int newPatientSuggestion(Connection conn, String patientID, String email, String content) {
		int returnInt = 0;	
		java.text.DateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar now = Calendar.getInstance();
		try {
			Statement st = conn.createStatement();
			returnInt = st.executeUpdate("INSERT INTO patientsuggestion (patientID, dateTime, email, content) VALUES('"+patientID+"','"+ sdf.format(now.getTime())+"','"+email+"','"+content+"')");
		    st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return returnInt;
	}
}
