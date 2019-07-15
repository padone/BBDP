package bbdp.homepage.model;

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

public class HomepageFolderDataServer {
	//取得首頁資料
	public static String getHomepageFolderData(DataSource datasource, String doctorID) {
		Connection con = null;
		String result = "";
		try {
			JSONArray FList = new JSONArray();
		    con = datasource.getConnection();
		    Statement statement = con.createStatement();
		    //取得今日檔案資料
		    ResultSet resultSet = statement.executeQuery("SELECT patientID, time, video, description FROM file where doctorID = '" + doctorID + "' ORDER BY time DESC");
		    while (resultSet.next()) {
		    	if(isToday(resultSet.getString("time").substring(0, 10))) {
		    		JSONObject FItem = new JSONObject();
					FItem.put("patientID", resultSet.getString("patientID"));
					FItem.put("time", resultSet.getString("time"));
					if(resultSet.getString("video") == null || resultSet.getString("video").equals("")) {
						FItem.put("pictureOrVideo", "picture");
					} else {
						FItem.put("pictureOrVideo", "video");
					}
					FItem.put("description", resultSet.getString("description"));
					FList.put(FItem);
				} else {
					break;
				}
		    }
			result = FList.toString();
			resultSet.close();
			statement.close();
		} catch (SQLException e) {
			System.out.println("BBDPFolderServer HomepageFolderDataServer getHomepageFolderData SQLException: " + e);
			result = "";
		} catch (JSONException e) {
			System.out.println("BBDPFolderServer HomepageFolderDataServer getHomepageFolderData JSONException: " + e);
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
		int date = Integer.valueOf(inputDate.substring(8, 10));
		LocalDate birthday = LocalDate.of(year, month, date);
		LocalDate today = LocalDate.now();
		Period period = Period.between(birthday, today);
		if(period.getDays() == 0) return true;
		return false;
	}
}