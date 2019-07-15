package bbdp.patient.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClinicPushServer {
	//取得醫生姓名
	public static String getDoctorName(Connection conn, String scanText){
		String doctorName = "";
		PreparedStatement statement = null;
		try {
			String sql = "SELECT name FROM doctor WHERE doctorID = ?";
			statement = conn.prepareStatement(sql);
			statement.setString(1, scanText);
				
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				doctorName = resultSet.getString("name");
			}
			if (resultSet != null) try { resultSet.close(); } catch (SQLException ignore) {}			
		}
		catch (SQLException ex) {
			System.out.println("發生SQLException");
		}
		finally {
			if (statement != null) try { statement.close(); }catch (SQLException ignore) {}
			if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return doctorName;
	}
}
