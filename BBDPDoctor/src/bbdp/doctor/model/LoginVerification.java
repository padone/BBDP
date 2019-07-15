package bbdp.doctor.model;

import java.sql.*;
import java.util.HashMap;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

public class LoginVerification {

	//已關資料庫
	public HashMap verification(DataSource datasource, String account, String password) {
		String result = "";
		String s, p, doctorID = null;
		Connection con = null;

		//解密
		password = bbdp.encryption.base64.BBDPBase64.decode(password);

		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select doctorID, account, password from doctor");

			while (rs.next()) {
				s = rs.getString("account");
				p = rs.getString("password");
				if (account.equals(s) == true) {// true 代表已有此使用者
					if (password.equals(p) == true) {
						result = "登入成功";// true 代表此使用者密碼正確
						doctorID = rs.getString("doctorID");
						break;
					} else if(password.equals("")){
						result = "請輸入密碼"; // false 代表此使用者密碼不正確
						break;
					} else{
						result = "密碼錯誤"; // false 代表此使用者密碼不正確
						break;
					}
				} else {
					result = "沒有此使用者";// false 代表沒有此使用者
				}
			}
			rs.close();//關閉rs

		    st.close();//關閉st			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		HashMap loginResult = new HashMap();
		loginResult.put("result", result);
		loginResult.put("doctorID", doctorID);
		return loginResult;
	}
}
