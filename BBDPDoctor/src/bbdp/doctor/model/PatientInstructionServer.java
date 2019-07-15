package bbdp.doctor.model;

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
	//PatientInstruction.html//取得type
	public ArrayList getType(DataSource datasource, String doctorID){
		Connection con = null;
		ArrayList typeList = new ArrayList();
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select distinct type from patientinstruction where doctorID='"+doctorID +"' ");
		
			while (rs.next()) {
				typeList.add(rs.getString("type"));
			}
			rs.close();//關閉rs
			st.close();//關閉st
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer getType Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return typeList;
	}

	//PatientInstruction.html//取得title
	public HashMap getTitleDate(DataSource datasource, String sqlString) {
		HashMap getTitleDate = new HashMap();
		Connection con = null;
		ArrayList IDList = new ArrayList();
		ArrayList titleList = new ArrayList();
		ArrayList dateList = new ArrayList();

		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sqlString);
			
			while (rs.next()) {
				IDList.add(rs.getString("patientInstructionID"));
				titleList.add(rs.getString("title"));
				dateList.add(rs.getString("date").substring(0, 16));
			}
			rs.close();//關閉rs
			
			st.close();//關閉st
			
			getTitleDate.put("IDList", IDList);
			getTitleDate.put("titleList", titleList);			
			getTitleDate.put("dateList", dateList);	
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer getTitleDate Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return getTitleDate;
	}

	/*******************************************************************************************/

	//NewPatientInstruction.html//取得symptom
	public ArrayList getSymptom(DataSource datasource) {
		Connection con = null;
		ArrayList symptomList = new ArrayList();
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select distinct symptom from patientinstruction ");
		
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
	
	//NewPatientInstruction.html//取得getMaxInstructionID
	public String getMaxInstructionID(DataSource datasource) {
		Connection con = null;
		String patientInstructionID = new String();
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select max(patientInstructionID+1) as patientInstructionID from patientinstruction ");
		
			while (rs.next()) {
				patientInstructionID = rs.getString("patientInstructionID");
			}
			rs.close();//關閉rs
			st.close();//關閉st
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer getMaxInstructionID Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return patientInstructionID;
	}
		
	//NewPatientInstruction.html//新增
	synchronized public boolean newInstruction(DataSource datasource, String doctorID, String title, String type, String symptom,
			String html) {
		Connection con = null;
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			
			String date = getNowDate();
		    int insert = st.executeUpdate("insert into patientinstruction(patientInstructionID,doctorID,symptom,type,date,title,content,editDate) select ifNULL(max(patientInstructionID+0),0)+1,'" + doctorID + "','" + symptom + "','"+ type + "','" + date + "','" + title + "','"+ html + "','" + date + "' FROM patientinstruction");
			st.close();//關閉st
			
			if(insert > 0){
				pushInstruction(datasource, doctorID, symptom);	//推播給訂閱此醫生的病患
				return true;
			}
			else{
				return false;
			}
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer newInstruction Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return true;		
	}
	
	//取得現在的時間
	public static String getNowDate(){
		//取得現在時間
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = simpleDateFormat.format(timestamp);
		return currentTime;
	}

	//訂閱:症狀跟醫生ID
	class Subscription {
		List<String> symptom;
		List<Integer> doctorID;
		Subscription(List<String> symptom, List<Integer> doctorID){
			this.symptom = symptom;
			this.doctorID = doctorID;
		}
		
		//移除醫生ID//這邊用不到
		void removeDoctorID(int doctorID){
			for(int i = 0; i < this.doctorID.size(); i++){
				if(this.doctorID.get(i) == doctorID){
					this.doctorID.remove(i);
				}
			}
		}
	}
	
	//推播給訂閱此醫生或症狀的病患
	public void pushInstruction(DataSource datasource, String doctorID, String symptom){
		Connection con = null;
		Gson gson = new Gson();
		ArrayList patientIDList = new ArrayList();
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select patientID, subscription from subscription ");
			while (rs.next()) {
				boolean flag = false;	//判斷此病患是否已經有訂閱
				Subscription temp = gson.fromJson(rs.getString("subscription"), Subscription.class);	//解析
				for(int i = 0; i < temp.doctorID.size(); i++){
					if(temp.doctorID.get(i).toString().equals(doctorID)){	//因為是數字，所以轉成String來比較
						patientIDList.add(rs.getString("patientID"));
						flag = true;
						break;
					}
				}
				if(!flag){	//如果訂閱醫生並未納入才來判斷
					for(int i = 0; i < temp.symptom.size(); i++){
						if(temp.symptom.get(i).equals(symptom)){
							patientIDList.add(rs.getString("patientID"));
							break;
						}
					}
				}
			}
			rs.close();
			st.close();//關閉st	
			
			//取得max patientInstructionID並推播
			st = con.createStatement();
			rs = st.executeQuery("select max(patientInstructionID+0) from patientinstruction");
			while (rs.next()) {
				//推播
				for(int i = 0 ; i < patientIDList.size(); i++){
					bbdp.push.fcm.PushToFCM.sendNotification("BBDP", "新的衛教資訊文章已發布", patientIDList.get(i).toString(), "PatientInstruction.html?patientInstructionID="+rs.getString("max(patientInstructionID+0)")+"&from=2");
				}
			}
			rs.close();
			st.close();//關閉st				
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer pushInstruction Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
	}
	
	/*******************************************************************************************/
	
	//EditPatientInstruction.html//取得衛教資訊
	public HashMap getInstruction(DataSource datasource, String doctorID, String patientInstructionID) {
		HashMap getInstruction = new HashMap();
		Connection con = null;
		
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select symptom, type, date, title, content, editDate from patientinstruction where doctorID='"+doctorID+"' and patientInstructionID='"+patientInstructionID+"' ");
			while (rs.next()) {
				getInstruction.put("symptom", rs.getString("symptom"));
				getInstruction.put("type", rs.getString("type"));
				getInstruction.put("date", rs.getString("date").substring(0, 16));
				getInstruction.put("title", rs.getString("title"));
				getInstruction.put("content", rs.getString("content"));
				getInstruction.put("editDate", rs.getString("editDate").substring(0, 16));
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
	
	//EditPatientInstruction.html//刪除衛教資訊和留言和收藏文章的人
	public boolean deleteInstruction(DataSource datasource, String doctorID, String patientInstructionID) {
		Connection con = null;
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			//刪除收藏
			int deleteCollect = st.executeUpdate("delete from collection where patientInstructionID='"+patientInstructionID+"' ");
		    st.close();//關閉st
			
		    //刪除留言
			st = con.createStatement();
		    int deleteComment = st.executeUpdate("delete from comment where patientInstructionID='"+patientInstructionID+"'  ");
			st.close();//關閉st
			
			//刪除衛教資訊
			st = con.createStatement();
		    int delete = st.executeUpdate("delete from patientinstruction where patientInstructionID='"+patientInstructionID+"' and doctorID='"+doctorID+"' ");
		    st.close();//關閉st
			
			if(delete > 0){
				return true;
			}
			else{
				return false;
			}
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer deleteInstruction Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return true;
	}
	
	//EditPatientInstruction.html//檢查InstructionID
	public boolean checkInstructionID(DataSource datasource, String doctorID, String patientInstructionID) {
		Connection con = null;
		
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select * from patientinstruction where doctorID='"+doctorID+"' and patientInstructionID='"+patientInstructionID+"' ");
			while (rs.next()) {
				return false;
			}
			rs.close();//關閉rs
			st.close();//關閉st			
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer checkInstructionID Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return true;
	}
	
	/*******************************************************************************************/

	//EditPatientInstruction - 編輯.html//更新
	public boolean updateInstruction(DataSource datasource, String doctorID, String patientInstructionID,
			String title, String type, String symptom, String html) {
		Connection con = null;
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			
			String date = getNowDate();
		    int update = st.executeUpdate("update patientinstruction set symptom='"+symptom+"',type='"+type+"',editDate='"+date+"',title='"+title+"',content='"+html+"' where patientInstructionID='"+patientInstructionID+"' ");
			st.close();//關閉st
			
			if(update > 0){
				return true;
			}
			else{
				return false;
			}
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer updateInstruction Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return true;	
	}

	/*******************************************************************************************/
	
	//EditPatientInstruction - 留言.html//取得留言
	public HashMap getComment(DataSource datasource, String patientInstructionID, String doctorID) {
		HashMap getComment = new HashMap();
		Connection con = null;
		ArrayList commentIDList = new ArrayList();
		ArrayList patientIDList = new ArrayList();
		ArrayList time_1List = new ArrayList();
		ArrayList comment_1List = new ArrayList();
		ArrayList time_2List = new ArrayList();
		ArrayList comment_2List = new ArrayList();
		
		ArrayList doctorIDList = new ArrayList();
		ArrayList nameList = new ArrayList();
		ArrayList hideImgList = new ArrayList();
		
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select commentID, patientID, time_1, comment_1, time_2, comment_2, hideImg, doctorID, name, sex from comment natural join patientinstruction natural join patient where patientInstructionID='"+patientInstructionID+"' and patient.patientID=comment.patientID and doctorID='"+doctorID+"' order by (commentID+0) desc");
			while (rs.next()) {
				commentIDList.add(rs.getString("commentID"));
				patientIDList.add(rs.getString("patientID"));
				time_1List.add(rs.getString("time_1").substring(0, 16));
				comment_1List.add(rs.getString("comment_1"));
				if(rs.getString("time_2") != null)
					time_2List.add(rs.getString("time_2").substring(0, 16));
				else
					time_2List.add(rs.getString("time_2"));
				comment_2List.add(rs.getString("comment_2"));
				doctorIDList.add(rs.getString("doctorID"));
				
				//姓名判斷
				if(rs.getString("hideImg").equals("1")){	//不顯示姓名
					if(rs.getString("sex").equals("女"))
						nameList.add(rs.getString("name").substring(0, 1) + "小姐");
					else if(rs.getString("sex").equals("男"))
						nameList.add(rs.getString("name").substring(0, 1) + "先生");
				}
				else if(rs.getString("hideImg").equals("0")){	//顯示姓名
					nameList.add(rs.getString("name"));
				}
				hideImgList.add(rs.getString("hideImg"));
			}
			rs.close();//關閉rs
			
			st.close();//關閉st
			getComment.put("commentIDList", commentIDList);
			getComment.put("patientIDList", patientIDList);
			getComment.put("time_1List", time_1List);
			getComment.put("comment_1List", comment_1List);
			getComment.put("time_2List", time_2List);
			getComment.put("comment_2List", comment_2List);
			getComment.put("doctorIDList", doctorIDList);
			getComment.put("nameList", nameList);
			getComment.put("hideImgList", hideImgList);
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer getComment Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return getComment;
	}

	//EditPatientInstruction - 留言.html//醫生回復
	public boolean replyComment(DataSource datasource, String commentID, String comment_2) {
		Connection con = null;
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			
			String date = getNowDate();
		    int update = st.executeUpdate("update comment set time_2='"+date+"',comment_2='"+comment_2+"' where commentID='"+commentID+"' ");
			st.close();//關閉st
			
			if(update > 0){
				pushComment(datasource, commentID);	//醫生回復推播病患
				return true;
			}
			else{
				return false;
			}
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer replyComment Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return true;	
	}

	//醫生回復推播病患
	public void pushComment(DataSource datasource, String commentID){
		Connection con = null;
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select patientID, patientInstructionID from comment where commentID='"+commentID+"' ");
			while (rs.next()) {
				//推播
				bbdp.push.fcm.PushToFCM.sendNotification("BBDP", "醫師已回覆您的留言", rs.getString("patientID"), "PatientInstruction.html?patientInstructionID="+rs.getString("patientInstructionID")+"&from=1");
			}
			rs.close();
			st.close();//關閉st	
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer pushComment Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
	}
	
	
	//EditPatientInstruction - 留言.html////刪除醫生回復
	public boolean deleteReplyComment(DataSource datasource, String commentID) {
		Connection con = null;
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			
		    int update = st.executeUpdate("update comment set time_2=null,comment_2=null where commentID='"+commentID+"' ");
			st.close();//關閉st
			
			if(update > 0){
				return true;
			}
			else{
				return false;
			}
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer deleteReplyComment Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return true;	
	}
	
	//EditPatientInstruction - 留言.html//刪除病患留言
	public boolean deleteComment(DataSource datasource, String commentID) {
		Connection con = null;
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
		    int delete = st.executeUpdate("delete from comment where commentID='"+commentID+"' ");
			st.close();//關閉st
			
			if(delete > 0){
				return true;
			}
			else{
				return false;
			}
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer deleteComment Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return true;	
	}


	/*******************************************************************************************/

}
