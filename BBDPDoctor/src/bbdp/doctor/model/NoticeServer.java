package bbdp.doctor.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NoticeServer {
	//新增醫生注意事項
	synchronized public static String addNoticeItem(Connection conn, String doctorID, String type ,String content){
		PreparedStatement statement = null;
		String maxDoctorNoticeID = "0";
		try {
			//判斷是否重複新增
			statement = conn.prepareStatement("SELECT * FROM doctornotice WHERE doctorID = ? AND type = ? AND content = ?");
			statement.setString(1, doctorID);
			statement.setString(2, type);
			statement.setString(3, content);
			
			if (statement.executeQuery().next()) {	
				//System.out.println("新增的注意事項已存在");
				return "新增的注意事項已存在";
			}
			else{
				statement = conn.prepareStatement("SELECT MAX(CAST(doctorNoticeID AS INT)) FROM doctornotice");
			
				ResultSet resultSet = statement.executeQuery();
				while (resultSet.next()) {
					maxDoctorNoticeID = resultSet.getString(1);
				}
				
				if(maxDoctorNoticeID==null){
					maxDoctorNoticeID = "0";
				}
				
				String newDoctorNoticeID = Integer.toString(Integer.parseInt(maxDoctorNoticeID) + 1);
				
				statement = conn.prepareStatement("INSERT INTO doctornotice VALUES (?, ?, ?, ?)");
				statement.setString(1, newDoctorNoticeID);			//doctorNoticeID
				statement.setString(2, doctorID);					//doctorID
				statement.setString(3, type);						//type
				statement.setString(4, content);					//content
				statement.execute();
				return "新增成功";
			}
		}
		catch (SQLException e1) {
			System.out.println("新增醫生注意事項時發生SQLException:" + e1);
			return "資料庫發生錯誤";
		}
		finally {
            if (statement != null) try {statement.close();}catch (SQLException ignore) {}
            if (conn!=null) try {conn.close();}catch (Exception ignore) {}
        }
	}
	
	//修改醫生注意事項
	public static String editNoticeItem(Connection conn, String doctorNoticeID, String doctorID, String type ,String content){
		PreparedStatement statement = null;
		try {
			//判斷是否重複新增
			statement = conn.prepareStatement("SELECT * FROM doctornotice WHERE doctorID = ? AND type = ? AND content = ?");
			statement.setString(1, doctorID);
			statement.setString(2, type);
			statement.setString(3, content);
			
			if (statement.executeQuery().next()) {	
				//System.out.println("輸入的注意事項已存在");
				return "輸入的注意事項已存在";
			}
			else{
				statement = conn.prepareStatement("UPDATE doctornotice SET type = ?, content = ? WHERE doctorNoticeID = ?");
				statement.setString(1, type);
				statement.setString(2, content);
				statement.setString(3, doctorNoticeID);
				statement.executeUpdate();
				//System.out.println("修改成功");
				return "修改成功";
			}
		}
		catch (SQLException e1) {
			System.out.println("修改醫生注意事項時發生SQLException："+e1);
			return "資料庫發生錯誤";
		}
		finally {
            if (statement != null) try {statement.close();}catch (SQLException ignore) {}
            if (conn!=null) try {conn.close();}catch (Exception ignore) {}
        }
	} 
	
	//刪除醫生注意事項
	public static void deleteNoticeItem(Connection conn, String doctorNoticeID){
		PreparedStatement statement = null;
		try {
			statement = conn.prepareStatement("DELETE FROM doctornotice WHERE doctorNoticeID = ?");
			statement.setString(1, doctorNoticeID);
			statement.executeUpdate();
		}
		catch (SQLException e) {
			System.out.println("刪除醫生注意事項時發生SQLException");
		}
		finally {
            if (statement != null) try { statement.close();}catch (SQLException ignore) {}
            if (conn!=null) try {conn.close();}catch (Exception ignore) {}
        }
	}
	
	//取得醫生所有已新增的注意事項
	public static String getDoctorNitice(Connection conn, String doctorID){
		PreparedStatement statement = null;
		String jsonString = "";
		JSONArray doctorNoticeArray = new JSONArray();
		try {
			statement = conn.prepareStatement("SELECT * FROM doctornotice WHERE doctorID = ? ORDER BY CAST(doctorNoticeID AS UNSIGNED) DESC");
			statement.setString(1, doctorID); 
			ResultSet resultSet = statement.executeQuery();
			//type
			while (resultSet.next()) {
				JSONObject doctorNoticObject = new JSONObject();
				doctorNoticObject.put("doctorNoticeID", resultSet.getString("doctorNoticeID"));
				doctorNoticObject.put("doctorID", resultSet.getString("doctorID"));
				doctorNoticObject.put("type", resultSet.getString("type"));
				doctorNoticObject.put("content", resultSet.getString("content"));
				doctorNoticeArray.put(doctorNoticObject);
			}
			if (resultSet != null) try { resultSet.close(); } catch (SQLException ignore) {}
	            
			jsonString = doctorNoticeArray.toString();
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
	
	//搜尋類型
	public static String searchNoticeType(Connection conn, String doctorID){
		PreparedStatement statement = null;
		String jsonString = "";
		JSONArray noticeTypeArray = new JSONArray();
		try {
			statement = conn.prepareStatement("SELECT DISTINCT type FROM doctornotice WHERE doctorID = ?");
			statement.setString(1, doctorID); 
			ResultSet resultSet = statement.executeQuery();
			//type
			while (resultSet.next()) {
				JSONObject noticeTypeObject = new JSONObject();
				noticeTypeObject.put("type", resultSet.getString("type"));
				noticeTypeArray.put(noticeTypeObject);
			}
			if (resultSet != null) try { resultSet.close(); } catch (SQLException ignore) {}
	            
			jsonString = noticeTypeArray.toString();
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
	
	//取得單筆的醫生注意事項
	public static String getNoticeItem(Connection conn, String doctorID, String doctorNoticeID){
		PreparedStatement statement = null;
		String jsonString = "";
		JSONArray noticeTypeArray = new JSONArray();
		try {
			statement = conn.prepareStatement("SELECT * FROM doctornotice WHERE doctorID = ? AND doctorNoticeID = ?");
			statement.setString(1, doctorID); 
			statement.setString(2, doctorNoticeID); 
			ResultSet resultSet = statement.executeQuery();
			//type
			while (resultSet.next()) {
				JSONObject noticeTypeObject = new JSONObject();
				noticeTypeObject.put("doctorNoticeID", resultSet.getString("doctorNoticeID"));
				noticeTypeObject.put("doctorID", resultSet.getString("doctorID"));
				noticeTypeObject.put("type", resultSet.getString("type"));
				noticeTypeObject.put("content", resultSet.getString("content"));
				noticeTypeArray.put(noticeTypeObject);
			}
			if (resultSet != null) try { resultSet.close(); } catch (SQLException ignore) {}
		            
			jsonString = noticeTypeArray.toString();
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
	
	//新增注意事項給病患
	synchronized public static String addPatientNotice(Connection conn, String time, String patientID, String doctorID, String content){
		PreparedStatement statement = null;
		String maxNoticeID = "0";
		try {
			//判斷是否重複新增
			statement = conn.prepareStatement("SELECT * FROM notice WHERE patientID = ? AND doctorID = ? AND content = ?");
			statement.setString(1, patientID);
			statement.setString(2, doctorID);
			statement.setString(3, content);
			
			if (statement.executeQuery().next()) {	//修改時間
				//System.out.println("新增給病患的注意事項已存在,更新時間");
				statement = conn.prepareStatement("UPDATE notice SET time = ? WHERE patientID = ? AND doctorID = ? AND content = ?");
				statement.setString(1, time);
				statement.setString(2, patientID);
				statement.setString(3, doctorID);
				statement.setString(4, content);
				statement.executeUpdate();
				return "成功送出";
			}
			else{
				statement = conn.prepareStatement("SELECT MAX(CAST(noticeID AS UNSIGNED)) FROM notice");
			
				ResultSet resultSet = statement.executeQuery();
				while (resultSet.next()) {
					maxNoticeID = resultSet.getString(1);
				}
				
				if(maxNoticeID==null){
					maxNoticeID = "0";
				}
				
				//System.out.println("maxNoticeID : "+maxNoticeID);
				String newNoticeID = Integer.toString(Integer.parseInt(maxNoticeID) + 1);
				
				//System.out.println("newDoctorNoticeID : "+newNoticeID);
				statement = conn.prepareStatement("INSERT INTO notice VALUES (?, ?, ?, ?, ?, ?)");
				statement.setString(1, newNoticeID);			//noticeID
				statement.setString(2, time);					//time
				statement.setString(3, patientID);				//patientID
				statement.setString(4, null);						//senderID
				statement.setString(5, doctorID);				//doctorID
				statement.setString(6, content);				//content
				statement.execute();
				if (resultSet != null) try { resultSet.close(); } catch (SQLException ignore) {}
				
				//推播				
				String doctorName = getDoctorName(conn, doctorID);
				bbdp.push.fcm.PushToFCM.sendNotification("BBDP", doctorName + " 醫師發送了注意事項", patientID, "Notice.html");
				return "成功送出";
			}
		}
		catch (SQLException e1) {
			System.out.println("新增注意事項給病患時發生SQLException:" + e1);
			return "資料庫發生錯誤";
		}
		finally {
            if (statement != null) try {statement.close();}catch (SQLException ignore) {}
            if (conn!=null) try {conn.close();}catch (Exception ignore) {}
        }
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
