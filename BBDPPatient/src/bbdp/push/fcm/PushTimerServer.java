package bbdp.push.fcm;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.tomcat.jdbc.pool.DataSource;

public class PushTimerServer {
	//設定推播計時器(type: 0-天, 1-週, 2-月, 3-秒*10(測試), 4-分(測試))(EX: 三(rate)天(type)一次，共三(times)次)
	public static void setPushTimer(DataSource datasource, int type, int rate, int times, String patientID, String title, String body) {
		Calendar calendar = Calendar.getInstance();
		ArrayList<String> timeArray = new ArrayList<String>();
		int hour = 9;
		
		if (type == 0) {		//天
			calendar.add(Calendar.DATE, 1);
			calendar.set(Calendar.HOUR_OF_DAY, hour);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			timeArray.add(calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DATE) + " " + calendar.get(Calendar.HOUR_OF_DAY)+ ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND));
			pushTimer(calendar, patientID, title, body);
			for (int i=0; i<times-1; i++) {
				calendar.add(Calendar.DATE, rate);
				timeArray.add(calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DATE) + " " + calendar.get(Calendar.HOUR_OF_DAY)+ ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND));
				pushTimer(calendar, patientID, title, body);
			}
		} else if(type == 1) {		//週
			calendar.add(Calendar.DATE, 1);
			calendar.set(Calendar.HOUR_OF_DAY, hour);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			timeArray.add(calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DATE) + " " + calendar.get(Calendar.HOUR_OF_DAY)+ ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND));
			pushTimer(calendar, patientID, title, body);
			for (int i=0; i<times-1; i++) {
				calendar.add(Calendar.DATE, rate * 7);
				timeArray.add(calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DATE) + " " + calendar.get(Calendar.HOUR_OF_DAY)+ ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND));
				pushTimer(calendar, patientID, title, body);
			}
		} else if(type == 2) {		//月
			calendar.add(Calendar.DATE, 1);
			calendar.set(Calendar.HOUR_OF_DAY, hour);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			timeArray.add(calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DATE) + " " + calendar.get(Calendar.HOUR_OF_DAY)+ ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND));
			pushTimer(calendar, patientID, title, body);
			for (int i=0; i<times-1; i++) {
				calendar.add(Calendar.MONTH, 1);
				timeArray.add(calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DATE) + " " + calendar.get(Calendar.HOUR_OF_DAY)+ ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND));
				pushTimer(calendar, patientID, title, body);
			}
		} else if(type == 3) {		//秒*10(測試)
			calendar.add(Calendar.SECOND, 10);
			timeArray.add(calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DATE) + " " + calendar.get(Calendar.HOUR_OF_DAY)+ ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND));
			pushTimer(calendar, patientID, title, body);
			for (int i=0; i<times-1; i++) {
				calendar.add(Calendar.SECOND, rate * 10);
				timeArray.add(calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DATE) + " " + calendar.get(Calendar.HOUR_OF_DAY)+ ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND));
				pushTimer(calendar, patientID, title, body);
			}
		} else if(type == 4) {		//分(測試)
			calendar.add(Calendar.MINUTE, 1);
			timeArray.add(calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DATE) + " " + calendar.get(Calendar.HOUR_OF_DAY)+ ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND));
			pushTimer(calendar, patientID, title, body);
			for (int i=0; i<times-1; i++) {
				calendar.add(Calendar.MINUTE, rate);
				timeArray.add(calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DATE) + " " + calendar.get(Calendar.HOUR_OF_DAY)+ ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND));
				pushTimer(calendar, patientID, title, body);
			}
		} 
		
		//存到資料庫裡
		Connection con = null;
		try {
		    con = datasource.getConnection();
		    Statement statement = con.createStatement();
		    for (int i=0; i<timeArray.size(); i++) {
		    	statement.executeUpdate("INSERT INTO pushtimer(pushtimerID, patientID, time, hyperlink, title, body) SELECT ifNULL(max(pushtimerID + 0), 0) + 1, '" + patientID + "', '" + timeArray.get(i) + "', '', '" + title + "', '" + body + "' FROM pushtimer");
		    }
		    statement.close();
		} catch (SQLException e) {
			System.out.println("BBDPPatient PushTimerServer setPushTimer1 SQLException: " + e);
		} finally {
			if (con != null) try {con.close();}catch (Exception ignore) {}
		}
	}
	
	//time格式: yyyy-mm-dd hh:mm:ss
	public static void setPushTimer(DataSource datasource, String time, String patientID, String title, String body) {
		pushTimer(stringToCalendar(time), patientID, title, body);
		//存到資料庫裡
		Connection con = null;
		try {
		    con = datasource.getConnection();
		    Statement statement = con.createStatement();
		    statement.executeUpdate("INSERT INTO pushtimer(pushtimerID, patientID, time, hyperlink, title, body) SELECT ifNULL(max(pushtimerID + 0), 0) + 1, '" + patientID + "', '" + time + "', '', '" + title + "', '" + body + "' FROM pushtimer");
		    statement.close();
		} catch (SQLException e) {
			System.out.println("BBDPPatient PushTimerServer setPushTimer2 SQLException: " + e);
		} finally {
			if (con != null) try {con.close();}catch (Exception ignore) {}
		}
	}
	
	//重新啟動所有的timer(在tomcat啟動時)，用listener呼叫
	public static void restartPushTimer(DataSource datasource) {
		deletePushTimerData(datasource);		//先判斷資料庫裡的時間是否為過去，過了就刪除
		Connection con = null;
		try {
		    con = datasource.getConnection();
		    Statement statement = con.createStatement();
		    ResultSet resultSet = statement.executeQuery("SELECT * FROM pushtimer");
		    while(resultSet.next()) {
	    		pushTimer(stringToCalendar(resultSet.getString("time")), resultSet.getString("patientID"), resultSet.getString("title"), resultSet.getString("body"));
	    	}
		    resultSet.close();
			statement.close();
		} catch (SQLException e) {
			System.out.println("BBDPPatient PushTimerServer restartPushTimer SQLException: " + e);
		} finally {
			if (con != null) try {con.close();}catch (Exception ignore) {}
		}
	}
	
	//計時器
	public static void pushTimer(Calendar calendar, String patientID, String title, String body) {
		Date date = calendar.getTime();
		Timer timer = new Timer();
		timer.schedule(new PushTimerTask(patientID, title, body), date);
	}
	
	//刪除資料庫裡的過期資料
	public static void deletePushTimerData(DataSource datasource) {
		Connection con = null;
		try {
		    con = datasource.getConnection();
		    Statement statement = con.createStatement();
		    ResultSet resultSet = statement.executeQuery("SELECT * FROM pushtimer ");
		    Statement statement2 = con.createStatement();
		    while(resultSet.next()) {
		    	if(isPast(resultSet.getString("time"))) {
		    		statement2.executeUpdate("DELETE FROM pushtimer WHERE pushtimerID = '" + resultSet.getString("pushtimerID") + "'");
		    	}
		    }
		    resultSet.close();
			statement.close();
			statement2.close();
		} catch (SQLException e) {
			System.out.println("BBDPPatient PushTimerServer deletePushTimerData SQLException: " + e);
		} finally {
			if (con != null) try {con.close();}catch (Exception ignore) {}
		}
	}
	
	//string(yyyy-mm-dd hh:mm:ss)轉calendar
	//       01234567890123456789
	private static Calendar stringToCalendar(String time) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Integer.parseInt(time.substring(0, 4)), Integer.parseInt(time.substring(5, 7)) - 1, Integer.parseInt(time.substring(8, 10)), Integer.parseInt(time.substring(11, 13)), Integer.parseInt(time.substring(14, 16)), Integer.parseInt(time.substring(17, 19)));
		return calendar;
	}
	
	//判斷時間是不是過去
	private static boolean isPast(String time) {
		Timestamp now = new Timestamp(System.currentTimeMillis());
		Timestamp timestamp = Timestamp.valueOf(time);
		return now.after(timestamp);
	}
}

//TimerTask
class PushTimerTask extends TimerTask {
	private String patientID;
	private String title;
	private String body;
	
	public PushTimerTask(String patientID, String title, String body) {
		this.patientID = patientID;
		this.title = title;
		this.body = body;
	}
	
	public void run() {
	 	System.out.println("現在時間：" + new Date());
	 	PushToFCM.sendNotification(this.title, this.body, this.patientID);
	 	System.out.println("發送推播至" + this.patientID + ":" + this.title + " / " + this.body);
	}
}