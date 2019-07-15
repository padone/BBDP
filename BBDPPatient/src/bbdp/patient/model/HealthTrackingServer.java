package bbdp.patient.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import com.google.gson.Gson;

public class HealthTrackingServer {

	// 新增紀錄:找到項目//NewHealthTracking.html//已關資料庫
	public HashMap newHealthItemDefault(DataSource datasource, String patientID) {
		HashMap newHealthItem = new HashMap();
		Connection con = null;
		try {
			con = datasource.getConnection();
		    Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select itemID, name, doctorID, next, hideHealthtracking from healthtracking NATURAL JOIN healthtrackingitem where patientID = '"+patientID+"' ");

			ArrayList iDList = new ArrayList();
			ArrayList nameList = new ArrayList();
			ArrayList docotrNameList = new ArrayList();
			ArrayList hospitalList = new ArrayList();
			ArrayList departmentList = new ArrayList();
			String today = getNowDate();
		
			while (rs.next()) {
				System.out.println("rs.getString(next) : " + rs.getString("next"));
				System.out.println("rs.getString(doctorID) : " + rs.getString("doctorID"));

				// 如果下次可紀錄時間<=今天日期，才顯示項目給病患紀錄
				if(rs.getString("next").substring(0, 19).compareTo(today)<=0 && rs.getString("hideHealthtracking").equals("0")){
					iDList.add(rs.getString("itemID")); 	// 取得使用者的項目
					nameList.add(rs.getString("name")); 	// 取得項目名稱
					docotrNameList.add((getDoctorNameHospitalDepartment(datasource,rs.getString("doctorID")).get("docotrName")));
					hospitalList.add((getDoctorNameHospitalDepartment(datasource,rs.getString("doctorID")).get("hospital")));
					departmentList.add((getDoctorNameHospitalDepartment(datasource,rs.getString("doctorID")).get("department")));
				}
			}
			rs.close();//關閉rs
		    
			st.close();//關閉st
			
			newHealthItem.put("iDList", iDList);
			newHealthItem.put("nameList", nameList);
			newHealthItem.put("docotrNameList", docotrNameList);
			newHealthItem.put("hospitalList", hospitalList);
			newHealthItem.put("departmentList", departmentList);
		} catch (SQLException e) {
			System.out.println("HealthTrackingServer newHealthItemDefault Exception :" + e.toString());
			e.printStackTrace();
		} finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return newHealthItem;
	}
	
	// 新增紀錄:取得醫生//NewHealthTracking.html//已關資料庫
	public HashMap getDoctorNameHospitalDepartment(DataSource datasource, String doctorID){
		HashMap getDoctorNameHospitalDepartment = new HashMap();
		Connection con = null;
		try {
			con = datasource.getConnection();
		    Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select name, hospital, department from doctor where doctorID = '"+doctorID+"' ");
			while (rs.next()) {
				getDoctorNameHospitalDepartment.put("docotrName", rs.getString("name"));
				getDoctorNameHospitalDepartment.put("hospital", rs.getString("hospital"));
				getDoctorNameHospitalDepartment.put("department", rs.getString("department"));
			}
			rs.close();//關閉rs
		    
			st.close();//關閉st
			System.out.println("HealthTrackingServer getDoctorNameHospitalDepartment :" + getDoctorNameHospitalDepartment);

		} catch (SQLException e) {
			System.out.println("HealthTrackingServer getDoctorNameHospitalDepartment Exception :" + e.toString());
			e.printStackTrace();
		} finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return getDoctorNameHospitalDepartment;
	}

	// 新增紀錄:找到細項//NewHealthTracking.html//已關資料庫
	public HashMap newHealthDetail(DataSource datasource, String itemID){
		HashMap newHealthDetail = new HashMap();	//回傳結果
		Connection con = null;
		Gson gson = new Gson();
		
		String detailJson = null;
		String tempID, itemName = null, selfDescription = null;
		ArrayList iDList = new ArrayList();			//細項id
		ArrayList nameList = new ArrayList();		//細項name
		ArrayList unitList = new ArrayList();		//細項unit
		ArrayList range_1List = new ArrayList();	//細項合理值(最低)
		ArrayList range_2List = new ArrayList();	//細項合理值(最高)
		
		try {
			con = datasource.getConnection();
		    Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select detail, name, selfDescription from healthtrackingitem where itemID='"+itemID+"' ");
			
			while (rs.next()) {
				itemName = rs.getString("name"); 		 			// 取得項目的name
				detailJson = rs.getString("detail"); 				// 取得項目的細項
				selfDescription = rs.getString("selfDescription"); 	// 取得項目的文字敘述
			}
			rs.close();//關閉rs
			//////////////////////////解析item detail json(開始)////////////////////////////////////////////////////
			ItemDetail detailList = gson.fromJson(detailJson, ItemDetail.class);
			for(int i = 0; i < detailList.detailID.size(); i++){
				tempID = detailList.detailID.get(i);
				//取得細項內容
				st = con.createStatement();
				rs = st.executeQuery("select detailID, name, unit, range_1, range_2  from healthtrackingdetail where detailID='"+tempID+"' ");
				
				while (rs.next()) {// true 找到此細項ID
					iDList.add(tempID);
					nameList.add(rs.getString("name"));
					unitList.add(rs.getString("unit"));
					range_1List.add(rs.getString("range_1"));
					range_2List.add(rs.getString("range_2"));
				}
				rs.close();//關閉rs
			}
			//////////////////////////解析item detail json(結束)////////////////////////////////////////////////////
		    st.close();//關閉st
			
			newHealthDetail.put("iDList", iDList);
			newHealthDetail.put("nameList", nameList);
			newHealthDetail.put("unitList", unitList);
			newHealthDetail.put("range_1List", range_1List);
			newHealthDetail.put("range_2List", range_2List);
			newHealthDetail.put("itemName", itemName);
			newHealthDetail.put("selfDescription", selfDescription);
		} catch (SQLException e) {
			System.out.println("HealthTrackingServer itemDefault Exception :" + e.toString());
			e.printStackTrace();
		} finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return newHealthDetail;	
	}
	
	//新增紀錄:細項
	class ItemDetail {
		List<String> detailID;
		ItemDetail(List<String> detailID){
			this.detailID = detailID;
		}
	}
	
	//新增紀錄:新增紀錄//NewHealthTracking.html//已關資料庫
	synchronized public HashMap addHealth(DataSource datasource, String patientID, String itemNumber, String[] detailIdArray, String[] detailValueArray, String selfDescriptionValue){	
		Gson gson = new Gson();
		HashMap addHealth = new HashMap();	//回傳結果
		Connection con = null;
		String result = "請重新嘗試";
				
		//取得要存放的value值
		String value = "{\"itemID\": \""+itemNumber+"\", "+
				"\"detail\": [";
		String temp;
		for(int i=0; i<detailIdArray.length; i++){
			temp = "{\"detailID\": \""+detailIdArray[i]+"\", \"detailValue\": "+detailValueArray[i]+"}, ";
			value += temp;
		}
		value = value.substring(0,value.length()-2);	//去掉最後多餘的一個空格和一個，
		value += "]}";
		
		try {
			con = datasource.getConnection();
		    Statement st = con.createStatement();
			//////////////////////////取得cycle, healthTrackingID(開始)////////////////////////////////////////////////////
			ResultSet rs = st.executeQuery("select healthTrackingID, patientID, itemID, next, cycle from healthtracking NATURAL JOIN healthtrackingitem where itemID='"+itemNumber+"' and patientID='"+patientID+"' ");

			String healthTrackingID = null, cycle = null;
			
			while (rs.next()) {
				healthTrackingID = rs.getString("healthTrackingID");	//取得追蹤ID
				cycle = rs.getString("cycle");	//取得項目的週期
			}	
			rs.close();//關閉rs
			//////////////////////////取得cycle, healthTrackingID(結束)////////////////////////////////////////////////////
			//////////////////////////新增history紀錄(開始)////////////////////////////////////////////////////
			String today = getNowDate();
			
		    st = con.createStatement();
			int insert = st.executeUpdate("insert into history(time,healthTrackingID,value, selfDescriptionValue)" +
										"values('"+ today +"', '"+ healthTrackingID +"', '"+ value +"', '"+ selfDescriptionValue +"')");

			//////////////////////////新增history紀錄(開始)////////////////////////////////////////////////////
			//////////////////////////修改next(開始)////////////////////////////////////////////////////
			if(insert>0){
				//取得lifeStyle生活作息時間
			    st = con.createStatement();
			    String next = null;
			    rs = st.executeQuery("select getUp,breakfast,lunch,dinner,sleep from lifestyle where patientID = '"+patientID+"' ");
				while (rs.next()) {
					//分析cycle//取得next時間
					next = getNext(cycle, rs.getString("getUp"), rs.getString("breakfast"),  rs.getString("lunch"),  rs.getString("dinner"),  rs.getString("sleep"));
				}
				rs.close();//關閉rs
				
				st = con.createStatement();
				int update = st.executeUpdate("update healthtracking SET next='"+next+"' where patientID='"+patientID+"' and itemID='"+itemNumber+"' ");
				if(update>0){
					result = "新增成功";
				}
				else
					result = "新增失敗";
			}
			else
				result = "新增失敗";
			//////////////////////////修改next(結束)////////////////////////////////////////////////////
			
		    st.close();//關閉st

			addHealth.put("result", result);
		} catch (SQLException e) {
			System.out.println("HealthTrackingServer addHealth Exception :" + e.toString());
			e.printStackTrace();
		} finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return addHealth;
	}
	
	//分析cycle//取得next時間
	public static String getNext(String cycle, String getUp, String breakfast, String lunch, String dinner, String sleep){
		String next;
		String[] cycleSplit = cycle.split("\\.");
		//計算當下日期cycle
		Calendar cal = Calendar.getInstance();//使用預設時區和語言環境獲得一個日曆。  

	    //通過格式化輸出日期  
		java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	    next = format.format(cal.getTime());
	    //System.out.println("next brfore : " + next);
	    
	    String hour = next.substring(11, 19);
	    //System.out.println("hour : " + hour);
	    int tempCycle = Integer.parseInt(cycleSplit[0]);

	    if(cycleSplit[1].equals("0")){
	    }
	    else if(cycleSplit[1].equals("1")){
	    	hour = breakfast;
	    	cal.add(Calendar.DAY_OF_MONTH, +tempCycle);			//當前日期加上cycle日期
	    }
	    else if(cycleSplit[1].equals("2")){						//5:00//17:00
	    	if(hour.compareTo(dinner) < 0){					//現在時間小於17:00
	    		hour = dinner;
	    	}
	    	else if(hour.compareTo(dinner) >= 0){				//現在時間大於17:00
	    		hour = breakfast;
	    		cal.add(Calendar.DAY_OF_MONTH, +tempCycle);		//當前日期加上cycle日期
	    	}
	    }
	    else if(cycleSplit[1].equals("3")){						//5:00//11:00//17:00
	    	if(hour.compareTo(lunch) < 0){						//現在時間小於11:00
	    		hour = lunch;
	    	}
	    	else if(hour.compareTo(dinner) < 0){				//現在時間小於17:00
	    		hour = dinner;
	    	}
	    	else if(hour.compareTo(dinner) >= 0){				//現在時間大於17:00
	    		hour = breakfast;
	    		cal.add(Calendar.DAY_OF_MONTH, +tempCycle);		//當前日期加上cycle日期
	    	}
	    }
	    else if(cycleSplit[1].equals("4")){						//5:00//11:00//17:00//20:00
	    	if(hour.compareTo(lunch) < 0){						//現在時間小於11:00
	    		hour = lunch;
	    	}
	    	else if(hour.compareTo(dinner) < 0){				//現在時間小於17:00
	    		hour = dinner;
	    	}
	    	else if(hour.compareTo(sleep) < 0){					//現在時間小於20:00
	    		hour = sleep;
	    	}
	    	else if(hour.compareTo(sleep) >= 0){				//現在時間大於20:00
	    		hour = breakfast;
	    		cal.add(Calendar.DAY_OF_MONTH, +tempCycle);		//當前日期加上cycle日期
	    	}
	    }
	    
	    next = format.format(cal.getTime());	    
	    next = next.substring(0, 11) + hour;	//銜接
	    //System.out.println("next after : " + next);
		return next;
	}
	
	//取得現在的時間
	public static String getNowDate(){
		//取得現在時間
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = simpleDateFormat.format(timestamp);
		return currentTime;
	}
	
	//新增紀錄:細項
	/*
	class historyValue1 {
		String item;
		List<String> detailIdArray;
		List<String> detailValueArray;
		historyValue1(String item, List<String> detailIdArray, List<String> detailValueArray){
			this.item = item;
			this.detailIdArray = detailIdArray;
			this.detailValueArray = detailValueArray;
		}
	}
	*/
	
	/*******************************************************************************************/
/*
	// 統計圖表:取得可選取的項目列表//HistoryHealthTrackingChartList.html//已關資料庫
	public HashMap itemChart(DataSource datasource, String patientID) {
		HashMap ItemChart = new HashMap();
		try {
			ResultSet rs = conn.runSql("select  itemID, name, next from healthtracking NATURAL JOIN healthtrackingitem where patientID = '"+patientID+"' ");
			System.out.println("Listener runSql 成功");

			String itemID, name;

			ArrayList iDList = new ArrayList();
			ArrayList nameList = new ArrayList();
			
			LocalDate now = LocalDate.now();
			String today = now.toString();
		
			while (rs.next()) {
				itemID = rs.getString("itemID"); 	// 取得使用者的項目
				name = rs.getString("name"); 		// 取得項目名稱
				
				iDList.add(itemID);
				nameList.add(name);	
			}
			if (rs != null){ try {rs.close(); System.out.println("關閉ResultSet");} catch (SQLException ignore) {}}//關閉resultSet

			ItemChart.put("iDList", iDList);
			ItemChart.put("nameList", nameList);

		} catch (SQLException e) {
			System.out.println("HealthTrackingServer ItemChart Exception :" + e.toString());
		}
		return ItemChart;
	}
	
	// 統計圖表:取得圖表資料//HistoryHealthTrackingChartList.html//已關資料庫
	public HashMap itemChartShow(DataSource datasource, String patientID, String itemID, String dateStart, String dateEnd){
		HashMap ItemChartShow = new HashMap();
		Gson gson = new Gson();
		itemID = (String) itemID.subSequence(4, itemID.length());	//從第5個字元開始，取得itemID
		try {
			ResultSet rs;

			String time, value;
			boolean dateIsBetween;		//判斷時間是否介於開始日期及結束日期之間

			ArrayList itemTimeList = new ArrayList(); 			// item time record List
			ArrayList itemValueList = new ArrayList(); 			// item value record List

			//////////////////////////取得item Value、Time(開始)////////////////////////////////////////////////////
			rs = conn.runSql("SELECT time, value FROM healthtracking NATURAL JOIN healthtrackingitem NATURAL JOIN history WHERE patientID = '"
							+ patientID + "' and itemID = '"+itemID+"' ORDER BY time ASC");
			System.out.println("Listener runSql 成功");
			while (rs.next()) {
				time = rs.getString("time");
				value = rs.getString("value");
				time = time.substring(0, 16);
				
				if(betweenDate(dateStart, dateEnd, time.substring(0, 10))){			//如果介於開始與結束日期之間
					itemTimeList.add(time);
					itemValueList.add(value);
					//System.out.println("betweenDate : " + time.substring(0, 10));
				}
			}
			if (rs != null){ try {rs.close(); System.out.println("關閉ResultSet");} catch (SQLException ignore) {}}
			//////////////////////////取得item Value、Time(結束)////////////////////////////////////////////////////
			//////////////////////////解析history value json(開始)////////////////////////////////////////////////////
			//ArrayList detailIDList = new ArrayList();
			ArrayList detailValueList = new ArrayList();
			
			for(int i = 0; i < itemValueList.size(); i++){
				ArrayList tempDetailValueList = new ArrayList();	//必須再回圈內重新宣告，不然存進去地值會被刷掉
				
				HistoryValue historyValue = gson.fromJson((String) itemValueList.get(i), HistoryValue.class);
				List<Detail> tempList = historyValue.detail;
				for(int k = 0; k < tempList.size(); k++){
					tempDetailValueList.add(tempList.get(k).detailValue);	//取得解析後的detailValue
				}
				detailValueList.add(tempDetailValueList);	//放到陣列裡
			}
			///////////////////////////解析history value json(結束)////////////////////////////////////////////////////
			///////////////////////////透過item id取得detail name 跟 id(開始)////////////////////////////////////////////////////
			//取得item
			rs = conn.runSql("select detail from healthtrackingitem where itemID = '"+itemID+"' ");
			System.out.println("Listener runSql 成功");
			
			String detail = null;
			
			while (rs.next()) {
				detail = rs.getString("detail"); 	// 取得項目detail	
			}
			if (rs != null){ try {rs.close(); System.out.println("關閉ResultSet");} catch (SQLException ignore) {}}
			//取得detail
			ItemDetail detailClass = gson.fromJson(detail, ItemDetail.class);
			String detailID;
			ArrayList detailIDList = new ArrayList();
			ArrayList detailNameList = new ArrayList();
			
			for(int i = 0; i< detailClass.detailID.size(); i++){
				detailID = detailClass.detailID.get(i);
				rs = conn.runSql("select detailID, name from healthtrackingdetail where detailID = '"+detailID+"'");
				while (rs.next()) {
					detailIDList.add(rs.getString("detailID"));				// 取得細項detailID
					detailNameList.add(rs.getString("name")); 				// 取得細項name
		
				}
			}
			if (rs != null){ try {rs.close(); System.out.println("關閉ResultSet");} catch (SQLException ignore) {}}
			///////////////////////////透過detail id取得detail name 跟 id(結束)////////////////////////////////////////////////////
			
			ItemChartShow.put("itemTimeList", itemTimeList);
			ItemChartShow.put("detailIDList", detailIDList);
			ItemChartShow.put("detailValueList", detailValueList);
			ItemChartShow.put("detailNameList", detailNameList);

			System.out.println("Server ItemChartShow : " + ItemChartShow);

		} catch (SQLException e) {
			System.out.println("HealthTrackingServer ItemChartShow Exception :" + e.toString());
		}
		return ItemChartShow;
	}
*/
	//比較日期
	public static boolean betweenDate(String date1, String date2, String date3){
		//date1 開始日期//date2結束日期//比較日期
		boolean flag;
		if (date1.compareTo(date3) > 0) { 		//date1(開始日期)比較大//false
			flag = false;
		}
		else{
			flag = true;						//date1(開始日期)比較小//true
			//date2(結束日期)比較大//true//加上等於才會比到結束日期是對的
//date2(結束日期)比較小//false
			flag = date2.compareTo(date3) >= 0;
		}
		return flag;
	}
	
	// 取得紀錄:項目id與細項
	class HistoryValue {
		String itemID;
		List<Detail> detail;

		HistoryValue(String itemID, List<Detail> detail) {
			this.itemID = itemID;
			this.detail = detail;
		}
	}
	
	// 取得紀錄:細項id跟value
	public class Detail {
		String detailID;
		String detailValue;

		Detail(String detailID, String detailValue) {
			this.detailID = detailID;
			this.detailValue = detailValue;
		}
	}
	
	/*******************************************************************************************/

	// 歷史紀錄:取得紀錄//HistoryHealthTracking.html//已關資料庫
	public HashMap historyRecord(DataSource datasource, String patientID){
		HashMap historyRecord = new HashMap();
		Connection con = null;
		Gson gson = new Gson();
		
		try {
			con = datasource.getConnection();
		    Statement st = con.createStatement();
			ResultSet rs;

			String time, value, healthIDPlusTime, dateStart = null;
			boolean dateIsBetween;		//判斷時間是否介於開始日期及結束日期之間

			ArrayList itemTimeList = new ArrayList(); 			// item time record List
			ArrayList itemValueList = new ArrayList(); 			// item value record List
			ArrayList healthIDPlusTimeList = new ArrayList(); 	// 為了以後能擴充，給予div按鈕id(time+healthTrackingID)
			ArrayList itemNameList = new ArrayList();			//下拉選單用// item Name List
			ArrayList optionValueList = new ArrayList();		//下拉選單用//	存放 itemid

			//////////////////////////取得item Value、Time、healthIDPlusTime(開始)////////////////////////////////////////////////////
			rs = st.executeQuery("SELECT itemID, time, value, healthTrackingID FROM healthtracking NATURAL JOIN history WHERE patientID = '"
							+ patientID + "' ORDER BY time DESC");
			while (rs.next()) {
				time = rs.getString("time");
				value = rs.getString("value");
				time = time.substring(0, 19);
				
				time = time.replace(" ", "-");
				healthIDPlusTime = time + "_" +rs.getString("healthTrackingID");
				healthIDPlusTime = healthIDPlusTime.replace(" ", "_");
				healthIDPlusTime = healthIDPlusTime.replace(":", "-");
				
				time = replaceCharAt(time, 10,' ');
				itemTimeList.add(time);
				itemValueList.add(value);
				healthIDPlusTimeList.add(healthIDPlusTime);
			}
			rs.close();//關閉rs
			//////////////////////////取得item Value、Time、healthIDPlusTime(結束)////////////////////////////////////////////////////
			//////////////////////////解析history value json(開始)////////////////////////////////////////////////////
			ArrayList itemIDList = new ArrayList(); 			// itemID List//為了透過item id取得detail name 跟 id
			ArrayList detailIDList = new ArrayList();
			ArrayList detailValueList = new ArrayList();
			
			for(int i = 0; i < itemValueList.size(); i++){
				ArrayList tempDetailIDList = new ArrayList();		//必須再回圈內重新宣告，不然存進去地值會被刷掉
				ArrayList tempDetailValueList = new ArrayList();	//必須再回圈內重新宣告，不然存進去地值會被刷掉
				
				HistoryValue historyValue = gson.fromJson((String) itemValueList.get(i), HistoryValue.class);
				List<Detail> tempList = historyValue.detail;
				for(int k = 0; k < tempList.size(); k++){
					tempDetailIDList.add(tempList.get(k).detailID);			//取得解析後的detailID
					tempDetailValueList.add(tempList.get(k).detailValue);	//取得解析後的detailValue
				}
				itemIDList.add(historyValue.itemID);		//放到陣列裡
				detailIDList.add(tempDetailIDList);			//放到陣列裡
				detailValueList.add(tempDetailValueList);	//放到陣列裡
			}
			///////////////////////////解析history value json(結束)////////////////////////////////////////////////////
			///////////////////////////透過detail id取得detail name、 unit、upperLimit、lowerLimit(開始)////////////////////////////////////////////////////
			ArrayList detailNameList = new ArrayList();
			ArrayList detailUnitList = new ArrayList();
			ArrayList detailUpperLimitList = new ArrayList();
			ArrayList detailLowerLimitList = new ArrayList();
			ArrayList tempList = new ArrayList();		//暫存用
			
			for(int i=0; i<detailIDList.size(); i++){
				ArrayList tempDetailNameList = new ArrayList();		//必須再回圈內重新宣告，不然存進去地值會被刷掉
				ArrayList tempDetailUnitList = new ArrayList();		//必須再回圈內重新宣告，不然存進去地值會被刷掉
				ArrayList tempDetailUpperLimitList = new ArrayList();		//必須再回圈內重新宣告，不然存進去地值會被刷掉
				ArrayList tempDetailLowerLimitList = new ArrayList();		//必須再回圈內重新宣告，不然存進去地值會被刷掉

				tempList = (ArrayList) detailIDList.get(i);
				//System.out.println("tempList : " + tempList);
				for(int k=0; k<tempList.size(); k++){
					st = con.createStatement();
					rs = st.executeQuery("SELECT name, unit, upperLimit, lowerLimit  FROM healthtrackingdetail WHERE detailID = '"+ tempList.get(k) + "' ");
					while(rs.next()){	
						tempDetailNameList.add(rs.getString("name"));
						tempDetailUnitList.add(rs.getString("unit"));
						tempDetailUpperLimitList.add(rs.getString("upperLimit"));
						tempDetailLowerLimitList.add(rs.getString("lowerLimit"));
						//System.out.println("tempDetailIDList : " + tempDetailIDList);
					}
					rs.close();//關閉rs
				}
				detailNameList.add(tempDetailNameList);
				detailUnitList.add(tempDetailUnitList);
				detailUpperLimitList.add(tempDetailUpperLimitList);
				detailLowerLimitList.add(tempDetailLowerLimitList);
			}
			///////////////////////////透過detail id取得detail name、 unit、upperLimit、lowerLimit(結束)////////////////////////////////////////////////////
			///////////////////////////取得下拉選單值 itemID, name(開始)////////////////////////////////////////////////////
			st = con.createStatement();
			rs = st.executeQuery("SELECT itemID, name FROM healthtracking NATURAL JOIN healthtrackingitem WHERE patientID = '"
					+ patientID + "' ORDER BY (itemID+0) DESC");
			while (rs.next()) {
				itemNameList.add(rs.getString("name"));			//下拉選單用
				optionValueList.add(rs.getString("itemID"));	//下拉選單用
			}
			rs.close();//關閉rs
			///////////////////////////取得下拉選單值 itemID, nam(結束)////////////////////////////////////////////////////
			///////////////////////////取得min date(開始)////////////////////////////////////////////////////
			rs = st.executeQuery("SELECT min(time) FROM healthtracking NATURAL JOIN history WHERE patientID = '"+ patientID + "' ");
			while (rs.next()) {
				dateStart = rs.getString("min(time)");
			}
			rs.close();//關閉rs
			///////////////////////////取得min date(開始)////////////////////////////////////////////////////

		    st.close();//關閉st

			historyRecord.put("healthIDPlusTimeList", healthIDPlusTimeList);
			historyRecord.put("itemTimeList", itemTimeList);
			historyRecord.put("itemIDList", itemIDList);
			historyRecord.put("detailIDList", detailIDList);
			historyRecord.put("detailValueList", detailValueList);
			historyRecord.put("detailNameList", detailNameList);
			historyRecord.put("detailUnitList", detailUnitList);
			historyRecord.put("detailUpperLimitList", detailUpperLimitList);
			historyRecord.put("detailLowerLimitList", detailLowerLimitList);
			historyRecord.put("dateStart", dateStart);
			historyRecord.put("itemNameList", itemNameList);		//下拉選單用
			historyRecord.put("optionValueList", optionValueList);	//下拉選單用
		} catch (SQLException e) {
			System.out.println("HealthTrackingServer historyRecord Exception :" + e.toString());
			e.printStackTrace();
		} finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return historyRecord;
	}
	
	// 歷史紀錄:選擇日期區間及類別取得紀錄//HistoryHealthTracking.html//已關資料庫
	public HashMap historyRecordChangeList(DataSource datasource, String patientID, String itemID, String dateStart, String dateEnd){
		HashMap historyRecordSelectDate = new HashMap();
		Connection con = null;
		Gson gson = new Gson();
		
		try {
			con = datasource.getConnection();
		    Statement st = con.createStatement();
			ResultSet rs;

			String time, value, healthIDPlusTime;
			boolean dateIsBetween;		//判斷時間是否介於開始日期及結束日期之間

			ArrayList itemTimeList = new ArrayList(); 			// item time record List
			ArrayList itemValueList = new ArrayList(); 			// item value record List
			ArrayList healthIDPlusTimeList = new ArrayList(); 	// 為了以後能擴充，給予div按鈕id(time+healthTrackingID)

			//////////////////////////取得item Value、Time、healthIDPlusTime(開始)////////////////////////////////////////////////////
			String select;	//顯示所有項目或選擇的項目
			if(itemID.equals("all"))
				select = "";
			else
				select = "and itemID = '"+itemID+"' ";
			rs = st.executeQuery("SELECT itemID, time, value, healthTrackingID FROM healthtracking NATURAL JOIN history WHERE patientID = '"
					+ patientID + "' and time >= '"+dateStart+"' and time <= '"+dateEnd+"' "+select+" ORDER BY time DESC");
			while (rs.next()) {
				time = rs.getString("time");
				value = rs.getString("value");
				time = time.substring(0, 19);
				
				time = time.replace(" ", "-");
				healthIDPlusTime = time + "_" +rs.getString("healthTrackingID");
				healthIDPlusTime = healthIDPlusTime.replace(" ", "_");
				healthIDPlusTime = healthIDPlusTime.replace(":", "-");
				
				itemTimeList.add(time);
				itemValueList.add(value);
				healthIDPlusTimeList.add(healthIDPlusTime);				
				//判斷是否為選擇的日期
				/*if(isDate(time.substring(0, 10), dateSelect)){
					healthIDPlusTime = time + "_" +rs.getString("healthTrackingID");
					healthIDPlusTime = healthIDPlusTime.replace(" ", "_");
					healthIDPlusTime = healthIDPlusTime.replace(":", "-");
					
					itemTimeList.add(time);
					itemValueList.add(value);
					healthIDPlusTimeList.add(healthIDPlusTime);
				}*/
			}
			rs.close();//關閉rs
			//////////////////////////取得item Value、Time、healthIDPlusTime(結束)////////////////////////////////////////////////////
			//////////////////////////解析history value json(開始)////////////////////////////////////////////////////
			ArrayList itemIDList = new ArrayList(); 			// itemID List//為了透過item id取得detail name 跟 id
			ArrayList detailIDList = new ArrayList();
			ArrayList detailValueList = new ArrayList();
			
			for(int i = 0; i < itemValueList.size(); i++){
				ArrayList tempDetailIDList = new ArrayList();		//必須再回圈內重新宣告，不然存進去地值會被刷掉
				ArrayList tempDetailValueList = new ArrayList();	//必須再回圈內重新宣告，不然存進去地值會被刷掉
				
				HistoryValue historyValue = gson.fromJson((String) itemValueList.get(i), HistoryValue.class);
				List<Detail> tempList = historyValue.detail;
				for(int k = 0; k < tempList.size(); k++){
					tempDetailIDList.add(tempList.get(k).detailID);			//取得解析後的detailID
					tempDetailValueList.add(tempList.get(k).detailValue);	//取得解析後的detailValue
				}
				itemIDList.add(historyValue.itemID);		//放到陣列裡
				detailIDList.add(tempDetailIDList);			//放到陣列裡
				detailValueList.add(tempDetailValueList);	//放到陣列裡
			}
			///////////////////////////解析history value json(結束)////////////////////////////////////////////////////
			///////////////////////////透過detail id取得detail name、 unit、upperLimit、lowerLimit(開始)////////////////////////////////////////////////////
			ArrayList detailNameList = new ArrayList();
			ArrayList detailUnitList = new ArrayList();
			ArrayList detailUpperLimitList = new ArrayList();
			ArrayList detailLowerLimitList = new ArrayList();
			ArrayList tempList = new ArrayList();		//暫存用
			
			for(int i=0; i<detailIDList.size(); i++){
				ArrayList tempDetailNameList = new ArrayList();		//必須再回圈內重新宣告，不然存進去地值會被刷掉
				ArrayList tempDetailUnitList = new ArrayList();		//必須再回圈內重新宣告，不然存進去地值會被刷掉
				ArrayList tempDetailUpperLimitList = new ArrayList();		//必須再回圈內重新宣告，不然存進去地值會被刷掉
				ArrayList tempDetailLowerLimitList = new ArrayList();		//必須再回圈內重新宣告，不然存進去地值會被刷掉
			

				tempList = (ArrayList) detailIDList.get(i);
				//System.out.println("tempList : " + tempList);
				for(int k=0; k<tempList.size(); k++){
					st = con.createStatement();
					rs = st.executeQuery("SELECT name, unit, upperLimit, lowerLimit  FROM healthtrackingdetail WHERE detailID = '"+ tempList.get(k) + "' ");
					while(rs.next()){	
						tempDetailNameList.add(rs.getString("name"));
						tempDetailUnitList.add(rs.getString("unit"));
						tempDetailUpperLimitList.add(rs.getString("upperLimit"));
						tempDetailLowerLimitList.add(rs.getString("lowerLimit"));
						//System.out.println("tempDetailIDList : " + tempDetailIDList);
					}
					rs.close();//關閉rs
				}
				detailNameList.add(tempDetailNameList);
				detailUnitList.add(tempDetailUnitList);
				detailUpperLimitList.add(tempDetailUpperLimitList);
				detailLowerLimitList.add(tempDetailLowerLimitList);
			}
			///////////////////////////透過detail id取得detail name、 unit、upperLimit、lowerLimit(結束)////////////////////////////////////////////////////

			st.close();//關閉st

			historyRecordSelectDate.put("healthIDPlusTimeList", healthIDPlusTimeList);
			historyRecordSelectDate.put("itemTimeList", itemTimeList);
			historyRecordSelectDate.put("itemIDList", itemIDList);
			historyRecordSelectDate.put("detailIDList", detailIDList);
			historyRecordSelectDate.put("detailValueList", detailValueList);
			historyRecordSelectDate.put("detailNameList", detailNameList);
			historyRecordSelectDate.put("detailUnitList", detailUnitList);
			historyRecordSelectDate.put("detailUpperLimitList", detailUpperLimitList);
			historyRecordSelectDate.put("detailLowerLimitList", detailLowerLimitList);
		} catch (SQLException e) {
			System.out.println("HealthTrackingServer historyRecordSelectDate Exception :" + e.toString());
			e.printStackTrace();
		} finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return historyRecordSelectDate;
	}
	
	// 日期是否同天//改掉了用不到了
	private static boolean isDate(String Date1, String Date2) {
		// 一樣
		return Date1.compareTo(Date2) == 0;
	}

	
	/*******************************************************************************************/
	//取代某字串
	public static String replaceCharAt(String s, int pos, char c)
	{
	    return s.substring(0, pos) + c + s.substring(pos + 1);
	}
	
	//編輯紀錄:編輯紀錄細項和內容//EditHealthTracking.html
	public HashMap editHealthDetail(DataSource datasource, String time, String healthTrackingID) {
		HashMap editHealthDetail = new HashMap();	//回傳結果
		Connection con = null;
		Gson gson = new Gson();
		
		//替換成正確的格式
		time = replaceCharAt(time, 10,' ');
		time = replaceCharAt(time, 13,':');
		time = replaceCharAt(time, 16,':');
		String itemID = null, value = null, selfDescriptionValue = null;

		try {
			con = datasource.getConnection();
		    Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select itemID, value, selfDescriptionValue from healthtracking NATURAL JOIN history where healthTrackingID='"+healthTrackingID+"' and time='"+time+"' ");
			
			while (rs.next()) {
				itemID = rs.getString("itemID");
				value = rs.getString("value");
				selfDescriptionValue = rs.getString("selfDescriptionValue");
			}
			rs.close();//關閉rs
			
			editHealthDetail = newHealthDetail(datasource, itemID);	//取得細項
			
			//////////////////////////解析history value json(開始)////////////////////////////////////////////////////
			ArrayList detailValueList = new ArrayList();
			
			HistoryValue historyValue = gson.fromJson(value, HistoryValue.class);
			List<Detail> tempList = historyValue.detail;
			for(int k = 0; k < tempList.size(); k++){
				detailValueList.add(tempList.get(k).detailValue);	//取得解析後的detailValue
			}
			///////////////////////////解析history value json(結束)////////////////////////////////////////////////////
		    st.close();//關閉st
		    
		    editHealthDetail.put("selfDescriptionValue", selfDescriptionValue);
		    editHealthDetail.put("detailValueList", detailValueList);
		    editHealthDetail.put("itemID", itemID);

		} catch (SQLException e) {
			System.out.println("HealthTrackingServer editHealthDetail Exception :" + e.toString());
			e.printStackTrace();
		} finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return editHealthDetail;	
	}
	
	//編輯紀錄:更新紀錄//EditHealthTracking.html
	public HashMap updateHealth(DataSource datasource, String patientID, String itemNumber, String[] detailIdArray,
			String[] detailValueArray, String selfDescriptionValue, String time, String healthTrackingID) {
		Gson gson = new Gson();
		HashMap updateHealth = new HashMap();	//回傳結果
		Connection con = null;
		String result = "請重新嘗試";
				
		//取得要存放的value值
		String value = "{\"itemID\": \""+itemNumber+"\", "+
				"\"detail\": [";
		String temp;
		for(int i=0; i<detailIdArray.length; i++){
			temp = "{\"detailID\": \""+detailIdArray[i]+"\", \"detailValue\": "+detailValueArray[i]+"}, ";
			value += temp;
		}
		value = value.substring(0,value.length()-2);	//去掉最後多餘的一個空格和一個，
		value += "]}";
		
		try {
			con = datasource.getConnection();
		    Statement st = con.createStatement();
			//////////////////////////新增history紀錄(開始)////////////////////////////////////////////////////
		    //替換成正確的格式
		    time = replaceCharAt(time, 10,' ');
			time = replaceCharAt(time, 13,':');
			time = replaceCharAt(time, 16,':');
			
		    st = con.createStatement();
			int insert = st.executeUpdate("update history SET value = '"+ value +"', selfDescriptionValue = '"+ selfDescriptionValue +"' where healthTrackingID='"+healthTrackingID+"' and time='"+time+"' ");

			//////////////////////////新增history紀錄(開始)////////////////////////////////////////////////////
			if(insert>0){
				result = "修改成功";
			}
			else
				result = "修改失敗";
			
		    st.close();//關閉st

		    updateHealth.put("result", result);
		} catch (SQLException e) {
			System.out.println("HealthTrackingServer updateHealth Exception :" + e.toString());
			e.printStackTrace();
		} finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return updateHealth;
	}
	/*******************************************************************************************/

	
	public static void main(String[] args){
		/*
		String temp = "item123";
		String test = (String) temp.subSequence(4, temp.length());
		System.out.println(test);
		
		String temp2 = "{detailID:[1,2,3]}";
		Gson gson = new Gson();
		ItemDetail temp3 = gson.fromJson(temp2, ItemDetail.class);
		System.out.println(temp3.detailID.size());
		System.out.println(temp3.detailID.get(0));
		
		String temp4="{item:item1,detailIdArray:[\"detail1\",\"detail2\",\"detail4\"],detailValueArray:[\"2\",\"3\",\"6\"]}";
		historyValue temp5 = gson.fromJson(temp4, historyValue.class);
		System.out.println(temp5.item);
		System.out.println(temp5.detailIdArray.size());
		System.out.println(temp5.detailIdArray.get(0));
		System.out.println(temp5.detailValueArray.size());
		System.out.println(temp5.detailValueArray.get(0));
		
		Calendar cal = Calendar.getInstance();//使用預設時區和語言環境獲得一個日曆。  
		//cal.add(Calendar.DAY_OF_MONTH, -1);//取當前日期的前一天.  

		int cycle=9;
		cal.add(Calendar.DAY_OF_MONTH, +cycle);//取當前日期的後一天.  
 
		//通過格式化輸出日期  
		java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");  
		System.out.println("Today is:"+format.format(Calendar.getInstance().getTime()));  
		System.out.println("Today+cycle is:"+format.format(cal.getTime()));
		*/
		/*
		String time="2017-04-26 04:52";
		String healthTrackingID ="3";
		String timePlusID = time + "_" +healthTrackingID;
		System.out.println("timePlusID : " + timePlusID);
		timePlusID = timePlusID.replace(" ", "_");
		timePlusID = timePlusID.replace(":", "-");
		System.out.println("timePlusID : " + timePlusID);
		*/
		
		/*String temp = getNowDate();
		System.out.println("today : " + temp);*/
	}



}
