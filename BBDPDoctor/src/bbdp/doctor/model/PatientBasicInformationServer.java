package bbdp.doctor.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Period;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bbdp.encryption.base64.BBDPBase64;

public class PatientBasicInformationServer {
	//取得病患姓名
	public static String getPatientName(DataSource datasource, String patientID) {
		Connection con = null;
		String result = "";
		
		try {
		    con = datasource.getConnection();
		    Statement statement = con.createStatement();
		    ResultSet resultSet = statement.executeQuery("SELECT name FROM patient WHERE patientID = '" + patientID + "'");
		    while (resultSet.next()) {
		        result = resultSet.getString("name");
		    }
			if(result.equals("")) {
				System.out.println("BBDPDoctor PatientBasicInformationServer getPatientName empty");
				result = "病患姓名";
			}
			resultSet.close();
			statement.close();
		} catch (SQLException e) {
			System.out.println("BBDPDoctor PatientBasicInformationServer getPatientName SQLException: " + e);
			result = "病患姓名";
		} finally {
			if (con != null) try {con.close();}catch (Exception ignore) {}
		}
		return result;
	}
	
	//取得病患所有的基本資料
	public static String getPatientBasicInformation(DataSource datasource, String patientID, String doctorID) {
		//System.out.println("{" + getPatientBasicInfo(datasource, patientID) + ", " + getLatestMedicalRecord(datasource, patientID, doctorID) + ", " + getRecentQuestionnaire(datasource, patientID, doctorID) + ", " + getRecentFolder(datasource, patientID, doctorID) + ", " + getRecentHealthTracking(datasource, patientID, doctorID) + "}");
		return "{" + getPatientBasicInfo(datasource, patientID) + ", " + getLatestMedicalRecord(datasource, patientID, doctorID) + ", " + getRecentQuestionnaire(datasource, patientID, doctorID) + ", " + getRecentFolder(datasource, patientID, doctorID) + ", " + getRecentHealthTracking(datasource, patientID, doctorID) + "}";
	}
	
	//病患姓名, 生日, 年齡(計算), 身分證字號
	private static String getPatientBasicInfo(DataSource datasource, String patientID) {
		Connection con = null;
		String result = "";
		String encodedAccount = "";
		try {
		    con = datasource.getConnection();
		    Statement statement = con.createStatement();
		    ResultSet resultSet = statement.executeQuery("SELECT * FROM patient WHERE patientID = '" + patientID + "'");
			while (resultSet.next()) {
				encodedAccount = BBDPBase64.encode(resultSet.getString("account"));
				result = "\"patientID\": \"" + patientID + "\", \"patientName\": \"" + resultSet.getString("name") + "\", \"birth\": \"" + resultSet.getString("birthday") + "\", \"age\": " + calculateAge(resultSet.getString("birthday")) + ", \"account\": \"" + encodedAccount + "\"";
			}
			if(result.equals("")) {
				result = "\"patientName\": " + "\"資料空白\"" + ", \"birth\": " + "\"資料空白\"" + ", \"age\": " + "\"0\"" + ", \"account\": " + "\"資料空白\"";
			}
			resultSet.close();
			statement.close();
		} catch (SQLException e) {
			System.out.println("BBDPDoctor PatientBasicInformationServer getPatientBasicInformation SQLException");
			result = "\"patientName\": " + "\"取得資料失敗\"" + ", \"birth\": " + "\"取得資料失敗\"" + ", \"age\": " + "\"0\"" + ", \"account\": " + "\"取得資料失敗\"";
		} finally {
			if (con != null) try {con.close();}catch (Exception ignore) {}
		}
		return result;
	}
	
	//近一個月的問卷數量
	private static String getRecentQuestionnaire(DataSource datasource, String patientID, String doctorID) {
		Connection con = null;
		String result = "";
		try {
			con = datasource.getConnection();
		    Statement statement = con.createStatement();
		    ResultSet resultSet = statement.executeQuery("SELECT date FROM questionnaire, answer WHERE patientID = '" + patientID + "' AND questionnaire.questionnaireID = answer.questionnaireID AND questionnaire.doctorID = '" + doctorID + "' AND answer.doctorID is NULL");
			int countRecentQuestionnireNum = 0;
			while (resultSet.next()) {
				if(isRecent(resultSet.getString("date").substring(0, 10))) countRecentQuestionnireNum++;
			}
			result = "\"QNum\": " + countRecentQuestionnireNum;
			resultSet.close();
			statement.close();
		} catch (SQLException e) {
			System.out.println("BBDPDoctor PatientBasicInformationServer getRecentQuestionnaire SQLException");
			result = "\"QNum\": " + "0";
		} finally {
			if (con != null) try {con.close();}catch (Exception ignore) {}
		}
		return result;
	}
	
	//近一個月的檔案數量
	private static String getRecentFolder(DataSource datasource, String patientID, String doctorID) {
		Connection con = null;
		String result = "";
		try {
			con = datasource.getConnection();
		    Statement statement = con.createStatement();
		    ResultSet resultSet = statement.executeQuery("SELECT time FROM file WHERE patientID = '" + patientID + "' and doctorID = '" + doctorID + "'");
			int countRecentFolderNum = 0;
			while (resultSet.next()) {
				if(isRecent(resultSet.getString("time").substring(0, 10))) countRecentFolderNum++;
			}
			result = "\"FNum\": " + countRecentFolderNum;
			resultSet.close();
			statement.close();
		} catch (SQLException e) {
			System.out.println("BBDPDoctor PatientBasicInformationServer getRecentFolder SQLException");
			result = "\"FNum\": " + "0";
		} finally {
			if (con != null) try {con.close();}catch (Exception ignore) {}
		}
		return result;
	}
	
	//最新的病歷摘要
	private static String getLatestMedicalRecord(DataSource datasource, String patientID, String doctorID) {		
		Connection con = null;
		String result = "";
		String content = "";
		String time = "";
		try {
			con = datasource.getConnection();
		    Statement statement = con.createStatement();
		    ResultSet resultSet = statement.executeQuery("SELECT * FROM medicalrecord WHERE addTime = (SELECT MAX(addTime) FROM medicalrecord WHERE patientID = '" + patientID + "' and doctorID = '" + doctorID + "')");
			while (resultSet.next()) {
				content = resultSet.getString("content").replaceAll("\r\n|\n", "<br>");
				time = resultSet.getString("addTime");
			}

			result = "\"MRTime\": \"" + time + "\", \"MRContent\": \"" + content + "\"";
			if(result.equals("")) {
				result = "\"MRTime\": " + "\"資料空白\"" + ", \"MRContent\": " + "\"資料空白\"";
			}
			resultSet.close();
			statement.close();
		} catch (SQLException e) {
			System.out.println("BBDPDoctor PatientBasicInformationServer getLatestMedicalRecord SQLException: " + e);
			result = "\"MRTime\": " + "\"取得資料失敗\"" + ", \"MRContent\": " + "\"取得資料失敗\"";
		} finally {
			if (con != null) try {con.close();}catch (Exception ignore) {}
		}
		//System.out.println("result: " + result);
		return result;
	}
	
	//近一個月的健康狀況追蹤資料
	private static String getRecentHealthTracking(DataSource datasource, String patientID, String doctorID) {
		Connection con = null;
		String result = "";
		try {
			JSONArray HT = new JSONArray();
			con = datasource.getConnection();
		    Statement statement = con.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT healthTrackingID, itemID, name, detail FROM healthtracking NATURAL JOIN healthtrackingitem WHERE patientID = '" + patientID + "' and doctorID = '" + doctorID + "' ORDER BY itemID ASC");
			
		    Statement statement2 = con.createStatement();
		    ResultSet detailNameResultSet = null;
		    
			while (resultSet.next()) {
				JSONObject HTItem = new JSONObject();
				HTItem.put("HTItemName", resultSet.getString("name"));		//放項目名稱
				HTItem.put("HTTime", new JSONArray());
				HTItem.put("HTDetail", new JSONArray());
				//放detail name
				JSONObject detail = new JSONObject(resultSet.getString("detail"));
				for (int i = 0; i < detail.getJSONArray("detailID").length(); i++) {
					String detailID = detail.getJSONArray("detailID").get(i).toString();
					
					detailNameResultSet = statement2.executeQuery("SELECT * FROM healthtrackingdetail WHERE detailID = '" + detailID + "'");
					JSONObject HTDetail = new JSONObject();
					while (detailNameResultSet.next()) {
						HTDetail.put("HTDetailName", detailNameResultSet.getString("name"));
					}
					HTDetail.put("HTDetailValue", new JSONArray());
					HTItem.getJSONArray("HTDetail").put(HTDetail);
				}
				HT.put(HTItem);
			}
			
			resultSet = statement.executeQuery("SELECT healthTrackingID, itemID, name, time, value FROM healthtracking NATURAL JOIN healthtrackingitem NATURAL JOIN history WHERE patientID = '" + patientID + "' and doctorID = '" + doctorID + "'ORDER BY time ASC");
			while (resultSet.next()) {
				if (isRecent(resultSet.getString("time").substring(0, 10))) {		//最近一個月
					for (int i = 0; i < HT.length(); i++) {
						if (resultSet.getString("name").equals(HT.getJSONObject(i).get("HTItemName"))) {
							HT.getJSONObject(i).getJSONArray("HTTime").put(resultSet.getString("time"));		//放時間
							//放detail value
							JSONObject value = new JSONObject(resultSet.getString("value"));
							for (int j = 0; j < value.getJSONArray("detail").length(); j++) {
								HT.getJSONObject(i).getJSONArray("HTDetail").getJSONObject(j).getJSONArray("HTDetailValue").put(value.getJSONArray("detail").getJSONObject(j).get("detailValue"));
							}
						}
					}
				}
			}
			//System.out.println(HT);
			result = "\"healthTracking\": " + HT.toString();
			
			if(result.equals("") || result.equals("\"healthTracking\": []")) {
				result = "\"healthTracking\": " + "\"資料空白\"";
			}
			resultSet.close();
			statement.close();
			if (detailNameResultSet != null) detailNameResultSet.close();
			statement2.close();
		} catch (SQLException e) {
			System.out.println("BBDPDoctor PatientBasicInformationServer getRecentHealthTracking SQLException: " + e);
			result = "\"healthTracking\": " + "\"取得資料失敗\"";
		} catch (JSONException e) {
			System.out.println("BBDPDoctor PatientBasicInformationServer getRecentHealthTracking JSONException: " + e);
			result = "\"healthTracking\": " + "\"取得資料失敗\"";
		} finally {
			if (con != null) try {con.close();}catch (Exception ignore) {}
		}
		return result;
	}
	
	//計算年齡
	private static int calculateAge(String birth) {
		int year = Integer.valueOf(birth.substring(0, 4));
		int month = Integer.valueOf(birth.substring(5, 7));
		int date = Integer.valueOf(birth.substring(8));
		LocalDate birthday = LocalDate.of(year, month, date);
		LocalDate today = LocalDate.now();
		Period period = Period.between(birthday, today);
		return period.getYears();
	}
	
	//判斷日期是否是最近一個月
	private static boolean isRecent(String inputDate) {
		int year = Integer.valueOf(inputDate.substring(0, 4));
		int month = Integer.valueOf(inputDate.substring(5, 7));
		int date = Integer.valueOf(inputDate.substring(8));
		LocalDate birthday = LocalDate.of(year, month, date);
		LocalDate today = LocalDate.now();
		Period period = Period.between(birthday, today);
		if(period.getMonths() == 0) return true;
		return false;
	}
}