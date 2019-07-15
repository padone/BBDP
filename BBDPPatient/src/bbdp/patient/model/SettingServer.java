package bbdp.patient.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.apache.tomcat.jdbc.pool.DataSource;

public class SettingServer {
	//設置原本//PersonalData.html//已關資料庫
	public HashMap settingDefault(DataSource datasource, String patientID) {
		HashMap accountInfo = new HashMap();
		Connection con = null;
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select patientID, QRCode, account, password, name, birthday from patient where patientID = '"+patientID+"' ");

			String patient, QRCode = null, account = null, password = null, passwordCheck = null, name = null, birthday = null;

			while (rs.next()) {
				QRCode = rs.getString("QRCode");
				account = rs.getString("account");
				password = rs.getString("password");
				passwordCheck = password;
				name = rs.getString("name");
				birthday = rs.getString("birthday");
			}
			rs.close();//關閉rs
			
		    st.close();//關閉st
			
			accountInfo.put("QRCode", QRCode);
			accountInfo.put("account", account);
			accountInfo.put("name", name);
			accountInfo.put("password", password);
			accountInfo.put("passwordCheck", passwordCheck);
			accountInfo.put("birthday", birthday);
		} catch (SQLException e) {
			System.out.println("SettingServer settingDefault Exception :" + e.toString());
			e.printStackTrace();
		} finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return accountInfo;
	}

	//設置修改//PersonalData.html//已關資料庫
	public String settingChange(DataSource datasource, String patientID, String password, String passwordCheck, String name,
			String birthday) {
		String settingChange = "請重新整理";
		Connection con = null;
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			
			String updatedbSQL = "UPDATE patient " + "SET password='" + password + "',name='" + name + "',birthday='"
					+ birthday + "' where patientID = '"+patientID+"' ";
			int update = st.executeUpdate(updatedbSQL);
			if(update > 0)
				settingChange = "修改成功";
			else
				settingChange = "修改不成功";
		} catch (SQLException e) {
			System.out.println("SettingServer settingChange Exception :" + e.toString());
			e.printStackTrace();
		} finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return settingChange;
	}

	/*//失敗!請確認密碼符合格式並再度確認密碼!
	private boolean isInvalidPassword(String password, String confirmedPasswd) {
		return password == null || password.length() < 6 || password.length() > 15 || !password.equals(confirmedPasswd);
	}
	 */
	
	/*******************************************************************************************/
	//生活作息原本//Lifestyle.html//已關資料庫
	public HashMap lifestyleDefault(DataSource datasource, String patientID) {
		HashMap lifestyleDefault = new HashMap();	//生活作息原本
		Connection con = null;
		try {
			con = datasource.getConnection();
		    Statement st = con.createStatement();
		    ResultSet rs = st.executeQuery("select getUp,breakfast,lunch,dinner,sleep from lifestyle where patientID = '"+patientID+"' ");

			String getUp = null,breakfast = null,lunch = null,dinner = null,sleep = null;
			while (rs.next()) {
				getUp = rs.getString("getUp");
				breakfast = rs.getString("breakfast");
				lunch = rs.getString("lunch");
				dinner = rs.getString("dinner");
				sleep = rs.getString("sleep");
			}
			rs.close();//關閉rs
			
		    st.close();//關閉st
		    
		    lifestyleDefault.put("getUp", getUp);
		    lifestyleDefault.put("breakfast", breakfast);
		    lifestyleDefault.put("lunch", lunch);
		    lifestyleDefault.put("dinner", dinner);
		    lifestyleDefault.put("sleep", sleep);
		} catch (SQLException e) {
			System.out.println("SettingServer lifestyleDefault Exception :" + e.toString());
			e.printStackTrace();
		} finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}	
		return lifestyleDefault;
	}
	
	//生活作息修改//Lifestyle.html//已關資料庫
	public String lifestyleUpdate(DataSource datasource, String patientID, String getUp, String breakfast,
			String lunch, String dinner, String sleep) {
		String lifestyleUpdate = "";	//生活作息修改
		Connection con = null;
		try {
			con = datasource.getConnection();
		    Statement st = con.createStatement();
		    
			int update = st.executeUpdate("UPDATE lifestyle " + "SET getUp='" + getUp + "',breakfast='" + breakfast + "',lunch='"
					+ lunch + "',dinner='" + dinner + "',sleep='" + sleep + "' WHERE patientID='" + patientID + "'");
		  
			st.close();//關閉st
		    
		    if(update > 0){
		    	lifestyleUpdate = "修改成功";
		    }
		    else
		    	lifestyleUpdate = "修改不成功";
		    
		} catch (SQLException e) {
			System.out.println("SettingServer lifestyleUpdate Exception :" + e.toString());
			e.printStackTrace();
		} finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}	
		return lifestyleUpdate;
	}

}
