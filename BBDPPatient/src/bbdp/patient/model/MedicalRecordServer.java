package bbdp.patient.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class MedicalRecordServer {
	public static ArrayList searchHospital(Connection conn, String patientID, ArrayList searchList) {
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select distinct hospital FROM doctor natural join medicalrecord where patientID = '"+patientID+"'");
		    while (rs.next()) {
		    	searchList.add(rs.getString("hospital"));
		    }
			rs.close();
		    st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return searchList;
	}
	public static ArrayList searchDepartment(Connection conn, String patientID, String hospital, ArrayList searchList) {
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select distinct department FROM doctor natural join medicalrecord where hospital = '"+hospital+"' and patientID = '"+patientID+"'");
		    while (rs.next()) {
		    	searchList.add(rs.getString("department"));
		    }
			rs.close();
		    st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return searchList;
	}
	public static ArrayList searchDoctor(Connection conn, String patientID, String hospital, String department, ArrayList searchList) {
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select distinct doctorID,name FROM doctor natural join medicalrecord where hospital = '"+hospital+"' and department = '"+department+"' and patientID = '"+patientID+"'");
		    while (rs.next()) {
		    	searchList.add(rs.getString("doctorID"));
		    	searchList.add(rs.getString("name"));
		    }
			rs.close();
		    st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return searchList;
	}
	public static ArrayList searchDate(Connection conn, String patientID, String doctorID, ArrayList searchList) {
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select * FROM medicalrecord where doctorID = '"+doctorID+"' and patientID = '"+patientID+"' ORDER BY addTime DESC");
		    while (rs.next()) {
		    	searchList.add(rs.getString("medicalRecordID"));
		    	searchList.add(rs.getString("addTime"));
		    }
			rs.close();
		    st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return searchList;
	}
	public static ArrayList getMedicalRecord(Connection conn, String patientID, String medicalRecordID, ArrayList searchList) {
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select * FROM medicalrecord natural join doctor where patientID = '"+patientID+"' and medicalRecordID = '"+medicalRecordID+"'");
		    while (rs.next()) {
		    	searchList.add(rs.getString("hospital"));//0
		    	searchList.add(rs.getString("department"));//1
		    	searchList.add(rs.getString("name"));//2
		    	searchList.add(rs.getString("addTime"));//3
		    	searchList.add(rs.getString("editTime"));//4
		    	searchList.add(rs.getString("content"));//5
		    	searchList.add(rs.getString("doctorID"));//6
		    }
			rs.close();
		    st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return searchList;
	}
}
