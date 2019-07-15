package bbdp.patient.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.jasper.tagplugins.jstl.core.Out;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import com.google.gson.Gson;
import com.google.gson.JsonElement;


public class PatientInstructionServer {
	//AllOfPatientInstructions.html//取得科別
	public ArrayList getDepartment(DataSource datasource) {
		Connection con = null;
		ArrayList departmentList = new ArrayList();
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select distinct department from patientinstruction natural join doctor");
		
			while (rs.next()) {
				departmentList.add(rs.getString("department"));
			}
			rs.close();//關閉rs
			st.close();//關閉st
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer getDepartment Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return departmentList;
	}
	
	//AllOfPatientInstructions.html//取得症狀
	public ArrayList getSymptom(DataSource datasource, String select) {
		Connection con = null;
		ArrayList symptomList = new ArrayList();
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select distinct symptom from patientinstruction natural join doctor where department = '"+select+"' ");
		
			while (rs.next()) {
				symptomList.add(rs.getString("symptom"));
			}
			rs.close();//關閉rs
			st.close();//關閉st
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer getSymptom Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return symptomList;
	}
	
	//AllOfPatientInstructions.html//取得醫生
	public HashMap getDoctor(DataSource datasource, String select) {
		Connection con = null;
		HashMap getDoctor = new HashMap();

		ArrayList doctorIDList = new ArrayList();
		ArrayList nameList = new ArrayList();

		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select distinct doctorID, name from patientinstruction natural join doctor where department = '"+select+"' ");
		
			while (rs.next()) {
				doctorIDList.add(rs.getString("doctorID"));
				nameList.add(rs.getString("name"));
			}
			rs.close();//關閉rs
			st.close();//關閉st
			getDoctor.put("doctorIDList", doctorIDList);
			getDoctor.put("nameList", nameList);

		} catch (SQLException e) {
			System.out.println("PatientInstructionServer getDoctor Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return getDoctor;
	}
	
	//AllOfPatientInstructions.html//取得訂閱的衛教資訊//改成發布時間date排序
	public HashMap getAllList(DataSource datasource) {
		Connection con = null;
		Gson gson = new Gson();

		HashMap getList = new HashMap();
		
		ArrayList patientInstructionIDList = new ArrayList();
		ArrayList symptomList = new ArrayList();
		ArrayList titleList = new ArrayList();
		ArrayList hospitalList = new ArrayList();
		ArrayList departmentList = new ArrayList();
		ArrayList nameList = new ArrayList();

		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select patientInstructionID, symptom, title, hospital, department, name from patientinstruction natural join doctor order by date desc");
			
			while (rs.next()) {
				patientInstructionIDList.add(rs.getString("patientInstructionID"));
				symptomList.add(rs.getString("symptom"));
				titleList.add(rs.getString("title"));
				hospitalList.add(rs.getString("hospital"));
				departmentList.add(rs.getString("department"));
				nameList.add(rs.getString("name"));
			}
			rs.close();//關閉rs
			st.close();//關閉st

			getList.put("patientInstructionIDList", patientInstructionIDList);
			getList.put("symptomList", symptomList);
			getList.put("titleList", titleList);
			getList.put("hospitalList", hospitalList);
			getList.put("departmentList", departmentList);
			getList.put("nameList", nameList);
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer getAllList Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return getList;
	}

	//AllOfPatientInstructions.html//取得該科別的所有衛教文章//改成發布時間date排序
	public HashMap getDepartmentList(DataSource datasource, String department) {
		Connection con = null;
		Gson gson = new Gson();

		HashMap getList = new HashMap();
		
		ArrayList patientInstructionIDList = new ArrayList();
		ArrayList symptomList = new ArrayList();
		ArrayList titleList = new ArrayList();
		ArrayList hospitalList = new ArrayList();
		ArrayList departmentList = new ArrayList();
		ArrayList nameList = new ArrayList();

		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select patientInstructionID, symptom, title, hospital, department, name from patientinstruction natural join doctor where department = '"+department+"' order by date desc");
			
			while (rs.next()) {
				patientInstructionIDList.add(rs.getString("patientInstructionID"));
				symptomList.add(rs.getString("symptom"));
				titleList.add(rs.getString("title"));
				hospitalList.add(rs.getString("hospital"));
				departmentList.add(rs.getString("department"));
				nameList.add(rs.getString("name"));
			}
			rs.close();//關閉rs
			st.close();//關閉st

			getList.put("patientInstructionIDList", patientInstructionIDList);
			getList.put("symptomList", symptomList);
			getList.put("titleList", titleList);
			getList.put("hospitalList", hospitalList);
			getList.put("departmentList", departmentList);
			getList.put("nameList", nameList);
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer getDepartmentList Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return getList;
	}
	
	//AllOfPatientInstructions.html//取得該症狀的所有衛教文章//改成發布時間date排序
	public HashMap getSymptomList(DataSource datasource, String department, String select) {
		Connection con = null;
		Gson gson = new Gson();

		HashMap getList = new HashMap();
		
		ArrayList patientInstructionIDList = new ArrayList();
		ArrayList symptomList = new ArrayList();
		ArrayList titleList = new ArrayList();
		ArrayList hospitalList = new ArrayList();
		ArrayList departmentList = new ArrayList();
		ArrayList nameList = new ArrayList();

		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select patientInstructionID, symptom, title, hospital, department, name from patientinstruction natural join doctor where department = '"+department+"' and symptom = '"+select+"' order by date desc");
			
			while (rs.next()) {
				patientInstructionIDList.add(rs.getString("patientInstructionID"));
				symptomList.add(rs.getString("symptom"));
				titleList.add(rs.getString("title"));
				hospitalList.add(rs.getString("hospital"));
				departmentList.add(rs.getString("department"));
				nameList.add(rs.getString("name"));
			}
			rs.close();//關閉rs
			st.close();//關閉st

			getList.put("patientInstructionIDList", patientInstructionIDList);
			getList.put("symptomList", symptomList);
			getList.put("titleList", titleList);
			getList.put("hospitalList", hospitalList);
			getList.put("departmentList", departmentList);
			getList.put("nameList", nameList);
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer getSymptomList Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return getList;
	}
	
	//AllOfPatientInstructions.html//取得該醫生的所有衛教文章//改成發布時間date排序
	public HashMap getDoctorList(DataSource datasource, String department, String select) {
		Connection con = null;
		Gson gson = new Gson();

		HashMap getList = new HashMap();
		
		ArrayList patientInstructionIDList = new ArrayList();
		ArrayList symptomList = new ArrayList();
		ArrayList titleList = new ArrayList();
		ArrayList hospitalList = new ArrayList();
		ArrayList departmentList = new ArrayList();
		ArrayList nameList = new ArrayList();

		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select patientInstructionID, symptom, title, hospital, department, name from patientinstruction natural join doctor where department = '"+department+"' and doctorID = '"+select+"' order by date desc");
			
			while (rs.next()) {
				patientInstructionIDList.add(rs.getString("patientInstructionID"));
				symptomList.add(rs.getString("symptom"));
				titleList.add(rs.getString("title"));
				hospitalList.add(rs.getString("hospital"));
				departmentList.add(rs.getString("department"));
				nameList.add(rs.getString("name"));
			}
			rs.close();//關閉rs
			st.close();//關閉st

			getList.put("patientInstructionIDList", patientInstructionIDList);
			getList.put("symptomList", symptomList);
			getList.put("titleList", titleList);
			getList.put("hospitalList", hospitalList);
			getList.put("departmentList", departmentList);
			getList.put("nameList", nameList);
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer getDoctorList Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return getList;
	}
		
	/*******************************************************************************************/
	//訂閱:症狀跟醫生ID
	class Subscription {
		List<String> symptom;
		List<Integer> doctorID;
		Subscription(List<String> symptom, List<Integer> doctorID){
			this.symptom = symptom;
			this.doctorID = doctorID;
		}
		
		//移除醫生ID
		void removeDoctorID(int doctorID){
			for(int i = 0; i < this.doctorID.size(); i++){
				if(this.doctorID.get(i) == doctorID){
					this.doctorID.remove(i);
				}
			}
		}
	}
		
	//Subscription.html//取得該病患的訂閱
	public String getSubscription(DataSource datasource, String patientID) {
		Connection con = null;
		String subscription = null;
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select subscription from subscription where patientID = '"+patientID+"' ");
			while (rs.next()) {
				subscription = rs.getString("subscription");
			}
			rs.close();
			st.close();//關閉st
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer getSubscription Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return subscription;
	}

	//Subscription.html//取得訂閱的科別(症狀)
	public ArrayList getSymptomDepartment(DataSource datasource, String patientID) {
		Connection con = null;
		Gson gson = new Gson();
		ArrayList departmentList = new ArrayList();
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			
			String subscription = getSubscription(datasource, patientID);	//取得訂閱
			Subscription temp = gson.fromJson(subscription, Subscription.class);	//解析
			
			if(temp != null && temp.symptom.size() > 0){
				String sql = "select distinct department from patientinstruction natural join doctor where ";
				for(int i = 0; i < temp.symptom.size(); i++){	//取得症狀
					sql += " symptom = '" + temp.symptom.get(i) + "' || ";
				}
				sql = sql.substring(0, sql.length()-3);	//去掉最後多餘||
				
				ResultSet rs = st.executeQuery(sql);
				while (rs.next()) {
					departmentList.add(rs.getString("department"));
				}
				rs.close();//關閉rs
			}
			
			st.close();//關閉st
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer getSymptomDepartment Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return departmentList;
	}
		
	//Subscription.html.html//取得有訂閱的症狀
	public ArrayList getSubscriptionSymptom(DataSource datasource, String patientID, String select) {
		Connection con = null;
		Gson gson = new Gson();
		ArrayList symptomList = new ArrayList();
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
		
			String subscription = getSubscription(datasource, patientID);	//取得訂閱
			Subscription temp = gson.fromJson(subscription, Subscription.class);	//解析
			
			if(temp != null){	//// && (temp.symptom.size() > 0 || temp.doctorID.size() > 0)
				if(temp.symptom.size() > 0){
					ResultSet rs = st.executeQuery("select distinct symptom from patientinstruction natural join doctor where department = '"+select+"' ");
					while (rs.next()) {
						for(int i = 0; i < temp.symptom.size(); i++){	//取得症狀
							if(temp.symptom.get(i).equals(rs.getString("symptom"))){
								symptomList.add(rs.getString("symptom"));
							}
						}
					}
					rs.close();//關閉rs
				}	
			}

			st.close();//關閉st
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer getSubscriptionSymptom Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return symptomList;
	}
	
	//Subscription.html//取得有訂閱的科別(醫生)
	public ArrayList getDoctorDepartment(DataSource datasource, String patientID) {
		Connection con = null;
		Gson gson = new Gson();
		ArrayList departmentList = new ArrayList();
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			
			String subscription = getSubscription(datasource, patientID);	//取得訂閱
			Subscription temp = gson.fromJson(subscription, Subscription.class);	//解析
			
			if(temp != null && temp.doctorID.size() > 0){
				String sql = "select distinct department from patientinstruction natural join doctor where ";
				for(int i = 0; i < temp.doctorID.size(); i++){	//取得症狀
					sql += " doctorID = '" + temp.doctorID.get(i) + "' || ";
				}
				sql = sql.substring(0, sql.length()-3);	//去掉最後多餘||

				ResultSet rs = st.executeQuery(sql);
				while (rs.next()) {
					departmentList.add(rs.getString("department"));
				}
				rs.close();//關閉rs
			}
			
			st.close();//關閉st
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer getDoctorDepartment Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return departmentList;
	}
	
	//Subscription.html//取得有訂閱的醫生
	public HashMap getSubscriptionDoctor(DataSource datasource, String patientID, String select) {
		Connection con = null;
		Gson gson = new Gson();
		HashMap getSubscriptionDoctor = new HashMap();
		ArrayList doctorIDList = new ArrayList();
		ArrayList nameList = new ArrayList();

		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
		
			String subscription = getSubscription(datasource, patientID);	//取得訂閱
			Subscription temp = gson.fromJson(subscription, Subscription.class);	//解析
			
			if(temp != null){	//// && (temp.symptom.size() > 0 || temp.doctorID.size() > 0)
				if(temp.doctorID.size() > 0){
					ResultSet rs = st.executeQuery("select distinct doctorID, name from patientinstruction natural join doctor where department = '"+select+"' ");
					while (rs.next()) {
						for(int i = 0; i < temp.doctorID.size(); i++){	//取得症狀
							if(temp.doctorID.get(i).toString().equals(rs.getString("doctorID"))){	//因為是數字，所以轉成String來比較
								doctorIDList.add(rs.getString("doctorID"));
								nameList.add(rs.getString("name"));
							}
						}
					}
					rs.close();//關閉rs
				}	
			}
			
			st.close();//關閉st
			
			getSubscriptionDoctor.put("doctorIDList", doctorIDList);
			getSubscriptionDoctor.put("nameList", nameList);
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer getSubscriptionDoctor Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return getSubscriptionDoctor;
	}
	
	//Subscription.html//取得該科別訂閱的所有衛教文章(症狀)//改成發布時間date排序
	public HashMap getSymptomDepartmentList(DataSource datasource, String patientID, String department) {
		Connection con = null;
		Gson gson = new Gson();

		HashMap getList = new HashMap();
		
		ArrayList patientInstructionIDList = new ArrayList();
		ArrayList symptomList = new ArrayList();
		ArrayList titleList = new ArrayList();
		ArrayList hospitalList = new ArrayList();
		ArrayList departmentList = new ArrayList();
		ArrayList nameList = new ArrayList();

		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			
			String subscription = getSubscription(datasource, patientID);	//取得訂閱
			Subscription temp = gson.fromJson(subscription, Subscription.class);	//解析
			
			if(temp.symptom.size() > 0){
				String sql = "select patientInstructionID, symptom, title, hospital, department, name from patientinstruction natural join doctor where department = '"+department+"' && ( ";
				for(int i = 0; i < temp.symptom.size(); i++){	//取得症狀
					sql += " symptom = '" + temp.symptom.get(i) + "' || ";
				}
				sql = sql.substring(0, sql.length()-3) + " ) order by date desc";	//去掉最後多餘||

				ResultSet rs = st.executeQuery(sql);
				while (rs.next()) {
					patientInstructionIDList.add(rs.getString("patientInstructionID"));
					symptomList.add(rs.getString("symptom"));
					titleList.add(rs.getString("title"));
					hospitalList.add(rs.getString("hospital"));
					departmentList.add(rs.getString("department"));
					nameList.add(rs.getString("name"));
				}
				rs.close();//關閉rs
			}
			
			st.close();//關閉st


			getList.put("patientInstructionIDList", patientInstructionIDList);
			getList.put("symptomList", symptomList);
			getList.put("titleList", titleList);
			getList.put("hospitalList", hospitalList);
			getList.put("departmentList", departmentList);
			getList.put("nameList", nameList);
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer getSymptomDepartmentList Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return getList;
	}

	//Subscription.html//顯示該科別訂閱的所有衛教文章(醫生)//改成發布時間date排序
	public HashMap getDoctorDepartmentList(DataSource datasource, String patientID, String department) {
		Connection con = null;
		Gson gson = new Gson();

		HashMap getList = new HashMap();
		
		ArrayList patientInstructionIDList = new ArrayList();
		ArrayList symptomList = new ArrayList();
		ArrayList titleList = new ArrayList();
		ArrayList hospitalList = new ArrayList();
		ArrayList departmentList = new ArrayList();
		ArrayList nameList = new ArrayList();

		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			
			String subscription = getSubscription(datasource, patientID);	//取得訂閱
			Subscription temp = gson.fromJson(subscription, Subscription.class);	//解析
			
			if(temp.symptom.size() > 0){
				String sql = "select patientInstructionID, symptom, title, hospital, department, name from patientinstruction natural join doctor where department = '"+department+"' && ( ";
				for(int i = 0; i < temp.doctorID.size(); i++){	//取得醫生
					sql += " doctorID = '" + temp.doctorID.get(i) + "' || ";
				}
				sql = sql.substring(0, sql.length()-3) + " ) order by date desc";	//去掉最後多餘||

				ResultSet rs = st.executeQuery(sql);
				while (rs.next()) {
					patientInstructionIDList.add(rs.getString("patientInstructionID"));
					symptomList.add(rs.getString("symptom"));
					titleList.add(rs.getString("title"));
					hospitalList.add(rs.getString("hospital"));
					departmentList.add(rs.getString("department"));
					nameList.add(rs.getString("name"));
				}
				rs.close();//關閉rs
			}
			
			st.close();//關閉st


			getList.put("patientInstructionIDList", patientInstructionIDList);
			getList.put("symptomList", symptomList);
			getList.put("titleList", titleList);
			getList.put("hospitalList", hospitalList);
			getList.put("departmentList", departmentList);
			getList.put("nameList", nameList);
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer getDoctorDepartmentList Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return getList;
	}

	
	
	
	
	//Subscription.html//顯示訂閱的所有衛教文章(症狀或醫生)//改成發布時間date排序
	public HashMap getSubscriptionList(DataSource datasource, String patientID, String stateType) {
		Connection con = null;
		Gson gson = new Gson();

		HashMap getList = new HashMap();
		
		ArrayList patientInstructionIDList = new ArrayList();
		ArrayList symptomList = new ArrayList();
		ArrayList titleList = new ArrayList();
		ArrayList hospitalList = new ArrayList();
		ArrayList departmentList = new ArrayList();
		ArrayList nameList = new ArrayList();

		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			
			String subscription = getSubscription(datasource, patientID);	//取得訂閱
			Subscription temp = gson.fromJson(subscription, Subscription.class);	//解析
			
			if(temp != null){	//// && (temp.symptom.size() > 0 || temp.doctorID.size() > 0)
				ResultSet rs = null;	//預設null
				if(temp.symptom.size() > 0 && stateType.equals("symptom")){
					String sql = "select patientInstructionID, symptom, title, hospital, department, name from patientinstruction natural join doctor where ";
					for(int i = 0; i < temp.symptom.size(); i++){	//取得症狀
						sql += " symptom = '" + temp.symptom.get(i) + "' || ";
					}
					sql = sql.substring(0, sql.length()-3) + " order by date desc ";	//去掉最後多餘||
					
					rs = st.executeQuery(sql);	//執行
				}
				else if(temp.doctorID.size() > 0 && stateType.equals("doctor")){
					String sql = "select patientInstructionID, symptom, title, hospital, department, name from patientinstruction natural join doctor where ";
					for(int i = 0; i < temp.doctorID.size(); i++){	//取得醫生
						sql += " doctorID = '" + temp.doctorID.get(i) + "' || ";
					}
					sql = sql.substring(0, sql.length()-3) + " order by date desc ";	//去掉最後多餘||
					
					rs = st.executeQuery(sql);	//執行
				}
				if(rs != null){
					while (rs.next()) {
						patientInstructionIDList.add(rs.getString("patientInstructionID"));
						symptomList.add(rs.getString("symptom"));
						titleList.add(rs.getString("title"));
						hospitalList.add(rs.getString("hospital"));
						departmentList.add(rs.getString("department"));
						nameList.add(rs.getString("name"));
					}
					rs.close();//關閉rs
					st.close();//關閉st
				}
			}

			getList.put("patientInstructionIDList", patientInstructionIDList);
			getList.put("symptomList", symptomList);
			getList.put("titleList", titleList);
			getList.put("hospitalList", hospitalList);
			getList.put("departmentList", departmentList);
			getList.put("nameList", nameList);
			
			System.out.println("getSubscriptionList : " + getList);
			
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer getSubscriptionList Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return getList;
	}

	
	
	
	//Subscription.html//刪除訂閱
	public boolean deleteSubscription(DataSource datasource, String patientID, String select, String stateType) {
		Connection con = null;
		Gson gson = new Gson();
		Subscription temp = null;
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			
			String subscription = getSubscription(datasource, patientID);	//取得訂閱
			temp = gson.fromJson(subscription, Subscription.class);	//解析
			
			if(stateType.equals("doctor")){
				temp.removeDoctorID((Integer.parseInt(select)));	//刪除
			}
			else if(stateType.equals("symptom")){
				temp.symptom.remove(select);	//刪除
			}
			
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer deleteSubscription Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return setSubscription(datasource, patientID, gson.toJson(temp));	//更新
	}
	
	//更新該病患的訂閱
	public boolean setSubscription(DataSource datasource, String patientID, String subscription){
		Connection con = null;
		Gson gson = new Gson();
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
		    int update = st.executeUpdate("update subscription set subscription='"+subscription+"' where patientID='"+patientID+"' ");
		    st.close();//關閉st

            return update > 0;
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer setSubscription Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return true;
	}

	/*******************************************************************************************/
	
	//NewSubscription.html.html//新增訂閱
	synchronized public boolean newSubscription(DataSource datasource,String patientID, String select, String stateType) {
		Connection con = null;
		Gson gson = new Gson();
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			String subscription = "", updateSql;
			
			//確認訂閱是否有此病患，沒有的話新增一個，有的話更新z
			subscription = getSubscription(datasource, patientID);
			System.out.println("newSubscription subscription : " + subscription);
			
			if(subscription == null){	//新增
				subscription = "{\"symptom\":[],\"doctorID\":[]}";	//給予空的新增
				Subscription temp = gson.fromJson(subscription, Subscription.class);	//解析
				if(stateType.equals("doctor")){
					temp.doctorID.add(Integer.parseInt(select));	//加入
				}
				else if(stateType.equals("symptom")){
					temp.symptom.add(select);	//加入
				}
				updateSql = "insert into subscription(patientID, subscription) values('"+patientID+"','"+gson.toJson(temp)+"')";
			}
			else{	//更新
				Subscription temp = gson.fromJson(subscription, Subscription.class);	//解析
				if(stateType.equals("doctor")){
					temp.doctorID.add(Integer.parseInt(select));	//加入
				}
				else if(stateType.equals("symptom")){
					temp.symptom.add(select);	//加入
				}
				updateSql = "update subscription set subscription='"+gson.toJson(temp)+"' where patientID='"+patientID+"' ";
			}
			
		    int update = st.executeUpdate(updateSql);
		    st.close();//關閉st

            return update > 0;
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer newSubscription Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return true;
	}
	
	//NewSubscription.html//此症狀是否訂閱//此醫生是否訂閱
	public boolean isSubscription(DataSource datasource, String patientID, String select, String stateType) {
		Connection con = null;
		Gson gson = new Gson();
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			
			String subscription = getSubscription(datasource, patientID);	//取得訂閱
			Subscription temp = gson.fromJson(subscription, Subscription.class);	//解析
			if(temp == null){ return false;}	//無任何訂閱，直接回傳false
			if(stateType.equals("doctor")){
				for(int i = 0; i < temp.doctorID.size(); i++){
					if(temp.doctorID.get(i).toString().equals(select)){	//因為是數字，所以轉成String來比較
						return true;
					}
				}
			}
			else if(stateType.equals("symptom")){
				for(int i = 0; i < temp.symptom.size(); i++){
					if(temp.symptom.get(i).equals(select)){
						return true;
					}
				}
			}
			st.close();//關閉st
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer isSubscription Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return false;
	}

	/*******************************************************************************************/
	
	//PatientInstruction.html//取得單一的衛教資訊
	public HashMap getInstruction(DataSource datasource, String patientInstructionID) {
		HashMap getInstruction = new HashMap();
		Connection con = null;
		
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select symptom, type, date, title, content, editDate, doctorID, hospital, department, name from patientinstruction natural join doctor where patientInstructionID='"+patientInstructionID+"' ");
			while (rs.next()) {
				getInstruction.put("symptom", rs.getString("symptom"));
				getInstruction.put("type", rs.getString("type"));
				getInstruction.put("date", rs.getString("date").substring(0, 16));
				getInstruction.put("title", rs.getString("title"));
				getInstruction.put("content", rs.getString("content"));
				getInstruction.put("editDate", rs.getString("editDate").substring(0, 16));
				getInstruction.put("doctorID", rs.getString("doctorID"));
				getInstruction.put("hospital", rs.getString("hospital"));
				getInstruction.put("department", rs.getString("department"));
				getInstruction.put("name", rs.getString("name"));
			}
			rs.close();//關閉rs
			
			st.close();//關閉st
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer getInstruction Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return getInstruction;
	}

	//PatientInstruction.html//取得留言資訊
	public HashMap getComment(DataSource datasource, String patientInstructionID, String patientID) {
		HashMap getComment = new HashMap();
		Connection con = null;
		ArrayList commentIDList = new ArrayList();
		ArrayList patientIDList = new ArrayList();
		ArrayList time_1List = new ArrayList();
		ArrayList comment_1List = new ArrayList();
		ArrayList time_2List = new ArrayList();
		ArrayList comment_2List = new ArrayList();
		ArrayList hideImgList = new ArrayList();
		ArrayList doctorIDList = new ArrayList();
		ArrayList nameList = new ArrayList();
		
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select commentID, patientID, time_1, comment_1, time_2, comment_2, hideImg, doctorID, name, sex from comment natural join patientinstruction natural join patient where patientInstructionID='"+patientInstructionID+"' and patient.patientID=comment.patientID order by (commentID+0) desc");
			while (rs.next()) {
				commentIDList.add(rs.getString("commentID"));
				patientIDList.add(rs.getString("patientID"));
				time_1List.add(rs.getString("time_1").substring(0, 16));
				comment_1List.add(rs.getString("comment_1"));
				
				if(rs.getString("time_2") != null)	//因為醫師可能沒有回覆
					time_2List.add(rs.getString("time_2").substring(0, 16));
				else
					time_2List.add(rs.getString("time_2"));
				
				comment_2List.add(rs.getString("comment_2"));
				hideImgList.add(rs.getString("hideImg"));
				doctorIDList.add(rs.getString("doctorID"));
				
				//姓名判斷//如果匿名，但本人登入可以看到
				if(rs.getString("hideImg").equals("1") && !rs.getString("patientID").equals(patientID)){	//不顯示姓名且非本人登入
					if(rs.getString("sex").equals("女"))
						nameList.add(rs.getString("name").substring(0, 1) + "小姐");
					else if(rs.getString("sex").equals("男"))
						nameList.add(rs.getString("name").substring(0, 1) + "先生");
				}
				else if(rs.getString("hideImg").equals("0") || rs.getString("patientID").equals(patientID)){	//顯示姓名或雖然匿名但為本人登入
					nameList.add(rs.getString("name"));
				}
			}
			rs.close();//關閉rs
			
			st.close();//關閉st
			getComment.put("commentIDList", commentIDList);
			getComment.put("patientIDList", patientIDList);
			getComment.put("time_1List", time_1List);
			getComment.put("comment_1List", comment_1List);
			getComment.put("time_2List", time_2List);
			getComment.put("comment_2List", comment_2List);
			getComment.put("hideImgList", hideImgList);
			getComment.put("doctorIDList", doctorIDList);
			getComment.put("nameList", nameList);
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer getComment Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return getComment;
	}

	//PatientInstruction.html//是否有收藏
	public boolean isCollect(DataSource datasource, String patientInstructionID, String patientID) {
		Connection con = null;
		
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select patientID, patientInstructionID, collectDate from collection where patientInstructionID='"+patientInstructionID+"' and patientID='"+patientID+"' ");
			while (rs.next()) {
				return true;
			}
			rs.close();//關閉rs
			
			st.close();//關閉st
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer isCollect Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return false;
	}
	
	//PatientInstruction.html//新增收藏
	synchronized public boolean newCollect(DataSource datasource, String patientInstructionID, String patientID) {
		Connection con = null;
		Gson gson = new Gson();
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			
		    int update = st.executeUpdate("insert into collection(patientID, patientInstructionID, collectDate) values('"+patientID+"','"+patientInstructionID+"', '"+getNowDate().substring(0, 16)+"')");
		    st.close();//關閉st

            return update > 0;
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer newCollect Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return true;
	}
	
	//PatientInstruction.html//刪除收藏
	public boolean deleteCollect(DataSource datasource, String patientInstructionID, String patientID) {
		Connection con = null;
		Gson gson = new Gson();
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			
		    int delete = st.executeUpdate("delete from collection where patientInstructionID='"+patientInstructionID+"' and patientID='"+patientID+"' ");
		    st.close();//關閉st

            return delete > 0;
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer deleteCollect Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return true;
	}

	//PatientInstruction.html//取得圖片位置
	static public FileInputStream getSrc(String srcPath){
		File downloadFile = new File("C:/apache-tomcat-8.0.44/webapps/BBDPDoctor/"+srcPath);

		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(downloadFile);		
		}
		catch (FileNotFoundException e) {
			System.out.println("發生FileNotFoundException : " + e);
		}
		return inputStream;
	}
	
	/*******************************************************************************************/

	//NewComment.html//新增留言
	synchronized public boolean newComment(DataSource datasource, String patientInstructionID, String patientID, String comment_1, String hideImg) {
		Connection con = null;
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			String time_1 = getNowDate();
		    int insert = st.executeUpdate("insert into comment(commentID,patientInstructionID,patientID,time_1,comment_1,hideImg) select ifNULL(max(commentID+0),0)+1,'" + patientInstructionID + "','" + patientID + "','"+ time_1 + "','" + comment_1 + "', '" + hideImg + "' from comment");
			st.close();//關閉st

            return insert > 0;
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer newComment Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return true;
	}

	//NewComment.html//取得病患名稱
	public String getPatientName(DataSource datasource, String patientID) {
		String getPatientName = new String();
		Connection con = null;
		
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select name from patient where patientID='"+patientID+"' ");
			while (rs.next()) {
				getPatientName = rs.getString("name");
			}
			rs.close();//關閉rs
			
			st.close();//關閉st
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer getPatientName Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return getPatientName;
	}
	
	//取得現在的時間
	public static String getNowDate(){
		//取得現在時間
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = simpleDateFormat.format(timestamp);
		return currentTime;
	}

	/*******************************************************************************************/
	
	//PatientInstructionFavoriteList.html//取得收藏列表
	public HashMap getFavoriteList(DataSource datasource, String patientID, String sort) {
		Connection con = null;
		Gson gson = new Gson();
		
		HashMap getFavoriteList = new HashMap();
		
		ArrayList patientInstructionIDList = new ArrayList();
		ArrayList symptomList = new ArrayList();
		ArrayList dateList = new ArrayList();
		ArrayList titleList = new ArrayList();
		ArrayList hospitalList = new ArrayList();
		ArrayList departmentList = new ArrayList();
		ArrayList nameList = new ArrayList();

		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
					
			ResultSet rs = st.executeQuery("select patientInstructionID, symptom, date, title, hospital, department, name from patientinstruction natural join doctor natural join collection where patientID='"+patientID+"' order by "+sort+" desc");	//執行
			while (rs.next()) {
				patientInstructionIDList.add(rs.getString("patientInstructionID"));
				symptomList.add(rs.getString("symptom"));
				dateList.add(rs.getString("date").substring(0, 16));
				titleList.add(rs.getString("title"));
				hospitalList.add(rs.getString("hospital"));
				departmentList.add(rs.getString("department"));
				nameList.add(rs.getString("name"));
			}
			rs.close();//關閉rs
			st.close();//關閉st

			getFavoriteList.put("patientInstructionIDList", patientInstructionIDList);
			getFavoriteList.put("symptomList", symptomList);
			getFavoriteList.put("dateList", dateList);
			getFavoriteList.put("titleList", titleList);
			getFavoriteList.put("hospitalList", hospitalList);
			getFavoriteList.put("departmentList", departmentList);
			getFavoriteList.put("nameList", nameList);
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer getFavoriteList Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return getFavoriteList;
	}
	
}
