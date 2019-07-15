package bbdp.doctor.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ClinicHoursServer {
	//取得門診時間
	public static String getClinicHours(DataSource datasource, String doctorID) {
		Connection con = null;
		String result = "";
		try {
			JSONObject CH = new JSONObject();
		    con = datasource.getConnection();
		    Statement statement = con.createStatement();
		    ResultSet resultSet = statement.executeQuery("SELECT * FROM clinichours WHERE doctorID = '" + doctorID + "'");
		    while (resultSet.next()) {
		    	CH.put("CHTime", new JSONObject(resultSet.getString("time")));
		    	CH.put("CHPS", new JSONArray(resultSet.getString("PS")));
		    	CH.put("CHPhone", resultSet.getString("phone"));
		    }
			result = CH.toString();
			resultSet.close();
			statement.close();
		} catch (SQLException e) {
			System.out.println("BBDPDoctor ClinicHoursServer getClinicHours SQLException: " + e);
			result = "";
		} catch (JSONException e) {
			System.out.println("BBDPDoctor ClinicHoursServer getClinicHours JSONException: " + e);
			result = "";
		} finally {
			if (con != null) try {con.close();}catch (Exception ignore) {}
		}
		//System.out.println(result);
		return result;
	}
	
	//更新門診時間
	public static void updateClinicHours(DataSource datasource, String doctorID, String PS, String time, String phone) {
		Connection con = null;
		try {
		    con = datasource.getConnection();
		    Statement statement = con.createStatement();
		    ResultSet resultSet = statement.executeQuery("SELECT * FROM clinichours WHERE doctorID = '" + doctorID + "'");
		    Statement statement2 = con.createStatement();
		    if(resultSet.next()) {
		    	 statement2.executeUpdate("UPDATE clinichours SET PS = '" + PS + "', time = '" + time + "', phone = '" + phone + "' WHERE doctorID = '" + doctorID + "'");
			} else {
				statement2.executeUpdate("INSERT INTO clinichours(doctorID, PS, time, phone) VALUES ('" + doctorID + "', '" + PS + "', '" + time + "', '" + phone + "')");
			}
		    resultSet.close();
			statement.close();
			statement2.close();
		} catch (SQLException e) {
			System.out.println("BBDPDoctor ClinicHoursServer updateClinicHours SQLException: " + e);
		} finally {
			if (con != null) try {con.close();}catch (Exception ignore) {}
		}
	}
}