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

public class HomepageServer {
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
	//取得首頁資料
	public static String getHomepageData(DataSource datasource, String doctorID) {
		Connection con = null;
		String result = "";
		
		try {
			JSONArray QList = new JSONArray();
		    con = datasource.getConnection();
		    Statement statement = con.createStatement();
		    //取得今日問卷資料
		    ResultSet resultSet = statement.executeQuery("SELECT answer.patientID, patient.name as patientName, answer.answerID, questionnaire.questionnaireID, questionnaire.name as questionnaireName, answer.date FROM questionnaire, answer, patient WHERE questionnaire.questionnaireID = answer.questionnaireID AND questionnaire.doctorID = '" + doctorID + "' AND answer.doctorID is NULL AND answer.patientID = patient.patientID ORDER BY date DESC");
		    while (resultSet.next()) {
				if(isToday(resultSet.getString("date").substring(0, 10))) {
					JSONObject QItem = new JSONObject();
					QItem.put("patientID", resultSet.getString("patientID"));
					QItem.put("patientName", resultSet.getString("patientName"));
					QItem.put("answerID", resultSet.getString("answerID"));
					QItem.put("questionnaireID", resultSet.getString("questionnaireID"));
					QItem.put("questionnaireName", resultSet.getString("questionnaireName"));
					QItem.put("date", resultSet.getString("date"));
					QList.put(QItem);
				} else {
					break;
				}
		    }
			result = QList.toString();
			resultSet.close();
			statement.close();
		} catch (SQLException e) {
			System.out.println("BBDPDoctor HomepageServer getHomepageData SQLException: " + e);
			result = "";
		} catch (JSONException e) {
			System.out.println("BBDPDoctor HomepageServer getHomepageData JSONException: " + e);
			result = "";
		} finally {
			if (con != null) try {con.close();}catch (Exception ignore) {}
		}
		//System.out.println(result);
		return result;
	}
	
	//判斷日期是否為今日
	public static boolean isToday(String inputDate) {
		int year = Integer.valueOf(inputDate.substring(0, 4));
		int month = Integer.valueOf(inputDate.substring(5, 7));
		int date = Integer.valueOf(inputDate.substring(8));
		LocalDate birthday = LocalDate.of(year, month, date);
		LocalDate today = LocalDate.now();
		Period period = Period.between(birthday, today);
		if(period.getDays() == 0) return true;
		return false;
	}
}