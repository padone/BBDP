package bbdp.patient.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.tomcat.jdbc.pool.DataSource;

import com.google.gson.Gson;

public class SystemServer {
	//system patients class//移除病患ID// 該病患ID是否在patients裡面
	class SystemPatients {
		List<Integer> patientID;
		SystemPatients(List<Integer> patientID){
			this.patientID = patientID;
		}
		
		//移除病患ID
		void removePatientID(int patientID){
			for(int i = 0; i < this.patientID.size(); i++){
				if(this.patientID.get(i) == patientID){
					this.patientID.remove(i);
					break;
				}
			}
		}
		
		// 該病患ID是否在patients裡面
		boolean isInPatients(int patientID){
			for(int i = 0; i < this.patientID.size(); i++){
				if(this.patientID.get(i) == patientID){
					return true;
				}
			}
			return false;
		}
	}
	
	// 更新system patients	
	public boolean updatePatients(DataSource datasource, String patientID, String patients) {
		Connection con = null;
		Gson gson = new Gson();
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			
			// 更新patients
		    int update = st.executeUpdate("update system set patients = '"+patients+"' where systemID = '1' ");
		    st.close();//關閉st

            return update > 0;
		} catch (SQLException e) {
			System.out.println("LoginVerification updatePatients Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return true;
	}
	// case1 如果人數超過上限，就把最舊的那個人刪掉	// case2 如果人數沒有超過上限，就直接加入
	public String newpatients(DataSource datasource, String patientID) {
		Gson gson = new Gson();
		HashMap getSystem =  getSystem(datasource, patientID);	// 取得system的numberlimit跟patients
		String patients = getSystem.get("patients").toString();
		String numberlimit = getSystem.get("numberlimit").toString();

		SystemPatients patientList = gson.fromJson(patients, SystemPatients.class);	//解析json 
		if(!patientList.isInPatients(Integer.valueOf(patientID))){	// 該patientID不在patients裡才要去移除舊的和新增新的，在patients的話，就不用改了
			if(patientList.patientID.size() >= Integer.valueOf(numberlimit)){	// 如果已經到達可登入人數上限
				int gap = patientList.patientID.size() - Integer.valueOf(numberlimit);
				for(int i = 0; i <= gap; i++){
					patientList.patientID.remove(0);	// 移除最舊的patientID
				}
			}
			patientList.patientID.add(Integer.valueOf(patientID));	// 新增現在的patientID
		}
		return gson.toJson(patientList);	// 轉成json
	} 
	
	// 取得system (numberlimit、patients)	
	public HashMap getSystem(DataSource datasource, String patientID) {
		Connection con = null;
		Gson gson = new Gson();
		HashMap getSystem = new HashMap();

		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select numberlimit, patients from system where systemID = '1' ");
			
			while (rs.next()) {
				getSystem.put("numberlimit", rs.getString("numberlimit"));
				getSystem.put("patients", rs.getString("patients"));
			}
			rs.close();//關閉rs
		} catch (SQLException e) {
			System.out.println("LoginVerification getSystem Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return getSystem;
	}

	// 判斷該病患ID是否在patients裡面
	public boolean isInPatients(DataSource datasource, String patientID) {
		Gson gson = new Gson();
		HashMap getSystem =  getSystem(datasource, patientID);
		String patients = getSystem.get("patients").toString();
			
		SystemPatients patientList = gson.fromJson(patients, SystemPatients.class);	//解析json 
		return patientList.isInPatients(Integer.valueOf(patientID));
	}
	
	// 移除病患ID
	public boolean removePatientID(DataSource datasource, String patientID) {
		Connection con = null;
		Gson gson = new Gson();
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			
			HashMap getSystem =  getSystem(datasource, patientID);	// 取得system的numberlimit跟patients
			String patients = getSystem.get("patients").toString();

			SystemPatients patientList = gson.fromJson(patients, SystemPatients.class);	//解析json 
			patientList.removePatientID(Integer.valueOf(patientID));	// 移除病患ID
			// 更新patients
		    int delete = st.executeUpdate("update system set patients = '"+gson.toJson(patientList)+"' where systemID = '1' ");
		    st.close();//關閉st

            return delete > 0;
		} catch (SQLException e) {
			System.out.println("LoginVerification removePatientID Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return true;
	}

	// 更新優先順序至最新	// 先刪掉在新增，就會變成最新的了
	public String firstPatients(DataSource datasource, String patientID) {
		Gson gson = new Gson();
		HashMap getSystem =  getSystem(datasource, patientID);	// 取得system的numberlimit跟patients
		String patients = getSystem.get("patients").toString();
		//String numberlimit = getSystem.get("numberlimit").toString();

		SystemPatients patientList = gson.fromJson(patients, SystemPatients.class);	//解析json 
		patientList.removePatientID(Integer.valueOf(patientID));	// 移除病患ID
		patientList.patientID.add(Integer.valueOf(patientID));	// 新增現在的patientID
		return gson.toJson(patientList);	// 轉成json
	}
}
