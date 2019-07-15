package bbdp.patient.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.tomcat.jdbc.pool.DataSource;

public class NotificationSettingServer {
	//取得通知設定
	public static String getNotificationSetting(DataSource datasource, String patientID) {
		Connection con = null;
		String result = "";
		
		try {
		    con = datasource.getConnection();
		    Statement statement = con.createStatement();
		    ResultSet resultSet = statement.executeQuery("SELECT notification FROM patient WHERE patientID = '" + patientID + "'");
		    while (resultSet.next()) {
		    	result = resultSet.getString("notification");
		    }
			if(result.equals("")) {
				System.out.println("BBDPPatient NotificationSettingServer getNotificationSetting empty");
				result = "yes";
			}
			resultSet.close();
			statement.close();
		} catch (SQLException e) {
			System.out.println("BBDPPatient NotificationSettingServer getNotificationSetting SQLException: " + e);
			result = "yes";
		} finally {
			if (con != null) try {con.close();}catch (Exception ignore) {}
		}
		//System.out.println(result);
		return result;
	}
	
	//修改通知設定
	public static void modifyNotificationSetting(DataSource datasource, String patientID, String notification) {
		Connection con = null;
		
		try {
		    con = datasource.getConnection();
		    Statement statement = con.createStatement();
		    statement.executeUpdate("UPDATE patient SET notification = '" + notification + "' WHERE patientID = '" + patientID + "'");
			statement.close();
		} catch (SQLException e) {
			System.out.println("BBDPPatient NotificationSettingServer modifyNotificationSetting SQLException: " + e);
		} finally {
			if (con != null) try {con.close();}catch (Exception ignore) {}
		}
	}
}