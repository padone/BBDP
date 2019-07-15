package bbdp.patient.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;


public class RegisterServer {

	//已關資料庫
	synchronized public HashMap registerAdd(DataSource datasource, String account, String password, String passwordCheck, String name,
			String birthday, String agree) {
		HashMap registerAdd = new HashMap();
		Connection con = null;
		String result = new String();
		
		try {
			con = datasource.getConnection();
		    Statement st = con.createStatement();
		    
			if (isInvalidUsername(account, datasource)) {
				result = "已有此使用者";
			}
			// 都沒有錯誤才新增註冊資料進入資料庫
			else {
				String QRcode = QRCodeIconGeneratorHandler(account);
				String sex = null;

				if (account.substring(1,2).equals("1")) {
					sex = "男";
				} else if (account.substring(1,2).equals("2")) {
					sex = "女";
				}
				/************************************新增patient(開始)*****************************************************/
				String insertdbSQL = "insert into patient(patientID,QRcode,account,password,name,sex,birthday,notification) "
				+ "select ifNULL(max(patientID+0),0)+1,'" + QRcode + "','" + account + "','" + password + "','"
						+ name + "','" + sex + "','" + birthday + "', 'yes' FROM patient";

				int insert = st.executeUpdate(insertdbSQL);
				st.close();//關閉st
				/************************************新增patient(結束)*****************************************************/
				/************************************新增lifestyle(開始)*****************************************************/
			    String patientID = null;
			    con = datasource.getConnection();
			    st = con.createStatement();
			    ResultSet rs = st.executeQuery("select max(patientID+0) from patient");

				while (rs.next()) {
					patientID = rs.getString("max(patientID+0)");
				}
				rs.close();//關閉rs
			    
			    con = datasource.getConnection();
			    st = con.createStatement();
			    int insertLifestyle = st.executeUpdate("insert into lifestyle(patientID,getUp,breakfast,lunch,dinner,sleep)"
			    									+ "values('"+patientID+"', '06:00:00', '08:00:00', '12:00:00', '17:00:00', '21:00:00')");
			    st.close();//關閉st
				/************************************新增lifestyle(結束)*****************************************************/
			    if(insert > 0)
			    	result = "成功";
			    else	
			    	result = "不成功";
			}
			registerAdd.put("result", result);
		} catch (SQLException e) {
			System.out.println("RegisterServer registerAdd Exception :" + e.toString());
			e.printStackTrace();
		} finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return registerAdd;
	}

	// 失敗!使用者帳號已註冊過!//已關資料庫
	private boolean isInvalidUsername(String account, DataSource datasource) {
		Connection con = null;
		try {
			con = datasource.getConnection();
		    Statement st = con.createStatement();
		    ResultSet rs = st.executeQuery("select account from patient where account = '"+account+"' ");

			String s;
			while (rs.next()) {
				return true; // true 代表已有此使用者
			}
			rs.close();//關閉rs
			
		    st.close();//關閉st
		} catch (SQLException e) {
			System.out.println("RegisterServer isInvalidUsername Exception :" + e.toString());
			e.printStackTrace();
		} finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}	
		return false;
	}

	/*//失敗!請確認密碼符合格式並再度確認密碼!
	private boolean isInvalidPassword(String password, String confirmedPasswd) {
		return password == null || password.length() < 6 || password.length() > 15 || !password.equals(confirmedPasswd);
	}
	*/
	/*//失敗!請輸入正確的身分證字號!
	private boolean isInvalidAccount(String account) {
		char[] array = account.toCharArray(); // array為account的字元陣列

		if (account.length() != 10) {	//身分證長度不等於10
			return false;
		} else if (array[0] < 'A' || array[0] > 'Z') {	//身分證字首不是英文或不是大寫
			return false;
		} else if (isNumber(array[1]) == true) {
			if (array[1] == '1') {
				sex = "男";
			} else if (array[1] == '2') {
				sex = "女";
			} else {	//第二個不是1、2
				return false;
			}
		} else if (isNumber(array[2]) == false || isNumber(array[3]) == false || isNumber(array[4]) == false
				|| isNumber(array[5]) == false || isNumber(array[6]) == false || isNumber(array[7]) == false
				|| isNumber(array[8]) == false || isNumber(array[9]) == false) {	//身分證字首以外的不是數字
			return false;
		}
		return true;
	}
	*/
	/*//輸入的字元是否為數字(true->輸入的確實為數字)
	private boolean isNumber(char a) {
		int n = Character.getNumericValue(a); // 字元轉成數字，字元只接受英文跟數字
		if (n >= 10) { // 小於10的話回傳false
			return false;
		} else {
			return true;
		}
	}
	 */
	/*//失敗!請先閱讀並同意使用者條款!
	private boolean isInvalidAgree(String agree) {
		if (agree.equals("false") == true)
			return true;
		else
			return false;
	}
	*/
	
	//QRcode產生
	private String QRCodeIconGeneratorHandler(String account) {
		String QRcode = "https://chart.googleapis.com/chart?chs=300x300&cht=qr&chl=" + bbdp.encryption.base64.BBDPBase64.encode(account) + "&choe=UTF-8";
		return QRcode;
	}

	public static void main(String[] args) {
		RegisterServer jdbc = new RegisterServer();
		// jdbc.registerAdd("H222222222", "123456", "123456", "黃佳惠", "女",
		// "1995/11/22", "true");
		//System.out.println("身份證字號長度是否正確 : " + jdbc.isInvalidAccount("H222222222"));
	}

}
