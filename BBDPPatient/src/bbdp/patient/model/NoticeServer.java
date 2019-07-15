package bbdp.patient.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NoticeServer {
	//病患新增注意事項
	synchronized public static String addPatientNotice(Connection conn, String time, String patientID, String content){
		PreparedStatement statement = null;
			String maxNoticeID = "0";
			try {
				//找最大的id						
				statement = conn.prepareStatement("SELECT MAX(CAST(noticeID AS UNSIGNED)) FROM notice");
				
				ResultSet resultSet = statement.executeQuery();
				while (resultSet.next()) {
					maxNoticeID = resultSet.getString(1);
				}
					
				if(maxNoticeID==null){
					maxNoticeID = "0";
				}
					
				String newNoticeID = Integer.toString(Integer.parseInt(maxNoticeID) + 1);
					
				statement = conn.prepareStatement("INSERT INTO notice VALUES (?, ?, ?, ?, ?, ?)");
				statement.setString(1, newNoticeID);			//noticeID
				statement.setString(2, time);					//time
				statement.setString(3, patientID);				//patientID
				statement.setString(4, null);					//senderID
				statement.setString(5, null);					//doctorID
				statement.setString(6, content);				//content
				statement.execute();
				if (resultSet != null) try { resultSet.close(); } catch (SQLException ignore) {}
					
				return "新增成功";
			}
			catch (SQLException e1) {
				System.out.println("病患新增注意事項時發生SQLException:" + e1);
				return "資料庫發生錯誤";
			}
			finally {
	            if (statement != null) try {statement.close();}catch (SQLException ignore) {}
	            if (conn!=null) try {conn.close();}catch (Exception ignore) {}
	        }
	}
	
	//家屬新增注意事項
	synchronized public static String addFamilyNotice(Connection conn, String time, String patientID, String senderID, String content){
		PreparedStatement statement = null;
		String maxNoticeID = "0";
		try {
			//判斷是否重複新增
			statement = conn.prepareStatement("SELECT * FROM notice WHERE patientID = ? AND senderID = ? AND content = ?");
			statement.setString(1, patientID);
			statement.setString(2, senderID);
			statement.setString(3, content);
				
			if (statement.executeQuery().next()) {	//修改時間
				//System.out.println("新增給病患的注意事項已存在,更新時間");
				statement = conn.prepareStatement("UPDATE notice SET time = ? WHERE patientID = ? AND senderID = ? AND content = ?");
				statement.setString(1, time);
				statement.setString(2, patientID);
				statement.setString(3, senderID);
				statement.setString(4, content);
				statement.executeUpdate();
				return "新增成功";
			}
			else{
				//找最大的id						
				statement = conn.prepareStatement("SELECT MAX(CAST(noticeID AS UNSIGNED)) FROM notice");
						
				ResultSet resultSet = statement.executeQuery();
				while (resultSet.next()) {
					maxNoticeID = resultSet.getString(1);
				}
							
				if(maxNoticeID==null){
					maxNoticeID = "0";
				}
							
				String newNoticeID = Integer.toString(Integer.parseInt(maxNoticeID) + 1);
							
				statement = conn.prepareStatement("INSERT INTO notice VALUES (?, ?, ?, ?, ?, ?)");
				statement.setString(1, newNoticeID);			//noticeID
				statement.setString(2, time);					//time
				statement.setString(3, patientID);				//patientID
				statement.setString(4, senderID);				//senderID
				statement.setString(5, null);					//doctorID
				statement.setString(6, content);				//content
				statement.execute();
				if (resultSet != null) try { resultSet.close(); } catch (SQLException ignore) {}
				
				String familyName = FamilyServer.getFamilyName(conn, patientID, senderID);
				bbdp.push.fcm.PushToFCM.sendNotification("BBDP", familyName + "發送了注意事項", patientID, "Notice.html");
				
				return "新增成功";
			}
		}
		catch (SQLException e1) {
			System.out.println("家屬新增注意事項發生SQLException:" + e1);
			return "資料庫發生錯誤";
		}
		finally {
			if (statement != null) try {statement.close();}catch (SQLException ignore) {}
			if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
	}
	
	//刪除病患注意事項
	public static String deleteNoticeItem(Connection conn, String noticeID){
		PreparedStatement statement = null;
		try {
			statement = conn.prepareStatement("DELETE FROM notice WHERE noticeID = ?");
			statement.setString(1, noticeID);
			statement.executeUpdate();
			return "刪除成功";
		}
		catch (SQLException e) {
			System.out.println("刪除病患注意事項時發生SQLException : "+e);
			return "資料庫發生錯誤";
		}
		finally {
			if (statement != null) try { statement.close();}catch (SQLException ignore) {}
			if (conn!=null) try {conn.close();}catch (Exception ignore) {}
	    }
	}
	
	//取得所有病患注意事項
	public static String getPatientNitice(Connection conn, String patientID){
		PreparedStatement statement = null;
		String jsonString = "";
		JSONArray patientNoticeArray = new JSONArray();
		try {
			statement = conn.prepareStatement("SELECT * FROM notice WHERE patientID = ? ORDER BY time DESC");
			statement.setString(1, patientID); 
			ResultSet resultSet = statement.executeQuery();
			
			while (resultSet.next()) {
				JSONObject patientNoticeObject = new JSONObject();
				patientNoticeObject.put("noticeID", resultSet.getString("noticeID"));
				patientNoticeObject.put("time", resultSet.getString("time"));
				patientNoticeObject.put("senderID", resultSet.getString("senderID"));
				patientNoticeObject.put("doctorID", resultSet.getString("doctorID"));
				patientNoticeObject.put("content", resultSet.getString("content"));
				patientNoticeArray.put(patientNoticeObject);
			}
			if (resultSet != null) try { resultSet.close(); } catch (SQLException ignore) {}
	            
			jsonString = patientNoticeArray.toString();
			return jsonString;
		} 
		catch (SQLException ex) {
			System.out.println("取得所有病患注意事項時發生SQLException："+ex);
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
	
	//取得醫生姓名
	public static String getDoctorName(Connection conn, String doctorID){
		String doctorName = "";
		PreparedStatement statement = null;
		try {
			statement = conn.prepareStatement("SELECT name FROM doctor WHERE doctorID = ?");
			statement.setString(1, doctorID);
				
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				doctorName = resultSet.getString("name");
			}
			if (resultSet != null) try { resultSet.close(); } catch (SQLException ignore) {}
				
			return doctorName;
		}
		catch (SQLException ex) {
			System.out.println("發生SQLException : " + ex);
			return "資料庫發生錯誤";
		}
		finally {
			if (statement != null) try { statement.close(); }catch (SQLException ignore) {}
			if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
	}
	
	
}
