package bbdp.patient.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bbdp.push.fcm.PushTimerServer;

public class RevisitTimeServer {
	//取得最新的回診時間
	public static String getLatestRevisitTime(DataSource datasource, String patientID) {System.out.println("patientID: " + patientID);
		deleteExpiredRevisitTime(datasource, patientID);		//刪除過時回診時間
		Connection con = null;
		String result = "";
		
		try {
			JSONObject LatestRVT = new JSONObject();
		    con = datasource.getConnection();
		    Statement statement = con.createStatement();
		    ResultSet resultSet = statement.executeQuery("SELECT * FROM calendar WHERE patientID = '" + patientID + "' ORDER BY date ASC");
		    if (resultSet.next()) {
		    	LatestRVT.put("patientID", resultSet.getString("patientID"));
		    	LatestRVT.put("date", resultSet.getString("date"));
		    	LatestRVT.put("hospital",resultSet.getString("hospital"));
		    	LatestRVT.put("department", resultSet.getString("department"));
		    	LatestRVT.put("timePeriod", resultSet.getInt("timePeriod"));
		    	LatestRVT.put("roomNumber", resultSet.getInt("roomNumber"));
		    	LatestRVT.put("number", resultSet.getInt("number"));
		    	LatestRVT.put("PS", resultSet.getString("PS"));
		    }
			result = LatestRVT.toString();
			resultSet.close();
			statement.close();
		} catch (SQLException e) {
			System.out.println("BBDPPatient RevisitTimeServer getLatestRevisitTime SQLException: " + e);
			result = "";
		} catch (JSONException e) {
			System.out.println("BBDPPatient RevisitTimeServer getLatestRevisitTime JSONException: " + e);
			result = "";
		} finally {
			if (con != null) try {con.close();}catch (Exception ignore) {}
		}
		//System.out.println(result);
		return result;
	}

	//取得回診時間列表
	public static String getRevisitTimeList(DataSource datasource, String patientID) {
		deleteExpiredRevisitTime(datasource, patientID);		//刪除過時回診時間
		Connection con = null;
		String result = "";
		
		try {
			JSONArray RVTList = new JSONArray();
		    con = datasource.getConnection();
		    Statement statement = con.createStatement();
		    ResultSet resultSet = statement.executeQuery("SELECT * FROM calendar WHERE patientID = '" + patientID + "' ORDER BY date ASC");
		    while (resultSet.next()) {
		    	JSONObject RVT = new JSONObject();
		    	RVT.put("patientID", resultSet.getString("patientID"));
		    	RVT.put("date", resultSet.getString("date"));
		    	RVT.put("hospital",resultSet.getString("hospital"));
		    	RVT.put("department", resultSet.getString("department"));
		    	RVT.put("timePeriod", resultSet.getInt("timePeriod"));
		    	RVT.put("roomNumber", resultSet.getInt("roomNumber"));
		    	RVT.put("number", resultSet.getInt("number"));
		    	RVT.put("PS", resultSet.getString("PS"));
		    	RVTList.put(RVT);
		    }
			result = RVTList.toString();
			resultSet.close();
			statement.close();
		} catch (SQLException e) {
			System.out.println("BBDPPatient RevisitTimeServer getLatestRevisitTime SQLException: " + e);
			result = "";
		} catch (JSONException e) {
			System.out.println("BBDPPatient RevisitTimeServer getLatestRevisitTime JSONException: " + e);
			result = "";
		} finally {
			if (con != null) try {con.close();}catch (Exception ignore) {}
		}
		//System.out.println(result);
		return result;
	}
	
	//刪除指定的回診時間
	public static String deleteRevisitTime(DataSource datasource, String patientID, String date, int timePeriod) {
		Connection con = null;
		String result = "";
		try {
		    con = datasource.getConnection();
		    Statement statement = con.createStatement();
		    ResultSet resultSet = statement.executeQuery("SELECT * FROM calendar WHERE patientID = '" + patientID + "' AND date = '" + date + "' AND timePeriod = " + timePeriod); 
		    if (resultSet.next()) {
		    	result = resultSet.getString("calendarID");
		    }
		    Statement statement2 = con.createStatement();
		    statement2.executeUpdate("DELETE FROM calendar WHERE patientID = '" + patientID + "' AND date = '" + date + "' AND timePeriod = " + timePeriod); 
		    resultSet.close();
			statement.close();
			statement2.close();
		} catch (SQLException e) {
			System.out.println("BBDPPatient RevisitTimeServer deleteRevisitTime SQLException: " + e);
		} finally {
			if (con != null) try {con.close();}catch (Exception ignore) {}
		}
		return result;
	}
	
	//取得所有醫院和科別資料
	public static String getHospital(DataSource datasource) {
		Connection con = null;
		String result = "";
		
		try {
			JSONObject allData = new JSONObject();
			JSONArray hospitalArray = new JSONArray();
		    con = datasource.getConnection();
		    Statement statement = con.createStatement();
		    ResultSet resultSet = statement.executeQuery("SELECT DISTINCT hospital FROM doctor");
		    Statement statement2 = con.createStatement();
		    ResultSet resultSet2 = null;
		    
		    while (resultSet.next()) {
		    	JSONObject hospital = new JSONObject();
		    	hospital.put("hospitalName", resultSet.getString("hospital"));
		    	JSONArray departmentArray = new JSONArray();
		    	resultSet2 = statement2.executeQuery("SELECT DISTINCT department FROM doctor WHERE hospital = '" + resultSet.getString("hospital") + "'");
		    	while (resultSet2.next()) {
		    		JSONObject department = new JSONObject();
		    		department.put("departmentName", resultSet2.getString("department"));
		    		departmentArray.put(department);
		    	}
		    	hospital.put("department", departmentArray);
		    	hospitalArray.put(hospital);
		    }
		    allData.put("hospital", hospitalArray);
			result = allData.toString();
			resultSet.close();
			statement.close();
			resultSet2.close();
			statement2.close();
		} catch (SQLException e) {
			System.out.println("BBDPPatient RevisitTimeServer getHospital SQLException: " + e);
			result = "";
		} catch (JSONException e) {
			System.out.println("BBDPPatient RevisitTimeServer getHospital JSONException: " + e);
			result = "";
		} finally {
			if (con != null) try {con.close();}catch (Exception ignore) {}
		}
		//System.out.println(result);
		return result;
	}

	//檢查是否有重複新增同一日同一時段
	public static String checkRevisitTimeIsRepeat(DataSource datasource, String patientID, String date, int timePeriod) {
		Connection con = null;
		String isRepeat = "no";
		
		try {
		    con = datasource.getConnection();
		    Statement statement = con.createStatement();
		    ResultSet resultSet = statement.executeQuery("SELECT * FROM calendar WHERE patientID = '" + patientID + "' AND date = '" + date + "' AND timePeriod = " + timePeriod);
		    if (resultSet.next()) {
		    	isRepeat = "yes";
		    } else {
		    	isRepeat = "no";
		    }
			resultSet.close();
			statement.close();
		} catch (SQLException e) {
			System.out.println("BBDPPatient RevisitTimeServer checkRevisitTimeIsDouble SQLException: " + e);
		} finally {
			if (con != null) try {con.close();}catch (Exception ignore) {}
		}
		return isRepeat;
	}
	
	//新增回診時間
	synchronized public static String newRevisitTime(DataSource datasource, String inputJSONString) {
		//分析input json
		JSONObject inputJSON;
		String patientID = null;
		String date = null;
		String hospital = null;
		String department = null;
		int timePeriod = -1;
		int roomNumber = -1;
		int number = 0;
		String PS = null;
		try {
			inputJSON = new JSONObject(inputJSONString);
			patientID = inputJSON.getString("patientID");
			date = inputJSON.getString("date");
			hospital = inputJSON.getString("hospital");
			department = inputJSON.getString("department");
			timePeriod = inputJSON.getInt("timePeriod");
			roomNumber = inputJSON.getInt("roomNumber");
			number = inputJSON.getInt("number");
			PS = inputJSON.getString("PS");
		} catch (JSONException e) {
			System.out.println("BBDPPatient RevisitTimeServer newRevisitTime parse json JSONException: " + e);
		}
		deleteExpiredRevisitTime(datasource, patientID);		//刪除過時回診時間
		
		//存資料
		Connection con = null;
		try {
		    con = datasource.getConnection();
		    Statement statement = con.createStatement();
		    statement.executeUpdate("INSERT INTO calendar(calendarID, patientID, date, hospital, department, timePeriod, roomNumber, number, PS) SELECT ifNULL(max(calendarID + 0), 0) + 1, '" + patientID + "', '" + date + "', '" + hospital + "', '" + department + "', '" + timePeriod + "', '" + roomNumber + "', '" + number + "', '" + PS + "' FROM calendar");
		    statement.close();
		} catch (SQLException e) {
			System.out.println("BBDPDoctor RevisitTimeServer newRevisitTime new SQLException: " + e);
		} finally {
			if (con != null) try {con.close();}catch (Exception ignore) {}
		}
		
		//取得ID
		String result = "";
		Connection con2 = null;
		
		try {
		    con2 = datasource.getConnection();
		    Statement statement2 = con2.createStatement();
		    ResultSet resultSet2 = statement2.executeQuery("SELECT calendarID FROM calendar WHERE patientID = '" + patientID + "' AND date = '" + date + "' AND timePeriod = " + timePeriod);
		    if (resultSet2.next()) {
		    	result = resultSet2.getString("calendarID");
		    } else {
		    	result = "error";
		    }
			resultSet2.close();
			statement2.close();
		} catch (SQLException e) {
			System.out.println("BBDPPatient RevisitTimeServer newRevisitTime get ID SQLException: " + e);
		} finally {
			if (con2 != null) try {con2.close();}catch (Exception ignore) {}
		}
		return result;
	}
	
	//新增回診時間時，加入推播(前一天早上9:00和當天早上9:00)
	public static void revisitTimePush(DataSource datasource, String inputJSONString) {
		String[] timePeriodtext = {"早上診", "下午診", "晚上診"};
		//分析input json
		JSONObject inputJSON;
		String patientID = null;
		String date = null;
		String hospital = null;
		String department = null;
		int timePeriod = -1;
		int number = 0;
		try {
			inputJSON = new JSONObject(inputJSONString);
			patientID = inputJSON.getString("patientID");
			date = inputJSON.getString("date");
			hospital = inputJSON.getString("hospital");
			department = inputJSON.getString("department");
			timePeriod = inputJSON.getInt("timePeriod");
			number = inputJSON.getInt("number");
			System.out.println("date: " + date);
			//設定計時器
			String title = "回診時間提醒";
			if(!isToday(date)) {		//日期不是當日
				String time1 = computeYesterday(date + " 09:00:00");
				String body1 = "明天須至" + hospital + " " + department + "回診(" + timePeriodtext[timePeriod] + number + "號)";
				PushTimerServer.setPushTimer(datasource, time1, patientID, title, body1);		//前一天早上9:00
			}
			String time2 = date + " 09:00:00";
			String body2 = "今天須至" + hospital + " " + department + "回診(" + timePeriodtext[timePeriod] + number + "號)";
			PushTimerServer.setPushTimer(datasource, time2, patientID, title, body2);		//當天早上9:00
		} catch (JSONException e) {
			System.out.println("BBDPPatient RevisitTimeServer revisitTimePush parse json JSONException: " + e);
		}
	}
	
	//刪除過時回診時間(日期一過直接刪)
	public static void deleteExpiredRevisitTime(DataSource datasource, String patientID) {
		Connection con = null;
		try {
		    con = datasource.getConnection();
		    Statement statement = con.createStatement();
		    ResultSet resultSet = statement.executeQuery("SELECT * FROM calendar WHERE patientID = '" + patientID + "'");
		    Statement statement2 = con.createStatement();
		    while(resultSet.next()) {
		    	if(isPast(resultSet.getString("date"))) {
		    		statement2.executeUpdate("DELETE FROM calendar WHERE patientID = '" + patientID + "' AND date = '" + resultSet.getString("date") + "' AND timePeriod = '" + resultSet.getString("timePeriod") + "'");
		    	}
		    }
		    resultSet.close();
			statement.close();
			statement2.close();
		} catch (SQLException e) {
			System.out.println("BBDPPatient RevisitTimeServer deleteExpiredRevisitTime SQLException: " + e);
		} finally {
			if (con != null) try {con.close();}catch (Exception ignore) {}
		}
	}
	
	//判斷日期是否為過去
	private static boolean isPast(String inputDate) {
		LocalDate now = LocalDate.now();
		LocalDate date = LocalDate.of(Integer.valueOf(inputDate.substring(0, 4)), Integer.valueOf(inputDate.substring(5, 7)), Integer.valueOf(inputDate.substring(8, 10)));
        return now.compareTo(date) > 0;
	}
	
	//計算前一天
	private static String computeYesterday(String time) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Integer.parseInt(time.substring(0, 4)), Integer.parseInt(time.substring(5, 7)) - 1, Integer.parseInt(time.substring(8, 10)), Integer.parseInt(time.substring(11, 13)), Integer.parseInt(time.substring(14, 16)), Integer.parseInt(time.substring(17, 19)));
		calendar.add(Calendar.DATE, -1);
		calendar.set(Calendar.MILLISECOND, 0);
		return new Timestamp(calendar.getTimeInMillis()).toString();
	}
	
	//判斷日期是否為今日
	private static boolean isToday(String inputDate) {
		int year = Integer.valueOf(inputDate.substring(0, 4));
		int month = Integer.valueOf(inputDate.substring(5, 7));
		int date = Integer.valueOf(inputDate.substring(8));
		LocalDate birthday = LocalDate.of(year, month, date);
		LocalDate today = LocalDate.now();
		Period period = Period.between(birthday, today);
        return period.getDays() == 0;
    }
}