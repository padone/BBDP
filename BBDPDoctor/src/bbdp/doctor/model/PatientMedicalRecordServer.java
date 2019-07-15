package bbdp.doctor.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;

public class PatientMedicalRecordServer {
	synchronized public static int newMedicalRecord(Connection conn, String doctorID, String patientID, String medicalRecord) {
		int returnInt = 0;
		String medicalRecordID = "";
		java.text.DateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar now = Calendar.getInstance();
		try {
			Statement st = conn.createStatement();			
			ResultSet rs = st.executeQuery("select ifNULL(max(medicalRecordID+0),0)+1 FROM medicalrecord");
			if(rs.next()) medicalRecordID = rs.getString("ifNULL(max(medicalRecordID+0),0)+1");
			returnInt = st.executeUpdate("INSERT INTO medicalrecord (medicalRecordID,addTime,editTime,patientID,doctorID,content) VALUES('"+medicalRecordID+"','"+sdf.format(now.getTime())+"','"+sdf.format(now.getTime())+"','"+patientID+"','"+doctorID+"','"+medicalRecord+"')");
			if(returnInt != 0){
				rs = st.executeQuery("select name FROM doctor where doctorID = '"+doctorID+"'");
				if (rs.next()) {
					String name = rs.getString("name");
					bbdp.push.fcm.PushToFCM.sendNotification("BBDP", name + " 醫師新增了您的病歷", patientID, "MedicalRecord.html?num="+medicalRecordID);
				}
			}
			rs.close();
		    st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}	
		return returnInt;
	}
	public static ArrayList selectMedicalRecordDate(Connection conn, String doctorID, String patientID, String dateRange, ArrayList dateList) {
		ArrayList temp = new ArrayList();

		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select distinct addTime FROM medicalrecord where doctorID='"+doctorID+"' and patientID = '"+patientID+"' ORDER BY CAST(addTime AS UNSIGNED) DESC");
		    while (rs.next()) {
		    	temp.add(rs.getString("addTime"));
		    }
			rs.close();
		    st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		String tempYear="";
		String tempMonth="";
		if(dateRange.equals("year")){		
			for(int i = 0;i<temp.size();i++){
				String[] AfterSplit = ((String)temp.get(i)).split("-");
				if(tempYear.equals(AfterSplit[0])){
				}else{
					dateList.add(AfterSplit[0]);
					tempYear = AfterSplit[0];
				}
			}			
		}else{
			for(int i = 0;i<temp.size();i++){
				String[] AfterSplit = ((String)temp.get(i)).split("-");
				if(tempYear.equals(AfterSplit[0]) && tempMonth.equals(AfterSplit[1])){
					
				}else{
					dateList.add(AfterSplit[0]+"/"+AfterSplit[1]);
					tempYear = AfterSplit[0];
					tempMonth = AfterSplit[1];
				}
			}				
		}
		return dateList;
	}
	public static ArrayList getMedicalRecordList(Connection conn, String doctorID, String patientID, String dateRange, String date, ArrayList medicalRecordList){	
		String searchTemp="";

		if(date.equals("")){
			dateRange = "";		
		}		
		switch(dateRange){
			case "year":
				searchTemp = "select * FROM medicalrecord where doctorID = '"+doctorID+"' and patientID = '"+patientID+"' and (addTime BETWEEN '"+date+"/01/01' and '"+date+"/12/31') ORDER BY CAST(addTime AS UNSIGNED) DESC";
			break;
			case "month":
				searchTemp = "select * FROM medicalrecord where doctorID = '"+doctorID+"' and patientID = '"+patientID+"' and (addTime BETWEEN '"+date+"/01' and '"+date+"/31') ORDER BY CAST(addTime AS UNSIGNED) DESC";
			break;
			default: 
				searchTemp = "select * FROM medicalrecord where doctorID = '"+doctorID+"' and patientID = '"+patientID+"' ORDER BY CAST(addTime AS UNSIGNED) DESC";
		}

		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(searchTemp);
			while (rs.next()) {
				medicalRecordList.add(rs.getString("medicalRecordID"));//0
				medicalRecordList.add(rs.getString("addTime"));//1
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			  if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}		
		return medicalRecordList;
	}
	public static String checkMedicalRecordID(Connection conn, String doctorID, String patientID, String medicalRecordID) {
		String returnString = "";
		
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select * FROM medicalrecord where doctorID='"+doctorID+"' and patientID = '"+patientID+"' and medicalRecordID = '"+medicalRecordID+"'");
		    if(rs.next()) {
		        returnString = "Yes";
		    }
			rs.close();
		    st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return returnString;
	}
	public static ArrayList getMedicalRecord(Connection conn, String doctorID, String patientID, String medicalRecordID, ArrayList medicalRecordList){	

		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("select * FROM medicalrecord where doctorID='"+doctorID+"' and patientID = '"+patientID+"' and medicalRecordID = '"+medicalRecordID+"'");
			while (rs.next()) {
				medicalRecordList.add(rs.getString("addTime"));//0
				medicalRecordList.add(rs.getString("editTime"));//1
				medicalRecordList.add(rs.getString("content"));//2
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			  if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}		
		return medicalRecordList;
	}
	public static int editMedicalRecord(Connection conn, String doctorID, String patientID, String medicalRecordID, String medicalRecord) {
		int returnInt = 0;
		java.text.DateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar now = Calendar.getInstance();
		try {
			Statement st = conn.createStatement();	
			returnInt = st.executeUpdate("UPDATE medicalrecord SET editTime = '"+sdf.format(now.getTime())+"' , content = '"+medicalRecord+"' WHERE medicalRecordID = '"+medicalRecordID+"'");
		    st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}	
		return returnInt;
	}
}
