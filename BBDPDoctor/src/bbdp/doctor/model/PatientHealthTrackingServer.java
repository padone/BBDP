package bbdp.doctor.model;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import bbdp.doctor.model.HealthTrackingServer.ItemDetail;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;


public class PatientHealthTrackingServer {

	//一進來取得所有項目//PatientHealthTracking.html//已關資料庫
	public HashMap allItemDefault(DataSource datasource, String doctorID, String patientID) {
		Gson gson = new Gson();
		HashMap allItem = new HashMap();
		Connection con = null;

		try {
			con = datasource.getConnection();
		    Statement st = con.createStatement();
			ResultSet rs;

			ArrayList itemIDList = new ArrayList(); 				// itemID List
			ArrayList itemNameList = new ArrayList(); 				// item name List
			ArrayList itemRecordList = new ArrayList(); 			// item record number List
			int record = 0;
			ArrayList itemLastTimeList = new ArrayList(); 			// item last time record List
			ArrayList itemLastValueList = new ArrayList(); 			// item last value record List
			//////////////////////////取得item id、name、lastValue、lastTime(開始)////////////////////////////////////////////////////
			st = con.createStatement();
			rs = st.executeQuery("SELECT itemID, time, value, name FROM healthtracking NATURAL JOIN healthtrackingitem NATURAL JOIN history WHERE patientID = '"
							+ patientID + "' and doctorID = '" + doctorID + "'ORDER BY healthTrackingID ASC");
			
			while (rs.next()) {
				//近一個月的history紀錄
				if (isRecent(rs.getString("time").substring(0, 10))) {
					if (itemIDList.size() == 0) { // 都還沒有項目
						itemRecordList.add(1);
						itemIDList.add(rs.getString("itemID"));
						itemLastTimeList.add(rs.getString("time").substring(0, 16));
						itemLastValueList.add(rs.getString("value"));
						itemNameList.add(rs.getString("name"));
					} else { // 有項目了
						for (int i = 0; i < itemIDList.size(); i++) {
							if (itemIDList.get(i).equals(rs.getString("itemID"))) { // 判斷是不是已有這個項目在陣列裡//是的話項目number加加
								record = (int) itemRecordList.get(i);
								record += 1;
								itemRecordList.set(i, record);	//修改紀錄的資料總數+1
								
								if(isDateBigger(rs.getString("time").substring(0, 16), (String)itemLastTimeList.get(i))){	//如果新的時間比較晚，就紀錄較晚的時間
									itemLastTimeList.set(i, rs.getString("time").substring(0, 16));
									itemLastValueList.set(i, rs.getString("value"));
								}
								break;
							}
							if (i == itemIDList.size() - 1) { // 判斷是不是已有這個項目在陣列裡//不是的話新增項目進陣列
								itemRecordList.add(1);
								itemIDList.add(rs.getString("itemID"));
								itemLastTimeList.add(rs.getString("time").substring(0, 16));
								itemLastValueList.add(rs.getString("value"));
								itemNameList.add(rs.getString("name"));
								break;
							}
						}
					}
				}
			}
			rs.close();//關閉rs
			//////////////////////////取得item id、name、lastValue、lastTime(結束)////////////////////////////////////////////////////
			///////////////////////////解析history last value json(開始)////////////////////////////////////////////////////
			ArrayList detailIDList = new ArrayList();
			ArrayList detailValueList = new ArrayList();
			
			for(int i = 0; i < itemLastValueList.size(); i++){
				ArrayList tempDetailIDList = new ArrayList();		//必須再回圈內重新宣告，不然存進去地值會被刷掉
				ArrayList tempDetailValueList = new ArrayList();	//必須再回圈內重新宣告，不然存進去地值會被刷掉
				
				HistoryValue historyValue = gson.fromJson((String) itemLastValueList.get(i), HistoryValue.class);
				List<Detail> tempList = historyValue.detail;
				for(int k = 0; k < tempList.size(); k++){
					tempDetailIDList.add(tempList.get(k).detailID);			//取得解析後的detailID
					tempDetailValueList.add(tempList.get(k).detailValue);	//取得解析後的detailValue
				}
				detailIDList.add(tempDetailIDList);			//放到陣列裡
				detailValueList.add(tempDetailValueList);	//放到陣列裡
				//tempDetailIDList.clear();		//清空暫存
				//tempDetailValueList.clear();	//清空暫存
			}
			///////////////////////////解析history last value json(結束)////////////////////////////////////////////////////
			///////////////////////////透過detail id取得detail name 跟 unit(開始)////////////////////////////////////////////////////
			String detailID;
			ArrayList tempList1 = new ArrayList();
			ArrayList detailNameList = new ArrayList();
			ArrayList detailUnitList = new ArrayList();
			
			for(int i = 0; i<detailIDList.size(); i++){
				tempList1 = (ArrayList) detailIDList.get(i);// 取得dtail id
				ArrayList tempList2 = new ArrayList();		// detail name的暫存區//必須再回圈內重新宣告，不然存進去地值會被刷掉
				ArrayList tempList3 = new ArrayList();		// detail unit的暫存區//必須再回圈內重新宣告，不然存進去地值會被刷掉
				
				for(int k = 0; k < tempList1.size(); k++){
					detailID = tempList1.get(k).toString();		
					st = con.createStatement();
					rs = st.executeQuery("SELECT detailID, name, unit FROM healthtrackingdetail WHERE detailID = '" + detailID + "' ");
					while (rs.next()) {
						tempList2.add(rs.getString("name"));
						tempList3.add(rs.getString("unit"));
					}
					rs.close();//關閉rs
				}
				detailNameList.add(tempList2);
				detailUnitList.add(tempList3);
			}
			///////////////////////////透過detail id取得detail name 跟 unit(結束)////////////////////////////////////////////////////
			///////////////////////////如果病患沒有紀錄的話 放空值進去(開始)////////////////////////////////////////////////////
			boolean judge;
			st = con.createStatement();
			rs = st.executeQuery("SELECT itemID, name FROM healthtracking NATURAL JOIN healthtrackingitem WHERE patientID = '"
					+ patientID + "' and doctorID = '" + doctorID + "'ORDER BY healthTrackingID ASC");
			while (rs.next()) {
				judge = false;
				for(int i = 0; i < itemIDList.size(); i++){	//從有紀錄的項目陣列尋找
					if(rs.getString("itemID").equals(itemIDList.get(i))){	//醫生有給病患此項目，病患最近一個月有新增過紀錄
						judge = true;
						break;
					}
				}
				if(judge == false){//醫生有給病患此項目，但最近一個月病患並沒有新增任何紀錄
					itemIDList.add(rs.getString("itemID"));
					itemNameList.add(rs.getString("name"));
					itemRecordList.add(0);
					itemLastTimeList.add("...");
					detailValueList.add("");
					detailIDList.add("");
					detailNameList.add("");
					detailUnitList.add("");
				}
			}
			rs.close();//關閉rs
			///////////////////////////如果病患沒有紀錄的話 放空值進去(結束)////////////////////////////////////////////////////
			
		    st.close();//關閉st
			
			allItem.put("itemIDList", itemIDList);
			allItem.put("itemNameList", itemNameList);
			allItem.put("itemRecordList", itemRecordList);
			allItem.put("itemLastTimeList", itemLastTimeList);
			allItem.put("detailValueList", detailValueList);
			allItem.put("detailIDList", detailIDList);
			allItem.put("detailNameList", detailNameList);
			allItem.put("detailUnitList", detailUnitList);
		} catch (SQLException e) {
			System.out.println("PatientHealthTrackingServer allItemDefault Exception :" + e.toString());
			e.printStackTrace();
		} finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return allItem;
	}

	// 判斷日期是否是最近一個月
	private static boolean isRecent(String inputDate) {
		int year = Integer.valueOf(inputDate.substring(0, 4));
		int month = Integer.valueOf(inputDate.substring(5, 7));
		int date = Integer.valueOf(inputDate.substring(8));
		LocalDate birthday = LocalDate.of(year, month, date);
		LocalDate today = LocalDate.now();
		Period period = Period.between(birthday, today);
		if (period.getMonths() == 0)
			return true;
		return false;
	}

	// 比較日期大小
	private static boolean isDateBigger(String Date1, String Date2) {
		if (Date1.compareTo(Date2) > 0) { // Date1比較大
			return true;
		}
		return false; // Date1比較小
	}

	// 取得紀錄:細項
	class ItemDetail {
		List<String> detailID;

		ItemDetail(List<String> detailID) {
			this.detailID = detailID;
		}
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
	
	//取得分類下拉選單的值//NewPatientHealthTracking.html//已關資料庫
	public HashMap allTypeDefault(DataSource datasource, String doctorID, String patientID){
		HashMap allType = new HashMap();
		Connection con = null;
		try {
			con = datasource.getConnection();
		    Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select distinct type, doctorID from healthtrackingitem where doctorID = '"+doctorID+"' ORDER BY itemID DESC");

			ArrayList typeList = new ArrayList(); // item type List

			while (rs.next()) {
				typeList.add(rs.getString("type")); // 取得使用者的項目type
			}
			rs.close();//關閉rs
			
			// 取得所有項目的name
			st = con.createStatement();
			rs = st.executeQuery("select itemID, name from healthtrackingitem where doctorID = '"+doctorID+"' ORDER BY itemID DESC");
			
			ArrayList itemIDList = new ArrayList(); // item itemID List
			ArrayList nameList = new ArrayList(); // item name List
			
			while (rs.next()) {
					nameList.add(rs.getString("name")); // 取得使用者的項目name
					itemIDList.add(rs.getString("itemID"));	// 取得使用者的項目itemID	
			}
			rs.close();//關閉rs
			
		    st.close();//關閉st

			allType.put("typeList", typeList);
			allType.put("itemIDList", itemIDList);
			allType.put("nameList", nameList);
		} catch (SQLException e) {
			System.out.println("PatientHealthTrackingServer allTypeDefault Exception :" + e.toString());
			e.printStackTrace();
		} finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return allType;
		
	}
	
	//選什麼分類，得到該分類的項目//NewPatientHealthTracking.html//已關資料庫
	public HashMap typeSelectItem(DataSource datasource, String doctorID, String patientID, String select){
		HashMap typeSelectItem = new HashMap();
		Connection con = null;
		try {
			con = datasource.getConnection();
		    Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select name, itemID from healthtrackingitem where doctorID='" + doctorID
					+ "' and type='" + select + "' ORDER BY itemID DESC");

			ArrayList nameList = new ArrayList(); // item name List
			ArrayList itemIDList = new ArrayList(); // item itemID List

			while (rs.next()) {
				nameList.add(rs.getString("name")); // 取得項目名稱
				itemIDList.add(rs.getString("itemID")); // 取得項目id
			}
			rs.close();//關閉rs

		    st.close();//關閉st

			typeSelectItem.put("itemIDList", itemIDList);
			typeSelectItem.put("nameList", nameList);
		} catch (SQLException e) {
			System.out.println("PatientHealthTrackingServer typeSelectItem Exception :" + e.toString());
			e.printStackTrace();
		} finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return typeSelectItem;
	}
	
	//按下後新增項目給病患//NewPatientHealthTracking.html//已關資料庫
	synchronized public HashMap addItemToPatient(DataSource datasource, String doctorID, String patientID, String itemSelect){
		HashMap addItemToPatient = new HashMap();
		Connection con = null;
		String result = "";
		try {
			con = datasource.getConnection();
		    Statement st = con.createStatement();
			itemSelect = (String) itemSelect.subSequence(4, itemSelect.length());	//取得選擇的項目id
			//////////////////////////////確認並沒有新增給病患過(開始)///////////////////////////////////////
			ResultSet rs = st.executeQuery("select patientID, itemID, hideHealthtracking from healthtracking Where patientID='"+patientID+"' and itemID='"+itemSelect+"' ORDER BY healthTrackingID DESC");
			
			boolean isUsed = false;	//判斷是否新增過
			LocalDate today = LocalDate.now();
			String hideHealthtracking = null;
			
			while (rs.next()) {
				hideHealthtracking = rs.getString("hideHealthtracking");
				if(hideHealthtracking.equals("0")){
					isUsed = true;
					result = "已新增過此模板給病患";
				}
			}
			rs.close();//關閉rs
			//////////////////////////////確認並沒有新增給病患過(結束)///////////////////////////////////////
			//////////////////////////////確認並未新增給病患過後開始新增，並去item那裏把used改成1，表示已經使用過(開始)///////////////////////////////////////
			if(!isUsed){	
				//新增給病患
				st = con.createStatement();
				int insert = 0;
				if(hideHealthtracking != null){	//曾新增過
					insert = st.executeUpdate("update healthtracking SET hideHealthtracking = 0 where patientID='"+patientID+"' and itemID='"+itemSelect+"' ");
				}
				else{	//第一次新增
					insert = st.executeUpdate("insert into healthtracking(healthTrackingID, patientID, itemID, next, hideHealthtracking)"
							+ "select ifNULL(max(healthTrackingID+0),0)+1, '"+patientID+"', '"+itemSelect+"', '"+today+"', '0' From healthtracking");
				}
				
				if(insert>0)
					result = "新增成功";
				else
					result = "新增不成功";
				
				//把項目used改為1
				int update = st.executeUpdate("update healthtrackingitem SET used = 1 WHERE itemID = '"+itemSelect+"' ");
				
			    //取得項目名稱
			    String itemName = null;
				st = con.createStatement();
				rs = st.executeQuery("select name from healthtrackingitem Where itemID='"+itemSelect+"' ");
				while (rs.next()) {
					itemName = rs.getString("name");
				}
				rs.close();//關閉rs
				
				//推播給病患
				bbdp.push.fcm.PushToFCM.sendNotification("BBDP", "新增健康狀況追蹤項目", patientID, "SelectItemHealthTracking.html");
				
			    st.close();//關閉st
			}
			//////////////////////////////確認並未新增給病患過後開始新增，並去item那裏把used改成1，表示已經使用過(結束)///////////////////////////////////////
		} catch (SQLException e) {
			System.out.println("PatientHealthTrackingServer addItemToPatient Exception :" + e.toString());
			e.printStackTrace();
		} finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		addItemToPatient.put("result", result);
		return addItemToPatient;
	}
	
	//取得現在的時間
	public String getNowDate(){
		//取得現在時間
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = simpleDateFormat.format(timestamp);
		return currentTime;
	}
	
	/*******************************************************************************************/

	//取得該項目一些基本資料//EditPatientHealthTracking.html//已關資料庫
	public HashMap itemAllDetail(DataSource datasource, String doctorID, String patientID, String itemID){
		Gson gson = new Gson();
		Connection con = null;
		HashMap itemAllDetail = new HashMap();
		try {
			con = datasource.getConnection();
		    Statement st = con.createStatement();
			ResultSet rs;

			String name = null, chart = null, selfDescription = null, dateStart = getNowDate().substring(0, 10);

			ArrayList itemTimeList = new ArrayList(); 			// item time record List
			ArrayList itemValueList = new ArrayList(); 			// item value record List
			ArrayList selfDescriptionValueList = new ArrayList(); 	// selfDescriptionValue List
			
			//////////////////////////取得item name、chart、 selfDescription(開始)////////////////////////////////////////////////////
			rs = st.executeQuery("SELECT name, chart, selfDescription FROM healthtrackingitem WHERE doctorID = '" + doctorID + "' and itemID = '"+itemID+"' ");
			while (rs.next()) {
				name = rs.getString("name");
				chart = rs.getString("chart"); 						// 取得項目chart	
				selfDescription = rs.getString("selfDescription"); 	// 取得項目selfDescription	
			}
			rs.close();//關閉rs
			//////////////////////////取得item name、chart、 selfDescription(結束)////////////////////////////////////////////////////
			//////////////////////////取得min(date)(開始)////////////////////////////////////////////////////
			st = con.createStatement();
			rs = st.executeQuery("SELECT min(time) FROM healthtracking NATURAL JOIN healthtrackingitem NATURAL JOIN history WHERE patientID = '"
							+ patientID + "' and doctorID = '" + doctorID + "' and itemID = '"+itemID+"' ORDER BY time ASC");
			while (rs.next()) {
				if(rs.getString("min(time)") != null)
					dateStart = rs.getString("min(time)").substring(0, 10);
			}
			rs.close();//關閉rs
			//////////////////////////取得min(date)(結束)////////////////////////////////////////////////////
			//////////////////////////取得item Value、Time、selfDescriptionValue(開始)////////////////////////////////////////////////////
			st = con.createStatement();
			rs = st.executeQuery("SELECT time, value, selfDescriptionValue FROM healthtracking NATURAL JOIN healthtrackingitem NATURAL JOIN history WHERE patientID = '"
							+ patientID + "' and doctorID = '" + doctorID + "' and itemID = '"+itemID+"' ORDER BY time ASC");
			while (rs.next()) {
				itemTimeList.add(rs.getString("time").substring(0, 16));
				itemValueList.add(rs.getString("value"));
				selfDescriptionValueList.add(rs.getString("selfDescriptionValue"));
			}
			rs.close();//關閉rs
			//////////////////////////取得item Value、Time、selfDescriptionValue(結束)////////////////////////////////////////////////////
			//////////////////////////解析history value json(開始)////////////////////////////////////////////////////
			//ArrayList detailIDList = new ArrayList();
			ArrayList detailValueList = new ArrayList();
			
			for(int i = 0; i < itemValueList.size(); i++){
				//ArrayList tempDetailIDList = new ArrayList();		//必須再回圈內重新宣告，不然存進去地值會被刷掉
				ArrayList tempDetailValueList = new ArrayList();	//必須再回圈內重新宣告，不然存進去地值會被刷掉
				
				HistoryValue historyValue = gson.fromJson((String) itemValueList.get(i), HistoryValue.class);
				List<Detail> tempList = historyValue.detail;
				for(int k = 0; k < tempList.size(); k++){
					//tempDetailIDList.add(tempList.get(k).detailID);			//取得解析後的detailID
					tempDetailValueList.add(tempList.get(k).detailValue);	//取得解析後的detailValue
				}
				//detailIDList.add(tempDetailIDList);			//放到陣列裡
				detailValueList.add(tempDetailValueList);	//放到陣列裡
			}
			///////////////////////////解析history value json(結束)////////////////////////////////////////////////////
			///////////////////////////透過item id取得detail name 跟 id(開始)////////////////////////////////////////////////////
			//取得item
			st = con.createStatement();
			rs = st.executeQuery("select detail from healthtrackingitem where itemID = '"+itemID+"' and doctorID = '" + doctorID + "' ");
			
			String detail = null;
			
			while (rs.next()) {
				detail = rs.getString("detail"); 	// 取得項目detail	
			}
			rs.close();//關閉rs
			//取得detail
			ItemDetail detailClass = gson.fromJson(detail, ItemDetail.class);
			String detailID;
			ArrayList detailIDList = new ArrayList();
			ArrayList detailNameList = new ArrayList();
			
			for(int i = 0; i< detailClass.detailID.size(); i++){
				detailID = detailClass.detailID.get(i);
				st = con.createStatement();
				rs = st.executeQuery("select detailID, name from healthtrackingdetail where detailID = '"+detailID+"'");
				while (rs.next()) {
					detailIDList.add(rs.getString("detailID"));				// 取得細項detailID
					detailNameList.add(rs.getString("name")); 				// 取得細項name
				}
				rs.close();//關閉rs
			}
			///////////////////////////透過detail id取得detail name 跟 id(結束)////////////////////////////////////////////////////
			
		    st.close();//關閉st

			itemAllDetail.put("itemName", name);
			itemAllDetail.put("chart", chart);
			itemAllDetail.put("dateStart", dateStart);
			itemAllDetail.put("itemTimeList", itemTimeList);
			//itemAllDetail.put("itemValueList", itemValueList);
			itemAllDetail.put("detailIDList", detailIDList);
			itemAllDetail.put("detailValueList", detailValueList);
			itemAllDetail.put("detailNameList", detailNameList);
			itemAllDetail.put("selfDescription", selfDescription);
			itemAllDetail.put("selfDescriptionValueList", selfDescriptionValueList);
		} catch (SQLException e) {
			System.out.println("PatientHealthTrackingServer itemAllDetail Exception :" + e.toString());
			e.printStackTrace();
		} finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}	
		return itemAllDetail;
	}
	
	//改變日期//EditPatientHealthTracking.html//已關資料庫
	public HashMap changeChart(DataSource datasource, String doctorID, String patientID, String itemID, String dateStart, String dateEnd){
		HashMap changeChart = new HashMap();
		Connection con = null;
		Gson gson = new Gson();
		
		try {
			con = datasource.getConnection();
		    Statement st = con.createStatement();
			ResultSet rs;

			String selfDescription = null;
			boolean dateIsBetween;		//判斷時間是否介於開始日期及結束日期之間

			ArrayList itemTimeList = new ArrayList(); 				// item time record List
			ArrayList itemValueList = new ArrayList(); 				// item value record List
			ArrayList selfDescriptionValueList = new ArrayList(); 	// selfDescriptionValue List
			//////////////////////////取得item selfDescription(開始)////////////////////////////////////////////////////
			rs = st.executeQuery("SELECT selfDescription FROM healthtrackingitem WHERE doctorID = '" + doctorID + "' and itemID = '"+itemID+"' ");
			while (rs.next()) {
				selfDescription = rs.getString("selfDescription"); 	// 取得項目selfDescription	
			}
			rs.close();//關閉rs
			//////////////////////////取得item selfDescription(結束)////////////////////////////////////////////////////
			//////////////////////////取得item Value、Time、selfDescriptionValue(開始)////////////////////////////////////////////////////
			rs = st.executeQuery("SELECT time, value, selfDescriptionValue FROM healthtracking NATURAL JOIN healthtrackingitem NATURAL JOIN history WHERE patientID = '"
							+ patientID + "' and doctorID = '" + doctorID + "' and itemID = '"+itemID+"' ORDER BY time ASC");
			while (rs.next()) {	
				if(betweenDate(dateStart, dateEnd, rs.getString("time").substring(0, 10))){			//如果介於開始與結束日期之間
					itemTimeList.add(rs.getString("time").substring(0, 16));
					itemValueList.add(rs.getString("value"));
					selfDescriptionValueList.add(rs.getString("selfDescriptionValue")); 	
				}
			}
			rs.close();//關閉rs
			//////////////////////////取得item Value、Time、selfDescriptionValue(結束)////////////////////////////////////////////////////
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
			st = con.createStatement();
			rs = st.executeQuery("select detail from healthtrackingitem where itemID = '"+itemID+"' and doctorID = '" + doctorID + "' ");
			
			String detail = null;
			
			while (rs.next()) {
				detail = rs.getString("detail"); 	// 取得項目detail	
			}
			rs.close();//關閉rs
			//取得detail
			ItemDetail detailClass = gson.fromJson(detail, ItemDetail.class);
			String detailID;
			ArrayList detailIDList = new ArrayList();
			ArrayList detailNameList = new ArrayList();
			
			for(int i = 0; i< detailClass.detailID.size(); i++){
				detailID = detailClass.detailID.get(i);
				st = con.createStatement();
				rs = st.executeQuery("select detailID, name from healthtrackingdetail where detailID = '"+detailID+"'");
				while (rs.next()) {
					detailIDList.add(rs.getString("detailID"));				// 取得細項detailID
					detailNameList.add(rs.getString("name")); 				// 取得細項name
				}
				rs.close();//關閉rs
			}
			///////////////////////////透過detail id取得detail name 跟 id(結束)////////////////////////////////////////////////////
			
		    st.close();//關閉st
			
			changeChart.put("itemTimeList", itemTimeList);
			changeChart.put("detailIDList", detailIDList);
			changeChart.put("detailValueList", detailValueList);
			changeChart.put("detailNameList", detailNameList);
			changeChart.put("selfDescription", selfDescription);
			changeChart.put("selfDescriptionValueList", selfDescriptionValueList);
			
		} catch (SQLException e) {
			System.out.println("PatientHealthTrackingServer changeChart Exception :" + e.toString());
			e.printStackTrace();
		} finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return changeChart;
	}
	
	//比較日期
	public static boolean betweenDate(String date1, String date2, String date3){
		//date1 開始日期//date2結束日期//比較日期
		boolean flag;
		if (date1.compareTo(date3) > 0) { 		//date1(開始日期)比較大//false
			flag = false;
		}
		else{
			flag = true;						//date1(開始日期)比較小//true
			if(date2.compareTo(date3) >=0){		//date2(結束日期)比較大//true//加上等於才會比到結束日期是對的
				flag=true;
			}
			else{
				flag = false;					//date2(結束日期)比較小//false
			}
		}
		return flag;
	}
	
	//刪除該追蹤項目//EditPatientHealthTracking.html//已關資料庫
	public HashMap deleteHealthTracking(DataSource datasource, String doctorID, String patientID, String itemID){
		HashMap deleteHealthTracking = new HashMap();
		Connection con = null;
		String result = "請重新嘗試";
		try {
		    con = datasource.getConnection();
		    Statement st = con.createStatement();		    

			st = con.createStatement();
			int hide = st.executeUpdate("Update healthtracking"+
					" SET hideHealthtracking='1' "+
					" WHERE itemID = '"+itemID+"' and patientID = '"+patientID+"' ");

			if(hide > 0){
				result="健康追蹤項目刪除成功";
			}
			/////////////////////////// 判斷history裡面病患是否有填過健康追蹤項目 開始 ///////////////////////////
			/*ResultSet rs = st.executeQuery("SELECT healthTrackingID, time, value FROM healthtracking NATURAL JOIN healthtrackingitem NATURAL JOIN history WHERE patientID = '"
					+ patientID + "' and doctorID = '" + doctorID + "' and itemID = '"+itemID+"' ORDER BY time ASC");
			
			boolean patientUsed = false;	//病患尚未紀錄過健康追蹤項目//false
			while (rs.next()) {				
				patientUsed = true;			//病患已紀錄過健康追蹤項目//true
				break;
			}
			rs.close();//關閉rs*/
			/////////////////////////// 判斷history裡面病患是否有填過健康追蹤項目 結束 ///////////////////////////
			/////////////////////////// 刪除HealthTracking 開始 ///////////////////////////
			/*if(patientUsed){
				result = "病患已記錄過，無法刪除該追蹤項目";
			}
			else{
				//刪除健康追蹤項目
				String deleteHealthTrackingSQL = "Delete healthtracking"+
						" WHERE itemID = '"+itemID+"' and patientID = '"+patientID+"' "; 
				st = con.createStatement();
				int delete = st.executeUpdate(deleteHealthTrackingSQL);

				if(delete > 0){
					result="健康追蹤項目刪除成功";
				}
				
				//此健康追蹤項目醫生是否有發給其他病患
				boolean otherPatientUsed = false;	//此健康追蹤項目醫生沒有發給其他病患//false
				
				st = con.createStatement();
				rs = st.executeQuery("SELECT * FROM healthtracking NATURAL JOIN healthtrackingitem WHERE doctorID = '" + doctorID + "' and itemID = '"+itemID+"' ");
				while (rs.next()) {				
					otherPatientUsed = true;		//此健康追蹤項目醫生有發給其他病患//true
					break;
				}
				rs.close();//關閉rs
				
				//此健康追蹤項目醫生沒有發給其他病患//更改項目used變為0，也就是未使用過
				if(!otherPatientUsed){
					st = con.createStatement();
					int update = st.executeUpdate("update healthtrackingitem SET used = 0 WHERE itemID = '"+itemID+"' ");
				}
			}*/
			/////////////////////////// 刪除HealthTracking 結束 ///////////////////////////
		    
			st.close();//關閉st
		    
			deleteHealthTracking.put("result", result);
		} catch (SQLException e) {
			System.out.println("PatientHealthTrackingServer deleteHealthTracking Exception :" + e.toString());
			e.printStackTrace();
		} finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return deleteHealthTracking;
	}
	
	/*******************************************************************************************/

	//檢查itemID//EditPatientInstruction.html
	public boolean checkItemID(DataSource datasource, String doctorID, String itemID, String patientID) {
		Connection con = null;
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select * from healthtracking NATURAL JOIN healthtrackingitem  where doctorID='"+doctorID+"' and itemID='"+itemID+"' and patientID='"+patientID+"' ");
			while (rs.next()) {
				return false;
			}
			rs.close();//關閉rs
			st.close();//關閉st			
		} catch (SQLException e) {
			System.out.println("PatientHealthTrackingServer checkItemID Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return true;
	}
	
	/*******************************************************************************************/

	public static void main(String[] args) {
		/*
		String temp = "item123";
		String test = (String) temp.subSequence(4, temp.length());
		System.out.println(test);

		String temp2 = "{detailID:[1,2,3]}";
		Gson gson = new Gson();
		ItemDetail temp3 = gson.fromJson(temp2, ItemDetail.class);
		System.out.println(temp3.detailID.size());
		System.out.println(temp3.detailID.get(0));
	
		
		String temp6 = "{\"itemID\": \"1\", \"detail\": [{\"detailID\": \"1\", \"detailValue\": 110}, {\"detailID\": \"5\", \"detailValue\": 75}]}";
		Gson gson = new Gson();
		
		HistoryValue historyValue = gson.fromJson(temp6, HistoryValue.class);
		System.out.println("historyValue : " + historyValue.itemID);
		List<Detail> tempList = historyValue.detail;
		System.out.println(tempList.get(0).detailID);
		System.out.println(tempList.get(0).detailValue);
		System.out.println(tempList.get(1).detailID);
		System.out.println(tempList.get(1).detailValue);
		
		System.out.println(isDateBigger("2017-01-07 01:14:57.0", "2017-04-07 01:14:56.0"));
		
		ArrayList tempList1 = new ArrayList();
		ArrayList tempList2 = new ArrayList();
		ArrayList tempList3 = new ArrayList();
		
		tempList1.add("123");
		tempList1.add("234");
		tempList1.add("345");
		
		tempList2.add(tempList1);
		tempList2.add(tempList1);
		
		System.out.println("tempList1 : " + tempList1);
		System.out.println("tempList2 : " + tempList2);
		
		for(int i = 0; i<tempList2.size(); i++){
			tempList3 = (ArrayList) tempList2.get(i);
			System.out.println("tempList2.get("+i+") : " + tempList3);
			
			for(int k = 0; k<tempList3.size(); k++){
				tempList3.get(k);	
				System.out.println("tempList1.get("+i+") : " + tempList3.get(k));
			}
			
		}
		*/
		
		String temp8 = "2017-03-01";
		String temp9 = "2017-03-02";
		String temp10 = "2017-03-01";
		//如果跟開始日期一樣的話，會是true，如果跟結束日期一樣的話，會是false
		System.out.println(temp10 + " : " + betweenDate(temp8, temp9, temp10));
				
	}
}
