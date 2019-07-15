package bbdp.doctor.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.jasper.tagplugins.jstl.core.Out;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import com.google.gson.Gson;


public class HealthTrackingServer {
	//取得項目type//HealthTracking.html//已關資料庫
	public ArrayList getItemType(DataSource datasource, String doctorID){
		ArrayList typeList = new ArrayList();	// item type List
		Connection con = null;
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select distinct type from healthtrackingitem where doctorID='" + doctorID + "' ");

			while (rs.next()) {
				typeList.add(rs.getString("type"));	// 使用者的項目type
			}
			rs.close();//關閉rs
		    
			st.close();//關閉st
		} catch (SQLException e) {
			System.out.println("HealthTrackingServer getItemType Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return typeList;
	}
	
	//取得項目id跟name//HealthTracking.html//已關資料庫
	public HashMap getItemIDName(DataSource datasource, String sqlString){
		HashMap getItemIDName = new HashMap();

		ArrayList nameList = new ArrayList();	// item name List
		ArrayList itemIDList = new ArrayList(); // item itemID List
		Connection con = null;
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sqlString);

			while (rs.next()) {
				nameList.add(rs.getString("name"));		// 取得使用者的項目name
				itemIDList.add(rs.getString("itemID"));	// 取得使用者的項目itemID
			}
			rs.close();//關閉rs
		    
			st.close();//關閉st
			
			getItemIDName.put("itemIDList", itemIDList);
			getItemIDName.put("nameList", nameList);
		} catch (SQLException e) {
			System.out.println("HealthTrackingServer getItemIDName Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return getItemIDName;
	}
	
	/*******************************************************************************************/

	//新增儲存//已關資料庫
	synchronized public HashMap addItemStorage(DataSource datasource, String doctorID, String modelName, String typeName, String[] nameList,
			String[] unitList, String[] range_1_List, String[] range_2_List, String[] upperLimitList,
			String[] lowerLimitList, String cycle, String chart, String selfDescription) {
		HashMap addItemStorage = new HashMap();
		Connection con = null;
		String result = "重新整理";
		Gson gson = new Gson();

		for (int i = 0; i < nameList.length; i++) {
			if (unitList[i].equals("")) {
				unitList[i] = "NULL";
			}
			if (range_1_List[i].equals("")) {
				range_1_List[i] = "NULL";
			}
			if (range_2_List[i].equals("")) {
				range_2_List[i] = "NULL";
			}
			if (upperLimitList[i].equals("")) {
				upperLimitList[i] = "NULL";
			}
			if (lowerLimitList[i].equals("")) {
				lowerLimitList[i] = "NULL";
			}
		}
		String insertdetailSQL, insertItemSQL;
		int insertDetail = 0, insertItem;

		String detail = "{\"detailID\":["; // 新增進去healthtrackingitem資料庫的detail
		ArrayList detailList = new ArrayList(); // item detail List	//確認用//並無實際使用

		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			ResultSet rs = null;
			/////////////////////////// 新增detail ///////////////////////////
			for (int i = 0; i < nameList.length; i++) {
				insertdetailSQL = "insert into healthtrackingdetail(detailID,name,unit,range_1,range_2,upperLimit,lowerLimit) select ifNULL(max(detailID+0),0)+1,'" + nameList[i] + "','" + unitList[i] + "','"+ range_1_List[i] + "','" + range_2_List[i] + "','" + upperLimitList[i] + "','"+ lowerLimitList[i] + "' FROM healthtrackingdetail";
				insertDetail = st.executeUpdate(insertdetailSQL);

				// 取得所有新增的detailID(除了最後一個)
				if (i == nameList.length - 1) {
					break;
				} else {
					rs = st.executeQuery("select max(detailID+0) as maxID from healthtrackingdetail");
					while (rs.next()) {
						detailList.add(rs.getString("maxID")); // 取得detailID
						detail += rs.getString("maxID") + ",";
					}
					rs.close();//關閉rs
				}
			}
			
			// 取得最後新增的detailID
			st = con.createStatement();
			rs = st.executeQuery("select max(detailID+0) as maxID from healthtrackingdetail");
			while (rs.next()) {
				detailList.add(rs.getString("maxID")); // 取得detailID
				detail += rs.getString("maxID") + "]}";
			}
			rs.close();//關閉rs

			//System.out.println("要新增進去Item裡的json detail : " + detail);
			/////////////////////////// 新增detail 結束 ///////////////////////////
			/////////////////////////// 新增item ///////////////////////////
			st = con.createStatement();
			insertItemSQL = "insert into healthtrackingitem(itemID,name,type,detail,cycle,doctorID,used,chart,selfDescription) select ifNULL(max(itemID+0),0)+1,'" + modelName + "','" + typeName + "','"+ detail + "','" + cycle + "','" + doctorID + "' ,0 ,'" + chart + "','" + selfDescription + "' FROM healthtrackingitem";
			insertItem = st.executeUpdate(insertItemSQL);
			/////////////////////////// 新增item 結束 ///////////////////////////

			st.close();//關閉st

			if(insertItem > 0 && insertDetail > 0)
				result = "新增成功";
			else
				result = "新增不成功";
			addItemStorage.put("result", result);
		} catch (SQLException e) {
			System.out.println("HealthTrackingServer addItemStorage Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return addItemStorage;
	}

	/*******************************************************************************************/
	
	//新增紀錄:細項
	class ItemDetail {
		List<String> detailID;

		ItemDetail(List<String> detailID) {
			this.detailID = detailID;
		}
	}
	
	// 編輯前的原本值//已關資料庫
	public HashMap editDefault(DataSource datasource, String doctorID, String itemID) {
		Gson gson = new Gson();
		HashMap editDefault = new HashMap();
		Connection con = null;
		
		itemID = (String) itemID.subSequence(4, itemID.length());	//取出item id
				
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			ResultSet rs;
			/////////////////////////////////取得item///////////////////////////////////////////////
			rs = st.executeQuery("select itemID, name, type, detail, cycle, doctorID, chart, selfDescription from healthtrackingitem where itemID = '"+itemID+"' and doctorID = '"+doctorID+"'");

			String doctor, item, modelName = null, type = null, detail = null, cycle = null, chart = null, selfDescription = null;

			while (rs.next()) {
				modelName = rs.getString("name"); 	// 取得項目name
				type = rs.getString("type"); 		// 取得項目type
				detail = rs.getString("detail"); 	// 取得項目detail
				cycle = rs.getString("cycle"); 		// 取得項目cycle	
				chart = rs.getString("chart"); 		// 取得項目chart	
				selfDescription = rs.getString("selfDescription"); 		// 取得項目selfDescription	
			}
			rs.close();//關閉rs
			
			editDefault.put("modelName", modelName);
			editDefault.put("type", type);
			editDefault.put("detail", detail);
			editDefault.put("cycle", cycle);
			editDefault.put("chart", chart);
			editDefault.put("selfDescription", selfDescription);
			
			/////////////////////////////////取得detail///////////////////////////////////////////////
			ItemDetail detailClass = gson.fromJson(detail, ItemDetail.class);
			String detailID;
			ArrayList detailIDList = new ArrayList();
			ArrayList nameList = new ArrayList();
			ArrayList unitList = new ArrayList();
			ArrayList range_1_List = new ArrayList();
			ArrayList range_2_List = new ArrayList();
			ArrayList upperLimitList = new ArrayList();
			ArrayList lowerLimitList = new ArrayList();
			
			for(int i = 0; i< detailClass.detailID.size(); i++){
				detailID = detailClass.detailID.get(i);
				rs = st.executeQuery("select detailID, name, unit, range_1, range_2, upperLimit, lowerLimit from healthtrackingdetail where detailID = '"+detailID+"'");
				while (rs.next()) {
					detailIDList.add(rs.getString("detailID"));			// 取得細項detailID
					nameList.add(rs.getString("name")); 				// 取得細項name
					unitList.add(rs.getString("unit")); 				// 取得細項unitList
					range_1_List.add(rs.getString("range_1")); 			// 取得細項range_1_List
					range_2_List.add(rs.getString("range_2")); 			// 取得細項range_2_List
					upperLimitList.add(rs.getString("upperLimit")); 	// 取得細項upperLimitList
					lowerLimitList.add(rs.getString("lowerLimit")); 	// 取得細項lowerLimitList
				}
			}
			rs.close();//關閉rs	
			st.close();//關閉st
			
			editDefault.put("detailIDList", detailIDList);	
			editDefault.put("nameList", nameList);	
			editDefault.put("unitList", unitList);	
			editDefault.put("range_1_List", range_1_List);	
			editDefault.put("range_2_List", range_2_List);	
			editDefault.put("upperLimitList", upperLimitList);	
			editDefault.put("lowerLimitList", lowerLimitList);	
		} catch (SQLException e) {
			System.out.println("HealthTrackingServer editDefault Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return editDefault;
	}

	// 修改儲存//已關資料庫
	public HashMap updateItemStorage(DataSource datasource, String doctorID, String itemID, String[] detailArray, String modelName, String typeName, String[] nameList,
			String[] unitList, String[] range_1_List, String[] range_2_List, String[] upperLimitList,
			String[] lowerLimitList, String cycle, String chart, String selfDescription) {
		HashMap updateItemStorage = new HashMap();
		Connection con = null;
		itemID = (String) itemID.subSequence(4, itemID.length());	//取出item id
		String result;
		
		Gson gson = new Gson();

		for (int i = 0; i < nameList.length; i++) {
			if (unitList[i].equals("")) {
				unitList[i] = "NULL";
			}
			if (range_1_List[i].equals("")) {
				range_1_List[i] = "NULL";
			}
			if (range_2_List[i].equals("")) {
				range_2_List[i] = "NULL";
			}
			if (upperLimitList[i].equals("")) {
				upperLimitList[i] = "NULL";
			}
			if (lowerLimitList[i].equals("")) {
				lowerLimitList[i] = "NULL";
			}
		}

		String updatedetailSQL, updateItemSQL;
		int updateDetail = 0, updateItem;

		try {
			con = datasource.getConnection();
		    Statement st = con.createStatement();
			/////////////////////////// 修改detail ///////////////////////////
			for (int i = 0; i < detailArray.length; i++) {
				updatedetailSQL = "UPDATE healthtrackingdetail"+
						" SET name='"+nameList[i]+"' ,unit='"+unitList[i]+"' ,range_1='"+range_1_List[i]+"' ,range_2='"+range_2_List[i]+"' ,upperLimit='"+upperLimitList[i]+"' ,lowerLimit='"+lowerLimitList[i]+"' "+
						" WHERE detailID = '"+detailArray[i]+"'";						
				updateDetail = st.executeUpdate(updatedetailSQL);
			}
			/////////////////////////// 修改detail 結束 ///////////////////////////
			/////////////////////////// 修改item ///////////////////////////
			updateItemSQL = "UPDATE healthtrackingitem"+
					" SET name='"+modelName+"' ,type='"+typeName+"' ,cycle='"+cycle+"' " + ",chart='"+chart+"' " + ",selfDescription='"+selfDescription+"' "+
					" WHERE itemID = '"+itemID+"' "; 
					
			updateItem = st.executeUpdate(updateItemSQL);
			/////////////////////////// 修改item 結束 ///////////////////////////
			st.close();//關閉st
			
			if(updateDetail > 0 && updateItem > 0)
				result = "儲存成功";
			else
				result = "儲存不成功";
			updateItemStorage.put("result", result);			
		} catch (SQLException e) {
			System.out.println("HealthTrackingServer updateItemStorage Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return updateItemStorage;
	}

	// 刪除//已關資料庫
	public HashMap deleteItemStorage(DataSource datasource, String doctorID, String itemID, String[] detailArray) {
		HashMap deleteItemStorage = new HashMap();
		Connection con = null;
		String result="無法刪除";
		itemID = (String) itemID.subSequence(4, itemID.length());	//取出item id
		
		Gson gson = new Gson();
		
		String deletedetailSQL, deleteItemSQL;
		int delete;
		boolean used = false;

		try {
			con = datasource.getConnection();
		    Statement st = con.createStatement();
			ResultSet rs;
			///////////////////////////判斷可不可以刪除，是否有用過 //////////////////////////////
			rs = st.executeQuery("select used from healthtrackingitem"+
					" WHERE itemID = '"+itemID+"' ");
			while (rs.next()) {
				if(rs.getString("used").equals("1")){
					used = true;
				}
			}
			rs.close();//關閉rs
			
			//如果這個項目醫生未使用過才可刪掉
			if(!used){
				result = "刪除成功";
				/////////////////////////// 刪除item ///////////////////////////
				deleteItemSQL = "Delete FROM healthtrackingitem"+
						" WHERE itemID = '"+itemID+"' and used = '0' "; 
			
				delete = st.executeUpdate(deleteItemSQL);
				/////////////////////////// 刪除item 結束 ///////////////////////////
				/////////////////////////// 刪除detail ///////////////////////////
				for (int i = 0; i < detailArray.length; i++) {
					deletedetailSQL = "Delete FROM healthtrackingdetail"+
							" WHERE detailID = '"+detailArray[i]+"'";			
		
					delete = st.executeUpdate(deletedetailSQL);
				}
				/////////////////////////// 刪除detail 結束 ///////////////////////////
			}
			else{
				result = "已發送給病患過，無法刪除";
			}
			
			st.close();//關閉st
			deleteItemStorage.put("result", result);
		} catch (SQLException e) {
			System.out.println("HealthTrackingServer deleteItemStorage Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return deleteItemStorage;
	}
	
	//檢查itemID//EditPatientInstruction.html
	public boolean checkItemID(DataSource datasource, String doctorID, String itemID) {
		Connection con = null;
		itemID = (String) itemID.subSequence(4, itemID.length());	//取出item id
		try {
			con = datasource.getConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select * from healthtrackingitem where doctorID='"+doctorID+"' and itemID='"+itemID+"' ");
			while (rs.next()) {
				return false;
			}
			rs.close();//關閉rs
			st.close();//關閉st			
		} catch (SQLException e) {
			System.out.println("HealthTrackingServer checkItemID Exception :" + e.toString());
			e.printStackTrace();
		}finally {
		      if (con!=null) try {con.close();}catch (Exception ignore) {}
		}
		return true;
	}
	
	public static void main(String[] args) {

		String temp = "item123";
		String test = (String) temp.subSequence(4, temp.length());
		System.out.println(test);

		String temp2 = "{\"detailID\":[1,2,3]}";
		Gson gson = new Gson();
		ItemDetail temp3 = gson.fromJson(temp2, ItemDetail.class);
		System.out.println(temp3.detailID.size());
		System.out.println(temp3.detailID.get(0));

		/*
		 * String temp4=
		 * "{item:item1,detailIdArray:[\"detail1\",\"detail2\",\"detail4\"],detailValueArray:[\"2\",\"3\",\"6\"]}";
		 * historyValue temp5 = gson.fromJson(temp4, historyValue.class);
		 * System.out.println(temp5.item);
		 * System.out.println(temp5.detailIdArray.size());
		 * System.out.println(temp5.detailIdArray.get(0));
		 * System.out.println(temp5.detailValueArray.size());
		 * System.out.println(temp5.detailValueArray.get(0));
		 */
	}

	
	
	
	
}
