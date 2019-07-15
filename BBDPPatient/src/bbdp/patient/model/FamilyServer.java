package bbdp.patient.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class FamilyServer {
	//取得自己送出的邀請名單
	public static String getSentInvitationList(Connection conn, String userID){
		String jsonString = "";
		JSONArray invitationList = new JSONArray();
		PreparedStatement statement = null;
		
		try {
			statement = conn.prepareStatement("SELECT familyID ,name FROM family INNER JOIN patient ON family.familyID = patient.patientID WHERE userID = ? AND acceptInvitation = 0");           
			statement.setString(1, userID);            
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				JSONObject invitationItem = new JSONObject();
				invitationItem.put("familyID", resultSet.getString("familyID"));            	//未被接受的id
				invitationItem.put("name", resultSet.getString("name"));
				invitationList.put(invitationItem);
			}
			if (resultSet != null) try { resultSet.close(); } catch (SQLException ignore) {}
	  
			jsonString = invitationList.toString();
	            //System.out.println(jsonString);
			return jsonString;
		}
		catch (SQLException ex) {
			System.out.println("發生SQLException");
		}
		catch (JSONException e) {
			System.out.println("JSONException: " + e);
		}
		finally {
			if (statement != null) try { statement.close(); }catch (SQLException ignore) {}
			if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return jsonString;
	}
	
	//取得自己收到的邀請名單
	public static String getInvitationList(Connection conn, String userID){
		String jsonString = "";
		JSONArray invitationList = new JSONArray();
		PreparedStatement statement = null;
		
		try {
			statement = conn.prepareStatement("SELECT userID,name FROM family INNER JOIN patient ON family.userID = patient.patientID WHERE familyID = ? AND acceptInvitation = 0");           
			statement.setString(1, userID);            
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				JSONObject invitationItem = new JSONObject();
				invitationItem.put("userID", resultSet.getString("userID"));            	//未被接受的id
				invitationItem.put("name", resultSet.getString("name"));
				invitationList.put(invitationItem);
			}
			if (resultSet != null) try { resultSet.close(); } catch (SQLException ignore) {}
	  
			jsonString = invitationList.toString();
	            //System.out.println(jsonString);
			return jsonString;
		}
		catch (SQLException ex) {
			System.out.println("發生SQLException");
		}
		catch (JSONException e) {
			System.out.println("JSONException: " + e);
		}
		finally {
			if (statement != null) try { statement.close(); }catch (SQLException ignore) {}
			if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return jsonString;
	}
	
	//取得已成為家屬的名單
	public static String getFamilyList(Connection conn, String userID){
		String jsonString = "";
		JSONArray familyList = new JSONArray();
		PreparedStatement statement = null;
		
		try {
			statement = conn.prepareStatement("SELECT familyID,kinship,healthtrackingLimit,fileLimit,medicalrecordLimlt,name "
											+ "FROM family INNER JOIN patient ON family.familyID = patient.patientID "
											+ "WHERE userID = ? AND acceptInvitation = 1");           
			statement.setString(1, userID);           
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				JSONObject familyItem = new JSONObject();            	
				familyItem.put("familyID", resultSet.getString("familyID"));
				familyItem.put("kinship", resultSet.getString("kinship"));
				familyItem.put("healthtrackingLimit", resultSet.getInt("healthtrackingLimit"));
				familyItem.put("fileLimit", resultSet.getInt("fileLimit"));
				familyItem.put("medicalrecordLimlt", resultSet.getInt("medicalrecordLimlt"));
				familyItem.put("familyName", resultSet.getString("name"));
				familyList.put(familyItem);
			}
			if (resultSet != null) try { resultSet.close(); } catch (SQLException ignore) {}
	  
			jsonString = familyList.toString();
	            //System.out.println(jsonString);
	     	return jsonString;
		}
		catch (SQLException ex) {
			System.out.println("發生SQLException");
		}
		catch (JSONException e) {
			System.out.println("JSONException: " + e);
		}
		finally {
			if (statement != null) try { statement.close(); }catch (SQLException ignore) {}
			if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return jsonString;
	}
	
	//拒絕邀請
	public static void refuseInvitation(Connection conn, String userID, String familyID){
		PreparedStatement statement = null;
		try {
			//刪除家屬
			statement = conn.prepareStatement("DELETE FROM family WHERE userID = ? AND familyID = ?");
			statement.setString(1, userID);
			statement.setString(2, familyID);
			statement.executeUpdate();
			
			statement = conn.prepareStatement("DELETE FROM family WHERE userID = ? AND familyID = ?");
			statement.setString(1, familyID);
			statement.setString(2, userID);
			statement.executeUpdate();
			
			//刪除注意事項
			statement = conn.prepareStatement("DELETE FROM notice WHERE patientID = ? AND senderID = ?");
			statement.setString(1, userID);
			statement.setString(2, familyID);
			statement.executeUpdate();
			
			statement = conn.prepareStatement("DELETE FROM notice WHERE patientID = ? AND senderID = ?");
			statement.setString(1, familyID);
			statement.setString(2, userID);
			statement.executeUpdate();
		}
		catch (SQLException e) {
			System.out.println("拒絕邀請時發生SQLException");
		}
		finally {
            if (statement != null) try { statement.close();}catch (SQLException ignore) {}
            if (conn!=null) try {conn.close();}catch (Exception ignore) {}
        }
	}
	
	//接受邀請
	synchronized public static void acceptInvitation(Connection conn, String recipient, String accepted){//接受者 被接受者
		PreparedStatement statement = null;
		try {
			//修改 userID=被接受者 的欄位
			statement = conn.prepareStatement("UPDATE family SET acceptInvitation = ?, healthtrackingLimit = ?, fileLimit = ?, medicalrecordLimlt = ? WHERE userID = ? AND familyID = ?");
			statement.setInt(1, 1);						//acceptInvitation
			statement.setInt(2, 1);						//healthtrackingLimit
			statement.setInt(3, 1);						//fileLimit
			statement.setInt(4, 1);						//medicalrecordLimlt
			statement.setString(5, accepted);			//userID(被接受者)
			statement.setString(6, recipient);			//familyID(接受者)
			statement.executeUpdate();
						
			//新增 userID=接受者 的欄位
			statement = conn.prepareStatement("INSERT INTO family VALUES (?, ?, ?, ?, ?, ?, ?)");
			statement.setString(1, recipient);			//userID(接受者)
			statement.setString(2, accepted);			//familyID(被接受者)
			statement.setString(3, "");				//kinship
			statement.setInt(4, 1);					//acceptInvitation
			statement.setInt(5, 1);					//healthtrackingLimit
			statement.setInt(6, 1);					//fileLimit
			statement.setInt(7, 1);					//medicalrecordLimlt
			statement.execute();
			
			String recipientName = getPatientNameByID(conn,recipient);
			bbdp.push.fcm.PushToFCM.sendNotification("BBDP", recipientName + "已接收您的家屬邀請", accepted, "FamilyList.html");
			
		} 
		catch (SQLException e) {
			System.out.println("接受邀請時發生SQLException");
		}
		finally {
            if (statement != null) try {statement.close();}catch (SQLException ignore) {}
            if (conn!=null) try {conn.close();}catch (Exception ignore) {}
        }
	}
	
	public static String checkInvitation(Connection conn, String familyID, String userID){
		if(isFamily(conn, userID, familyID)){
			if (conn!=null) try {conn.close();}catch (Exception ignore) {}
			return "該使用者已成為家屬";
		}
		else if(isSentInvitation(conn, userID, familyID)){
			if (conn!=null) try {conn.close();}catch (Exception ignore) {}
			return "已發送過邀請給該使用者";
		}
		else{
			return "發送家屬邀請失敗";
		}
	}
	//發送邀請
	synchronized public static String sendInvitation(Connection conn, String familyID, String userID){		
			PreparedStatement statement = null;
			try {			
				statement = conn.prepareStatement("INSERT INTO family VALUES (?, ?, ?, ?, ?, ?, ?)");
				statement.setString(1, userID);			//userID
				statement.setString(2, familyID);		//familyID
				statement.setString(3, "");				//kinship
				statement.setInt(4, 0);					//acceptInvitation
				statement.setInt(5, 0);					//healthtrackingLimit
				statement.setInt(6, 0);					//fileLimit
				statement.setInt(7, 0);					//medicalrecordLimlt
				statement.execute();
				
				String usertName = getPatientNameByID(conn,userID);
				bbdp.push.fcm.PushToFCM.sendNotification("BBDP", usertName + "向您發送邀請", familyID, "FamilyList.html");
			}
			catch (SQLException e1) {
				System.out.println("發送邀請時發生SQLException");
				return "資料庫發生錯誤";
			}
			finally {
	            if (statement != null) try {statement.close();}catch (SQLException ignore) {}
	            if (conn!=null) try {conn.close();}catch (Exception ignore) {}
	        }
			return "已發送家屬邀請";
	}
	
	//修改權限
	public static void editLimit(Connection conn, String userID,String familyID,
			int healthtrackingLimit, int fileLimit, int medicalrecordLimlt){
		PreparedStatement statement = null;
		try {
			statement = conn.prepareStatement("UPDATE family SET healthtrackingLimit = ?, fileLimit = ?, medicalrecordLimlt = ? WHERE userID = ? AND familyID = ?");
			statement.setInt(1, healthtrackingLimit);		//healthtrackingLimit
			statement.setInt(2, fileLimit);					//fileLimit
			statement.setInt(3, medicalrecordLimlt);		//medicalrecordLimlt
			statement.setString(4, userID);					//userID
			statement.setString(5, familyID);				//familyID
			statement.executeUpdate();
		} 
		catch (SQLException e) {
			System.out.println("修改權限時發生SQLException");
		}
		finally {
            if (statement != null) try {statement.close();}catch (SQLException ignore) {}
            if (conn!=null) try {conn.close();}catch (Exception ignore) {}
        }
	}
	
	//取得自己(familyID)對家屬(userID)的權限
	public static String getLimit(Connection conn, String userID , String familyID){
		String jsonString = "";
		JSONArray limitList = new JSONArray();
		PreparedStatement statement = null;
		
		try {
			statement = conn.prepareStatement("SELECT healthtrackingLimit, fileLimit, medicalrecordLimlt FROM family WHERE userID = ? AND familyID = ?");           
			statement.setString(1, userID);
			statement.setString(2, familyID);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				JSONObject limitItem = new JSONObject();
				limitItem.put("healthtrackingLimit", resultSet.getInt("healthtrackingLimit"));
				limitItem.put("fileLimit", resultSet.getInt("fileLimit"));
				limitItem.put("medicalrecordLimlt", resultSet.getInt("medicalrecordLimlt"));
				limitList.put(limitItem);
			}
			if (resultSet != null) try { resultSet.close(); } catch (SQLException ignore) {}
	  
			jsonString = limitList.toString();
	            //System.out.println(jsonString);
			return jsonString;
		}
		catch (SQLException ex) {
			System.out.println("發生SQLException");
		}
		catch (JSONException e) {
			System.out.println("JSONException: " + e);
		}
		finally {
			if (statement != null) try { statement.close(); }catch (SQLException ignore) {}
			if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return jsonString;
	}
	
	//修改稱謂
	public static void editKinship(Connection conn, String userID,String familyID, String kinship){
		PreparedStatement statement = null;
		try {
			statement = conn.prepareStatement("UPDATE family SET kinship = ? WHERE userID = ? AND familyID = ?");
			statement.setString(1, kinship);				//kinship
			statement.setString(2, userID);					//userID
			statement.setString(3, familyID);				//familyID
			statement.executeUpdate();
		} 
		catch (SQLException e) {
			System.out.println("修改稱謂時發生SQLException");
		}
		finally {
            if (statement != null) try {statement.close();}catch (SQLException ignore) {}
            if (conn!=null) try {conn.close();}catch (Exception ignore) {}
        }
	}
	
	//判斷是否為家屬
	public static boolean isFamily(Connection conn, String userID,String familyID){
		PreparedStatement statement = null;
		try {
			statement = conn.prepareStatement("SELECT * FROM family WHERE userID = ? AND familyID = ? AND acceptInvitation = 1");
			statement.setString(1, userID);	//userID(接受者)
			statement.setString(2, familyID);	//familyID(被接受者)
			return statement.executeQuery().next();
		} catch (SQLException e) {
			System.out.println("發生SQLException");
		}
		return false;
	}
	
	//判斷是否已發送邀請
	public static boolean isSentInvitation(Connection conn, String userID,String familyID){
		PreparedStatement statement = null;
		try {
			statement = conn.prepareStatement("SELECT * FROM family WHERE userID = ? AND familyID = ? AND acceptInvitation = 0");
			statement.setString(1, userID);	//userID(接受者)
			statement.setString(2, familyID);	//familyID(被接受者)
			return statement.executeQuery().next();
		} catch (SQLException e) {
			System.out.println("發生SQLException");
		}
		return false;
	}
	
	//搜尋patientID
	public static String searchPatientID(Connection conn, String inputAccount){
		String result = "";
		PreparedStatement statement = null;
		try {
			String sql = "SELECT patientID FROM patient WHERE account = ?";
			statement = conn.prepareStatement(sql);
			statement.setString(1, inputAccount);
			
			ResultSet resultSet = statement.executeQuery();
			
			while (resultSet.next()) {
				result = resultSet.getString("patientID");
			}
			
			if (resultSet != null) try { resultSet.close(); } catch (SQLException ignore) {}
			
			return result;
		}
		catch (SQLException ex) {
			System.out.println("發生SQLException");
		}
		finally {
			if (statement != null) try { statement.close(); }catch (SQLException ignore) {}
			//if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return result;
	}
	
	//搜尋病患姓名
	public static String getPatientName(Connection conn, String account){
		String patientName = "查無病患";
		PreparedStatement statement = null;
		try {
			String sql = "SELECT name FROM patient WHERE account = ?";
			statement = conn.prepareStatement(sql);
			statement.setString(1, account);
				
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
	
	//搜尋病患姓名
	public static String getPatientNameByID(Connection conn, String patientID){
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
	
	//取得稱謂
	public static String getKinship(Connection conn, String userID, String familyID){
		PreparedStatement statement = null;
		String kinship = "";
		try {
			statement = conn.prepareStatement("SELECT kinship FROM family WHERE userID = ? AND familyID = ?");
			statement.setString(1, userID);					//userID
			statement.setString(2, familyID);				//familyID		

			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				kinship = resultSet.getString("kinship");
			}
			if (resultSet != null) try { resultSet.close(); } catch (SQLException ignore) {}
		}
		catch (SQLException e) {
			System.out.println("修改稱謂時發生SQLException");
		}
		finally {
            if (statement != null) try {statement.close();}catch (SQLException ignore) {}
            if (conn!=null) try {conn.close();}catch (Exception ignore) {}
        }
		return kinship;
	}
	
	//搜尋家屬姓名
	public static String getFamilyName(Connection conn, String patientID, String senderID){
		String familyName = "";
		String kinship = "";
		PreparedStatement statement = null;
		try {
			statement = conn.prepareStatement("SELECT kinship, name " +
												"FROM family INNER JOIN patient ON family.familyID = patient.patientID "+
												"WHERE userID = ? AND familyID = ?");
			statement.setString(1, patientID);
			statement.setString(2, senderID);
				
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				familyName = resultSet.getString("name");
				kinship = resultSet.getString("kinship");
			}
				
			if (resultSet != null) try { resultSet.close(); } catch (SQLException ignore) {}
				
			if(kinship.equals("")){
				return familyName;
			}			
			else{
				return kinship;
			}
		}
		catch (SQLException ex) {
			System.out.println("發生SQLException : " + ex);
			return "資料庫發生錯誤";
		}
		finally {
			if (statement != null) try { statement.close(); }catch (SQLException ignore) {}
			//if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
	}
	
	//推播名單
	public static ArrayList<String> filePushList(Connection conn, String patientID){
		PreparedStatement statement = null;
		ArrayList<String> pushList = new ArrayList<String>(); 
			
		try {
			statement = conn.prepareStatement("SELECT familyID FROM family WHERE userID = ? AND fileLimit = 1");           
			statement.setString(1, patientID);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {				
				pushList.add(resultSet.getString("familyID"));
			}
			if (resultSet != null) try { resultSet.close(); } catch (SQLException ignore) {}
		  
		        //System.out.println(pushList.toString());
			return pushList;
		}
		catch (SQLException ex) {
			System.out.println("發生SQLException");
		}
		finally {
			if (statement != null) try { statement.close(); }catch (SQLException ignore) {}
			//if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return pushList;
	}
	
	//上傳檔案傳推播
	public static void sendFilePush(Connection conn, String patientID,String time){
		//推播名單
        ArrayList<String> pushList = FamilyServer.filePushList(conn, patientID);
        System.out.println("推播名單"+pushList);  
        for (String familyID : pushList) {
        	String familyName = FamilyServer.getFamilyName(conn, familyID, patientID);
        	
        	bbdp.push.fcm.PushToFCM.sendNotification("BBDP", familyName + "上傳了新的檔案", familyID, "FamilyFolder-view.html?familyID="+patientID+"&time="+time);
        }
	}
}
