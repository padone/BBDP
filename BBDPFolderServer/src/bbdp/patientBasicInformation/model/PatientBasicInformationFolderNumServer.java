package bbdp.patientBasicInformation.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Period;

import org.apache.tomcat.jdbc.pool.DataSource;

public class PatientBasicInformationFolderNumServer {
	//近一個月的檔案數量
	public static String getRecentFolder(DataSource datasource, String patientID, String doctorID) {
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
			result = String.valueOf(countRecentFolderNum);
			resultSet.close();
			statement.close();
		} catch (SQLException e) {
			System.out.println("BBDPFolderServer PatientBasicInformationFolderNumServer getRecentFolder SQLException");
			result = "0";
		} finally {
			if (con != null) try {con.close();}catch (Exception ignore) {}
		}
		return result;
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