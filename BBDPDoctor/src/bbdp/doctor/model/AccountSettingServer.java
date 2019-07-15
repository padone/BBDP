package bbdp.doctor.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

public class AccountSettingServer {
	//已關資料庫
	public HashMap settingDefault(DataSource datasource, String doctorID) {
		HashMap accountInfo = new HashMap();
		String doctor, account = null, password = null, name = null, hospital = null, department = null, QRCode = null;
		Connection con = null;
		
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select  doctorID, account, password, name, hospital, department, QRCode from doctor where doctorID='"+ doctorID+"' ");

			while (rs.next()) {
				account = rs.getString("account");
				password = bbdp.encryption.base64.BBDPBase64.encode(rs.getString("password"));	//加密
				name = rs.getString("name");
				hospital = rs.getString("hospital");
				department = rs.getString("department");
				QRCode = rs.getString("QRCode");
			}
			rs.close();//關閉rs

		    st.close();//關閉st
			
			accountInfo.put("name", name);
			accountInfo.put("account", account);
			accountInfo.put("hospital", hospital);
			accountInfo.put("password", password);
			accountInfo.put("passwordCheck", password);
			accountInfo.put("department", department);
			accountInfo.put("QRCode", QRCode);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("AccountSettingServer settingDefault Exception :" + e.toString());
		} finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return accountInfo;
	}

	//不須關資料庫//帳戶設定
	public String settingChange(DataSource datasource, String doctorID, String password, String passwordCheck) {
		String result = null;
		Connection con = null;

		try {
			con = datasource.getConnection();
		    Statement st = con.createStatement();
			
		    password =    bbdp.encryption.base64.BBDPBase64.decode(password);	//解密
			int update = st.executeUpdate("UPDATE doctor " + "SET password='" + password + "' WHERE doctorID='" + doctorID + "'");

		    st.close();//關閉st
		    if(update > 0){
		    	result = "成功";
		    }
		    else{
		    	result = "不成功";
		    }
		} catch (SQLException e) {
			System.out.println("AccountSettingServer settingChange Exception :" + e.toString());
			e.printStackTrace();
		} finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}	
		return result;
	}

	//不須關資料庫//個人資料
	public String settingChange2(DataSource datasource, String doctorID, String name, String hospital, String department) {
		String result = null;
		Connection con = null;

		try {
			con = datasource.getConnection();
		    Statement st = con.createStatement();
			
			int update = st.executeUpdate("UPDATE doctor " + "SET name='" + name + "',hospital='"
					+ hospital + "',department='" + department + "' WHERE doctorID='" + doctorID + "'");

		    st.close();//關閉st
		    if(update > 0){
		    	result = "成功";
		    }
		    else{
		    	result = "不成功";
		    }
		} catch (SQLException e) {
			System.out.println("AccountSettingServer settingChange2 Exception :" + e.toString());
			e.printStackTrace();
		} finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}	
		return result;
	}
}
