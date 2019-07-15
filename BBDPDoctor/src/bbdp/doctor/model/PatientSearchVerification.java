package bbdp.doctor.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PatientSearchVerification {

	public static String searchVerification(Connection conn, String inputID){
		String result = "";
		String patient = "";
		PreparedStatement statement = null;
		
		
		try {
			Statement st = conn.createStatement();
			ResultSet resultSet = st.executeQuery("SELECT patientID,account,name FROM patient WHERE account LIKE '_____" + inputID + "'");
			//搜尋成功
			while (resultSet.next()) {
				//第一個物件
				if(patient.equals("")){
					patient = "{\"patientID\":\"" + resultSet.getString("patientID") + 
							"\",\"account\":\"" + resultSet.getString("account") + 
								"\",\"name\":\"" + resultSet.getString("name") + "\"}";
				}
				else{
					patient += ",{\"patientID\":\"" + resultSet.getString("patientID") + 
							"\",\"account\":\"" + resultSet.getString("account") + 
							"\",\"name\":\"" + resultSet.getString("name") + "\"}";
				}
				result = "[" + patient +"]";				
			}
			
			//搜尋失敗
			if(result.equals("")){	
				result = "fail";
			}
			
			if (resultSet != null) try { resultSet.close();} catch (SQLException ignore) {}
			st.close();
			return result;
		}
		catch (SQLException e) {
			result = "SQLException";
			return result;
		}
		finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		
	}
}
