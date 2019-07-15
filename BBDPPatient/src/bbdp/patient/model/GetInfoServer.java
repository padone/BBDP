package bbdp.patient.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetInfoServer {
	//搜尋病患姓名
		public static String getPatientName(Connection conn, String patientID){
			String patientName = "查無病患";
			PreparedStatement statement = null;
			try {
				String sql = "SELECT name FROM patient WHERE patientID = ?";
				statement = conn.prepareStatement(sql);
				statement.setString(1, patientID);
					
				ResultSet resultSet = statement.executeQuery();
				while (resultSet.next()) {
					patientName = resultSet.getString("name");
				}
				if (resultSet != null) try { resultSet.close(); } catch (SQLException ignore) {}
					
				return patientName;
			}
			catch (SQLException ex) {
				System.out.println("發生SQLException");
			}
			finally {
				if (statement != null) try { statement.close(); }catch (SQLException ignore) {}
				if (conn!=null) try {conn.close();}catch (Exception ignore) {}
			}
			return patientName;
		}
			
		//搜尋所有醫院
		public static String searchHospital(Connection conn) {
			PreparedStatement statement = null;
			String jsonString = "";
			JSONArray hospitalArray = new JSONArray();
			try {
				statement = conn.prepareStatement("SELECT DISTINCT hospital FROM doctor");
				ResultSet resultSet = statement.executeQuery();
				//department
				while (resultSet.next()) {
					JSONObject hospitalObject = new JSONObject();
					hospitalObject.put("hospital", resultSet.getString("hospital"));
					hospitalArray.put(hospitalObject);
				}
				if (resultSet != null) try { resultSet.close(); } catch (SQLException ignore) {}
				            
				jsonString = hospitalArray.toString();
				//System.out.println(jsonString);
				return jsonString;
			} 
			catch (SQLException ex) {
				System.out.println("發生SQLException");
			}
			catch (JSONException e) {
				System.out.println("發生JSONException: " + e);
			}
			finally {
				if (statement != null) try { statement.close(); }catch (SQLException ignore) {}
				if (conn!=null) try {conn.close();}catch (Exception ignore) {}
			}
			return jsonString;
		}
			
		//搜尋某家醫院的所有診別
		public static String searchDepartment(Connection conn, String hospital) {
			PreparedStatement statement = null;
			String jsonString = "";
			JSONArray departmentArray = new JSONArray();
			try {
				statement = conn.prepareStatement("SELECT DISTINCT department FROM doctor WHERE hospital = ?");
				statement.setString(1, hospital);
				ResultSet resultSet = statement.executeQuery();
				//department
				while (resultSet.next()) {
					JSONObject departmentObject = new JSONObject();
					departmentObject.put("department", resultSet.getString("department"));
					departmentArray.put(departmentObject);
				}
				if (resultSet != null) try { resultSet.close(); } catch (SQLException ignore) {}
			            
				jsonString = departmentArray.toString();
					//System.out.println(jsonString);
				return jsonString;
			} 
			catch (SQLException ex) {
				System.out.println("發生SQLException");
			}
			catch (JSONException e) {
				System.out.println("發生JSONException: " + e);
			}
			finally {
				if (statement != null) try { statement.close(); }catch (SQLException ignore) {}
				if (conn!=null) try {conn.close();}catch (Exception ignore) {}
			}
			return jsonString;
		}
			
		//搜尋某家醫院某個診別的所有醫生	
		public static String searchDoctor(Connection conn, String hospital, String department) {
			PreparedStatement statement = null;
			String jsonString = "";
			JSONArray doctorArray = new JSONArray();
			try {
				statement = conn.prepareStatement("SELECT doctorID, name FROM doctor WHERE hospital = ? AND department = ?");
				statement.setString(1, hospital);
				statement.setString(2, department);
				ResultSet resultSet = statement.executeQuery();
					//doctorID name
				while (resultSet.next()) {
					JSONObject doctorObject = new JSONObject();
					doctorObject.put("doctorID", resultSet.getString("doctorID"));
					doctorObject.put("name", resultSet.getString("name"));
					doctorArray.put(doctorObject);
				}
				if (resultSet != null) try { resultSet.close(); } catch (SQLException ignore) {}
					jsonString = doctorArray.toString();    
					
					//System.out.println(jsonString);
				return jsonString;
			}
			catch (SQLException ex) {
				System.out.println("發生SQLException");
			}
			catch (JSONException e) {
				System.out.println("發生JSONException: " + e);
			}
			finally {
				if (statement != null) try { statement.close(); }catch (SQLException ignore) {}
				if (conn!=null) try {conn.close();}catch (Exception ignore) {}
			}
			return jsonString;
		}
}
