package bbdp.doctor.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

public class DoctorSuggestionServer {
	public static int newDoctorSuggestion(Connection conn, String doctorID, String email, String content) {
		int returnInt = 0;	
		java.text.DateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar now = Calendar.getInstance();
		try {
			Statement st = conn.createStatement();
			returnInt = st.executeUpdate("INSERT INTO doctorsuggestion (doctorID, dateTime, email, content) VALUES('"+doctorID+"','"+ sdf.format(now.getTime())+"','"+ email+"','"+content+"')");
		    st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return returnInt;
	}
}
