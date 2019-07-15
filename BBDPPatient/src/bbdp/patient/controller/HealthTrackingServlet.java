package bbdp.patient.controller;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import com.google.gson.Gson;

import bbdp.patient.model.HealthTrackingServer;

@WebServlet("/HealthTrackingServlet")
public class HealthTrackingServlet extends HttpServlet {
    public HealthTrackingServlet() {
        super();
    }

    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		Gson gson = new Gson();
		//連接資料庫
		DataSource datasource = (DataSource) getServletContext().getAttribute("db");
		HealthTrackingServer healthTrackingServer=new HealthTrackingServer();
		/*******************************************************************************************/

		//設置原本所需參數
		//NewHealthTracking.html
		String state = request.getParameter("state");
		String patientID = request.getParameter("patientID");
		String itemNumber = request.getParameter("itemNumber");
		String[] detailIdArray = request.getParameterValues("detailIdArray[]");
		String[] detailValueArray = request.getParameterValues("detailValueArray[]");
		String selfDescriptionValue = request.getParameter("selfDescriptionValue");
	
		/*if(detailIdArray !=null){  
			System.out.printf("在servlet中傳入參數 detailIdArray : ");
			for(String temp:detailIdArray){  
	            System.out.printf(temp + " ");  
	        }
			System.out.println();
		}
		if(detailValueArray !=null){  
			System.out.printf("在servlet中傳入參數 detailValueArray : ");
			for(String temp:detailValueArray){  
	            System.out.printf(temp + " ");  
	        }
			System.out.println();
		}*/
		
		//HistoryHealthTrackingChartList.html
		String itemChoose = request.getParameter("itemChoose");
		String dateStart = request.getParameter("dateStart");
		String dateEnd = request.getParameter("dateEnd");
		
		//HistoryHealthTracking.html
		//String dateSelect = request.getParameter("dateSelect");
		String select = request.getParameter("select");
		
		//EditHealthTracking.html
		String time = request.getParameter("time");
		String healthTrackingID = request.getParameter("healthTrackingID");
		
		/*******************************************************************************************/

		HashMap newHealthItem = new HashMap(); 				// 新增紀錄項目
		HashMap newHealthDetail = new HashMap(); 			// 新增紀錄細項
		HashMap addHealth = new HashMap(); 					// 新增紀錄是否成功
		HashMap itemChart = new HashMap(); 					// 統計圖表:可選擇的項目列表
		HashMap itemChartShow = new HashMap(); 				// 統計圖表:取得圖表資料
		HashMap historyRecord = new HashMap(); 				// 歷史紀錄:取得紀錄
		HashMap historyRecordSelectDate = new HashMap(); 	// 歷史紀錄:選擇日期後顯示的紀錄
		HashMap editHealthDetail = new HashMap(); 			// 編輯紀錄:編輯紀錄細項和內容
		HashMap updateHealth = new HashMap(); 				//編輯紀錄:更新紀錄

		/*******************************************************************************************/
		
		//新增紀錄:取得項目//NewHealthTracking.html
		if (state.equals("newHealthItemDefault")) {
			System.out.println("在servlet中的傳入參數 state:"+ state +" patientID:"+patientID);
			newHealthItem = healthTrackingServer.newHealthItemDefault(datasource, patientID);	//取得項目

			System.out.println("在servlet中的newHealthItem : " + newHealthItem);
			response.getWriter().write(gson.toJson(newHealthItem));	//回傳
		}
		
		//新增紀錄:取得細項//NewHealthTracking.html
		if (state.equals("newHealthDetail")) {
			System.out.println("在servlet中的傳入參數 state:"+ state +" patientID:"+patientID +" itemNumber :"+itemNumber);
			newHealthDetail = healthTrackingServer.newHealthDetail(datasource, itemNumber);		//取得細項

			System.out.println("在servlet中的newHealthDetail : " + newHealthDetail);
			response.getWriter().write(gson.toJson(newHealthDetail));	//回傳
		}
		
		//新增紀錄:新增紀錄//NewHealthTracking.html
		if (state.equals("addHealth")) {
			System.out.println("在servlet中的傳入參數 state:"+ state +" patientID:"+patientID +" itemNumber :"+itemNumber);
			
			addHealth = healthTrackingServer.addHealth(datasource, patientID, itemNumber, detailIdArray, detailValueArray, selfDescriptionValue);
			
			System.out.println("在servlet中的addHealth : " + addHealth);
			response.getWriter().write(gson.toJson(addHealth));	//回傳	
		}
		
		/*******************************************************************************************/
		/*
		//統計圖表:取得可選取的項目列表//HistoryHealthTrackingChartList.html
		if (state.equals("ItemChart")) {
			System.out.println("在servlet中的傳入參數 state:"+ state +" patientID:"+patientID);
			
			itemChart = healthTrackingServer.itemChart(datasource, patientID);
			
			System.out.println("在servlet中的ItemChart : " + itemChart);
			response.getWriter().write(gson.toJson(itemChart));	//回傳
		}
		//統計圖表:取得可選取的項目列表//HistoryHealthTrackingChartList.html
		if (state.equals("ItemChartShow")) {
			System.out.println("在servlet中的傳入參數 state:"+ state +" patientID:"+patientID+" itemChoose:"+itemChoose+" dateStart:"+dateStart+" dateEnd:"+dateEnd);

			itemChartShow = healthTrackingServer.itemChartShow(datasource, patientID, itemChoose, dateStart, dateEnd);
			
			System.out.println("在servlet中的ItemChartShow : " + itemChartShow);
			response.getWriter().write(gson.toJson(itemChartShow));	//回傳
		}
		*/
		/*******************************************************************************************/

		//歷史紀錄:取得紀錄//HistoryHealthTracking.html
		if (state.equals("historyRecord")) {
			System.out.println("在servlet中的傳入參數 state:"+ state +" patientID:"+patientID);
			historyRecord = healthTrackingServer.historyRecord(datasource, patientID);

			System.out.println("在servlet中的historyRecord : " + historyRecord);
			response.getWriter().write(gson.toJson(historyRecord));	//回傳
		}
		
		//歷史紀錄:選擇日期後顯示的紀錄//HistoryHealthTracking.html
		if (state.equals("historyRecordChangeList")) {
			System.out.println("在servlet中的傳入參數 state:"+ state +" patientID:"+patientID+" dateStart:"+dateStart+" dateEnd:"+dateEnd);
			historyRecordSelectDate = healthTrackingServer.historyRecordChangeList(datasource, patientID, select, dateStart, dateEnd);

			System.out.println("在servlet中的historyRecordSelectDate : " + historyRecordSelectDate);
			response.getWriter().write(gson.toJson(historyRecordSelectDate));	//回傳
		}
		
		/*******************************************************************************************/
		
		//編輯紀錄:編輯紀錄細項和內容//EditHealthTracking.html
		if (state.equals("editHealthDetail")) {
			System.out.println("在servlet中的傳入參數 state:"+ state +" time:"+time+" healthTrackingID:"+healthTrackingID);
			editHealthDetail = healthTrackingServer.editHealthDetail(datasource, time, healthTrackingID);

			System.out.println("在servlet中的editHealthDetail : " + editHealthDetail);
			response.getWriter().write(gson.toJson(editHealthDetail));	//回傳
		}
		
		//編輯紀錄:更新紀錄//EditHealthTracking.html
		if (state.equals("updateHealth")) {
			System.out.println("在servlet中的傳入參數 state:"+ state +" patientID:"+patientID +" itemNumber :"+itemNumber);
			updateHealth = healthTrackingServer.updateHealth(datasource, patientID, itemNumber, detailIdArray, detailValueArray, selfDescriptionValue, time, healthTrackingID);
			
			System.out.println("在servlet中的updateHealth : " + updateHealth);
			response.getWriter().write(gson.toJson(updateHealth));	//回傳	
		}
		
	}
}
