package bbdp.doctor.controller;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import com.google.gson.Gson;

import bbdp.doctor.model.PatientHealthTrackingServer;


@WebServlet("/PatientHealthTrackingServlet")
public class PatientHealthTrackingServlet extends HttpServlet {
    public PatientHealthTrackingServlet() {
        super();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		Gson gson = new Gson();
		//連接資料庫
		DataSource datasource = (DataSource) getServletContext().getAttribute("db");
		PatientHealthTrackingServer patientHealthTrackingServer = new PatientHealthTrackingServer();
		/*******************************************************************************************/

		//所需參數	
		HttpSession session = request.getSession();	
		
		//PatientHealthTracking.html
		String patientID = (String) session.getAttribute("patientID");
		String state = request.getParameter("state");
		String doctorID = (String) session.getAttribute("doctorID");
		
		//NewPatientHealthTracking
		String select = request.getParameter("select");
		String itemSelect = request.getParameter("itemSelect");
		
		//EditPatientHealthTracking.html
		String itemID = request.getParameter("itemID");
		String dateStart = request.getParameter("dateStart");
		String dateEnd = request.getParameter("dateEnd");
		
		/*******************************************************************************************/
		
		HashMap result = new HashMap();				 // 結果
		HashMap allItem = new HashMap();			 // 所有項目結果
		HashMap allType = new HashMap();			 // 所有類別結果
		HashMap typeSelectItem = new HashMap();		 // 選什麼分類，顯示該分類的項目結果
		HashMap addItemToPatient = new HashMap();	 // 按下後新增項目給病患
		HashMap itemAllDetail = new HashMap();		 // 項目的細項資料(itemName、itemTime、detailValue、detailID)
		HashMap changeChart = new HashMap();		 // 改變日期
		HashMap deleteHealthTracking = new HashMap();// 刪除該追蹤項目
		
		/*******************************************************************************************/
		
		//一進來取得所有項目//PatientHealthTracking.html
		if (state.equals("allItem")) {
			allItem = patientHealthTrackingServer.allItemDefault(datasource, doctorID, patientID);	//取得項目
			response.getWriter().write(gson.toJson(allItem));	// 回傳json型態
		}
		
		/*******************************************************************************************/

		//取得下拉選單的值//NewPatientHealthTracking.html
		if (state.equals("allType")) {
			allType = patientHealthTrackingServer.allTypeDefault(datasource, doctorID, patientID);	//取得類別
			response.getWriter().write(gson.toJson(allType));	// 回傳json型態
		}
		//選什麼分類，顯示該分類的項目//NewPatientHealthTracking.html
		if (state.equals("typeSelectItem")) {
			typeSelectItem = patientHealthTrackingServer.typeSelectItem(datasource, doctorID, patientID, select);	//取得類別
			response.getWriter().write(gson.toJson(typeSelectItem));	// 回傳json型態
		}
		
		//按下後新增項目給病患//NewPatientHealthTracking.html
		if (state.equals("addItemToPatient")) {
			addItemToPatient = patientHealthTrackingServer.addItemToPatient(datasource, doctorID, patientID, itemSelect);
			response.getWriter().write(gson.toJson(addItemToPatient));	// 回傳json型態
		}
		
		/*******************************************************************************************/
		
		//取得該項目一些基本資料//EditPatientHealthTracking.html
		if (state.equals("itemAllDetail")) {
			itemAllDetail = patientHealthTrackingServer.itemAllDetail(datasource, doctorID, patientID, itemID);
			response.getWriter().write(gson.toJson(itemAllDetail));	// 回傳json型態
		}
		
		//改變日期//EditPatientHealthTracking.html
		if (state.equals("changeChart")) {
			changeChart = patientHealthTrackingServer.changeChart(datasource, doctorID, patientID, itemID, dateStart, dateEnd);
			response.getWriter().write(gson.toJson(changeChart));	// 回傳json型態
		}
		
		//刪除該追蹤項目//EditPatientHealthTracking.html
		if (state.equals("deleteHealthTracking")) {
			deleteHealthTracking = patientHealthTrackingServer.deleteHealthTracking(datasource, doctorID, patientID, itemID);
			response.getWriter().write(gson.toJson(deleteHealthTracking));	// 回傳json型態
		}
		
		/*******************************************************************************************/

		//檢查itemID//EditPatientHealthTracking.html//EditPatientHealthTrackingData.html
		if(state.equals("checkItemID")){
			response.getWriter().write(gson.toJson(patientHealthTrackingServer.checkItemID(datasource, doctorID, itemID, patientID)));	// 回傳
		}

	}
}
