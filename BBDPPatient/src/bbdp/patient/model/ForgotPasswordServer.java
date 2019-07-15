package bbdp.patient.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.tomcat.jdbc.pool.DataSource;


public class ForgotPasswordServer {
	
	//已關資料庫
	public HashMap forgotPassword(DataSource datasource, String account, String birthday) {
		Connection con = null;
		HashMap forgotPassword = new HashMap();
		String result = "";
		boolean flag = false;

		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select account, password, birthday from patient");

			while (rs.next()) {
				if (account.equals(rs.getString("account")) == true) {// true 找到使用者
					if (birthday.equals(rs.getString("birthday"))) {
						result = "您的密碼是" + rs.getString("password");// true 代表此使用者生日正確
						flag = true;
						break;
					} else {
						result = "生日錯誤"; // false 代表此使用者密碼不正確
						break;
					}
				} else {
					result = "沒有此使用者";// false 代表沒有此使用者
				}
			}
			rs.close();//關閉rs
			st.close();//關閉st
			
			forgotPassword.put("result", result);
			forgotPassword.put("flag", flag);
		} catch (SQLException e) {
			System.out.println("PatientInstructionServer forgotPassword Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return forgotPassword;
	}
}
