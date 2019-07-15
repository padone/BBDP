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

import bbdp.doctor.model.HealthTrackingServer;

@WebServlet("/HealthTrackingServlet")
public class HealthTrackingServlet extends HttpServlet {
    public HealthTrackingServlet() {
        super();
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		Gson gson = new Gson();	
		//連接資料庫
		DataSource datasource = (DataSource) getServletContext().getAttribute("db");
		HealthTrackingServer healthTrackingServer=new HealthTrackingServer();
		
		/*******************************************************************************************/
		
		// 所需參數
		HttpSession session = request.getSession();	
		String doctorID = (String) session.getAttribute("doctorID");
		
		//HealthTracking.html
		String state = request.getParameter("state");
		String select = request.getParameter("select");
		
		//NewHealthTracking.html
		String modelName = request.getParameter("modelName");
		String typeName = request.getParameter("typeName");
		String[] nameList = request.getParameterValues("nameList[]");
		String[] unitList = request.getParameterValues("unitList[]");
		String[] range_1_List = request.getParameterValues("range_1_List[]");
		String[] range_2_List = request.getParameterValues("range_2_List[]");
		String[] upperLimitList = request.getParameterValues("upperLimitList[]");
		String[] lowerLimitList = request.getParameterValues("lowerLimitList[]");
		String cycle = request.getParameter("cycle");
		String chart = request.getParameter("chart");
		String selfDescription = request.getParameter("selfDescription");

		//EditHealthTracking.html
		String itemID = request.getParameter("itemID");
		String[] detailArray = request.getParameterValues("detailArray[]");
		
		/*******************************************************************************************/
		
		HashMap allItem = new HashMap();			 // 所有項目結果
		HashMap typeSelect = new HashMap();			 // 選取分類後的項目結果
		HashMap addItemStorage = new HashMap();		 // 儲存新增
		HashMap editDefault = new HashMap();		 // 編輯前的原本值
		HashMap updateItemStorage = new HashMap();	 // 修改儲存
		HashMap deleteItemStorage = new HashMap();	 // 刪除結果
		
		/*******************************************************************************************/
		
		//取得所有項目//HealthTracking.html
		if (state.equals("allItem")) {
			allItem = healthTrackingServer.getItemIDName(datasource, "select itemID, name from healthtrackingitem where doctorID='" + doctorID + "' ORDER BY (itemID+0) DESC");		//取得項目id跟name
			allItem.put("typeList", healthTrackingServer.getItemType(datasource, doctorID));	//取得項目type
			response.getWriter().write(gson.toJson(allItem));	// 回傳
		}
		
		//選取分類後的項目//HealthTracking.html
		if (state.equals("typeSelect")) {
			typeSelect = healthTrackingServer.getItemIDName(datasource,"select name, itemID from healthtrackingitem where doctorID='" + doctorID + "' and type='" + select + "' ORDER BY itemID DESC");	//取得項目id跟name
			response.getWriter().write(gson.toJson(typeSelect));	// 回傳
		}
		
		/*******************************************************************************************/
		
		//儲存//NewHealthTracking.html
		if (state.equals("storage")) {
			addItemStorage = healthTrackingServer.addItemStorage(datasource, doctorID, modelName, typeName, nameList, unitList, range_1_List, range_2_List, upperLimitList, lowerLimitList, cycle, chart, selfDescription);
			response.getWriter().write(gson.toJson(addItemStorage));	// 回傳
		}
		
		/*******************************************************************************************/
		
		//取得原本的值//EditHealthTracking.html
		if (state.equals("editDefault")) {
			editDefault = healthTrackingServer.editDefault(datasource, doctorID, itemID);
			response.getWriter().write(gson.toJson(editDefault));	// 回傳
		}
		
		//更新修改後的值//EditHealthTracking.html
		if (state.equals("update")) {
			updateItemStorage = healthTrackingServer.updateItemStorage(datasource, doctorID, itemID, detailArray, modelName, typeName, nameList, unitList, range_1_List, range_2_List, upperLimitList, lowerLimitList, cycle, chart, selfDescription);
			response.getWriter().write(gson.toJson(updateItemStorage));	// 回傳
		}
		
		//刪除//EditHealthTracking.html
		if (state.equals("deleteItem")) {
			deleteItemStorage = healthTrackingServer.deleteItemStorage(datasource, doctorID, itemID, detailArray);
			response.getWriter().write(gson.toJson(deleteItemStorage));	// 回傳
		}
		
		//檢查itemID//EditHealthTracking.html
		if(state.equals("checkItemID")){
			response.getWriter().write(gson.toJson(healthTrackingServer.checkItemID(datasource, doctorID, itemID)));	// 回傳
		}
	}
}
