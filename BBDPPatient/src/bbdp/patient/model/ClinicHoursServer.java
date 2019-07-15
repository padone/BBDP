package bbdp.patient.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ClinicHoursServer {
	//取得現有醫生的醫院科別資料
	public static String getHospital(DataSource datasource) {
		Connection con = null;
		String result = "";
		
		try {
			JSONObject allData = new JSONObject();
			JSONArray hospitalArray = new JSONArray();
		    con = datasource.getConnection();
		    Statement statement = con.createStatement();
		    ResultSet resultSet = statement.executeQuery("SELECT DISTINCT hospital FROM doctor");
		    Statement statement2 = con.createStatement();
		    ResultSet resultSet2 = null;
		    Statement statement3 = con.createStatement();
		    ResultSet resultSet3 = null;
		    
		    while (resultSet.next()) {
		    	JSONObject hospital = new JSONObject();
		    	hospital.put("hospitalName", resultSet.getString("hospital"));
		    	JSONArray departmentArray = new JSONArray();
		    	resultSet2 = statement2.executeQuery("SELECT DISTINCT department FROM doctor WHERE hospital = '" + resultSet.getString("hospital") + "'");
		    	while (resultSet2.next()) {
		    		JSONObject department = new JSONObject();
		    		department.put("departmentName", resultSet2.getString("department"));
		    		JSONArray doctorArray = new JSONArray();
		    		resultSet3 = statement3.executeQuery("SELECT DISTINCT doctorID, name FROM doctor WHERE hospital = '" + resultSet.getString("hospital") + "' AND department = '" + resultSet2.getString("department") + "'");
		    		while (resultSet3.next()) {
		    			JSONObject doctor = new JSONObject();
		    			doctor.put("doctorID", resultSet3.getString("doctorID"));
		    			doctor.put("doctorName", resultSet3.getString("name"));
		    			doctorArray.put(doctor);
		    		}
		    		department.put("doctor", doctorArray);
		    		departmentArray.put(department);
		    	}
		    	hospital.put("department", departmentArray);
		    	hospitalArray.put(hospital);
		    }
		    allData.put("hospital", hospitalArray);
			result = allData.toString();
			resultSet.close();
			statement.close();
			resultSet2.close();
			statement2.close();
			resultSet3.close();
			statement3.close();
		} catch (SQLException e) {
			System.out.println("BBDPPatient ClinicHoursServer getHospital SQLException: " + e);
			result = "";
		} catch (JSONException e) {
			System.out.println("BBDPPatient ClinicHoursServer getHospital JSONException: " + e);
			result = "";
		} finally {
			if (con != null) try {con.close();}catch (Exception ignore) {}
		}
		//System.out.println(result);
		return result;
	}
	
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
			System.out.println("BBDPPatient ClinicHoursServer getClinicHours SQLException: " + e);
			result = "";
		} catch (JSONException e) {
			System.out.println("BBDPPatient ClinicHoursServer getClinicHours JSONException: " + e);
			result = "";
		} finally {
			if (con != null) try {con.close();}catch (Exception ignore) {}
		}
		//System.out.println(result);
		return result;
	}
}