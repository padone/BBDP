package bbdp.doctor.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NotificationServer {
	//取得該醫生所有通知 
	public static String getNotification(DataSource datasource, String doctorID) {
		Connection con = null;
		String result = "";
		
		try {
			JSONArray notifications = new JSONArray();
		    con = datasource.getConnection();
		    Statement statement = con.createStatement();
		    ResultSet resultSet = statement.executeQuery("SELECT * FROM push WHERE doctorID = '" + doctorID + "' ORDER BY time DESC");
		    while (resultSet.next()) {
		    	JSONObject notification = new JSONObject();
		    	notification.put("doctorID", resultSet.getString("doctorID"));
		    	notification.put("patientID", resultSet.getString("patientID"));
		    	notification.put("time", resultSet.getString("time"));
		    	notification.put("hyperlink", resultSet.getString("hyperlink"));
		    	notification.put("title", resultSet.getString("title"));
		    	notification.put("body", resultSet.getString("body"));
		    	notifications.put(notification);
		    }
		    result = notifications.toString();
			if(result.equals("")) {
				System.out.println("BBDPDoctor NotificationServer getNotification empty");
				result = "[]";
			}
			resultSet.close();
			statement.close();
		} catch (SQLException e) {
			System.out.println("BBDPDoctor NotificationServer getNotification SQLException: " + e);
			result = "[]";
		} catch (JSONException e) {
			System.out.println("BBDPDoctor NotificationServer getNotification JSONException: " + e);
			result = "[]";
		} finally {
			if (con != null) try {con.close();}catch (Exception ignore) {}
		}
		//System.out.println(result);
		return result;
	}
	
	//刪除該醫生所有通知
	public static void clearAllNotification(DataSource datasource, String doctorID) {
		Connection con = null;
		
		try {
		    con = datasource.getConnection();
		    Statement statement = con.createStatement();
		    statement.executeUpdate("DELETE FROM push WHERE doctorID = '" + doctorID + "'");
			statement.close();
		} catch (SQLException e) {
			System.out.println("BBDPDoctor NotificationServer clearAllNotification SQLException: " + e);
		} finally {
			if (con != null) try {con.close();}catch (Exception ignore) {}
		}
	}
	
	//新增診間推播通知
	public static void newClinicPush(DataSource datasource, String doctorID, String patientID) {
		String currentTime = getCurrentTime();
		String hyperlink = "PatientBasicInformation.html";
		String title = "診間推播";
		String body = PatientBasicInformationServer.getPatientName(datasource, patientID) + "的診間推播";
		
		Connection con = null;
		try {
		    con = datasource.getConnection();
		    Statement statement = con.createStatement();
		    statement.executeUpdate("INSERT INTO push(doctorID, patientID, time, hyperlink, title, body) VALUES ('" + doctorID + "', '" + patientID + "', '" + currentTime + "', '" + hyperlink + "', '" + title + "', '" + body + "')");
			statement.close();
		} catch (SQLException e) {
			System.out.println("BBDPDoctor NotificationServer newClinicPush SQLException: " + e);
		} finally {
			if (con != null) try {con.close();}catch (Exception ignore) {}
		}
	}
	
	//新增提醒推播通知(問卷、檔案夾)
	public static void newRemindPush(DataSource datasource, String doctorID, String patientID, String title, String body, String hyperlink) {
		String currentTime = getCurrentTime();
		
		Connection con = null;
		try {
		    con = datasource.getConnection();
		    Statement statement = con.createStatement();
		    statement.executeUpdate("INSERT INTO push(doctorID, patientID, time, hyperlink, title, body) VALUES ('" + doctorID + "', '" + patientID + "', '" + currentTime + "', '" + hyperlink + "', '" + title + "', '" + body + "')");
			statement.close();
		} catch (SQLException e) {
			System.out.println("BBDPDoctor NotificationServer newRemindPush SQLException: " + e);
		} finally {
			if (con != null) try {con.close();}catch (Exception ignore) {}
		}
	}
	
	//取得現在時間
	public static String getCurrentTime() {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return simpleDateFormat.format(timestamp);
	}
	
	//取得通知設定
	public static String getNotificationSetting(DataSource datasource, String doctorID) {
		Connection con = null;
		String result = "";
		
		try {
		    con = datasource.getConnection();
		    Statement statement = con.createStatement();
		    ResultSet resultSet = statement.executeQuery("SELECT notification FROM doctor WHERE doctorID = '" + doctorID + "'");
		    while (resultSet.next()) {
		    	result = resultSet.getString("notification");
		    }
			if(result.equals("")) {
				System.out.println("BBDPDoctor NotificationServer getNotificationSetting empty");
				result = "{}";
			}
			resultSet.close();
			statement.close();
		} catch (SQLException e) {
			System.out.println("BBDPDoctor NotificationServer getNotificationSetting SQLException: " + e);
			result = "{}";
		} finally {
			if (con != null) try {con.close();}catch (Exception ignore) {}
		}
		//System.out.println(result);
		return result;
	}
	
	//修改通知設定
	public static void modifyNotificationSetting(DataSource datasource, String doctorID, String notification) {
		Connection con = null;
		
		try {
		    con = datasource.getConnection();
		    Statement statement = con.createStatement();
		    statement.executeUpdate("UPDATE doctor SET notification = '" + notification + "' WHERE doctorID = '" + doctorID + "'");
			statement.close();
		} catch (SQLException e) {
			System.out.println("BBDPDoctor NotificationServer modifyNotificationSetting SQLException: " + e);
		} finally {
			if (con != null) try {con.close();}catch (Exception ignore) {}
		}
	}
}