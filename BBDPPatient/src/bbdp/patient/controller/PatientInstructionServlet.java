package bbdp.patient.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import com.google.gson.Gson;

import bbdp.patient.model.PatientInstructionServer;

@WebServlet("/PatientInstructionServlet")
public class PatientInstructionServlet extends HttpServlet {
    public PatientInstructionServlet() {
        super();
    }
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		Gson gson = new Gson();	
		//連接資料庫
		DataSource datasource = (DataSource) getServletContext().getAttribute("db");
		PatientInstructionServer patientInstructionServer = new PatientInstructionServer();
		/*******************************************************************************************/

		// 所需參數
		//AllOfPatientInstructions.html
		String state = request.getParameter("state");
		String select = request.getParameter("select");
		String patientID = request.getParameter("patientID");
		String department = request.getParameter("department");
		String stateType = request.getParameter("stateType");
		
		//PatientInstruction.html
		String patientInstructionID = request.getParameter("patientInstructionID");
		
		//NewComment.html
		String comment_1 = request.getParameter("comment_1");
		String hideImg = request.getParameter("hideImg");
		
		//PatientInstructionFavoriteList.html
		String sort = request.getParameter("sort");


		/*******************************************************************************************/
		
		HashMap getDepartment = new HashMap();			 //取得科別
		HashMap getSymptom = new HashMap();			 	 //取得症狀
		HashMap getAllList = new HashMap();				 //取得所有衛教文章
		HashMap getDepartmentList = new HashMap();		 //取得該科別的所有衛教文章
		HashMap getSymptomList = new HashMap();		 	 //取得該症狀的所有衛教文章
		HashMap getDoctorList = new HashMap();		 	 //取得該醫生的所有衛教文章

		HashMap getSymptomDepartment = new HashMap();	 //取得訂閱的科別(症狀)
		HashMap getSubscriptionSymptom = new HashMap();	 //取得有訂閱的症狀
		HashMap getDoctorDepartment = new HashMap();	 //取得有訂閱的科別(醫生)
		HashMap getSubscriptionDoctor = new HashMap();	 //取得有訂閱的醫生
		
		
		HashMap getDoctor = new HashMap();			 	 //取得醫生
		HashMap getSubscriptionList = new HashMap();	 //取得訂閱的衛教資訊(醫生/症狀)
		HashMap getInstruction = new HashMap();			 //取得單一的衛教資訊
		HashMap getComment = new HashMap();				 //取得留言資訊
		HashMap getFavoriteList = new HashMap();		 //取得收藏列表
		HashMap newComment = new HashMap();				 //新增留言
		
		/*******************************************************************************************/
		
		//AllOfPatientInstructions.html.html//取得科別
		if(state.equals("getDepartment")){
			getDepartment.put("departmentList", patientInstructionServer.getDepartment(datasource));
			response.getWriter().write(gson.toJson(getDepartment));	// 回傳
		}
		
		//AllOfPatientInstructions.html//取得症狀
		if(state.equals("getSymptom")){
			getSymptom.put("symptomList", patientInstructionServer.getSymptom(datasource, select));
			response.getWriter().write(gson.toJson(getSymptom));	// 回傳
		}
		
		//AllOfPatientInstructions.html//取得醫生
		if(state.equals("getDoctor")){
			getDoctor = patientInstructionServer.getDoctor(datasource, select);
			response.getWriter().write(gson.toJson(getDoctor));	// 回傳
		}
		
		//AllOfPatientInstructions.html//取得所有衛教文章
		if(state.equals("getAllList")){
			getAllList = patientInstructionServer.getAllList(datasource);
			response.getWriter().write(gson.toJson(getAllList));	// 回傳
		}
		
		//AllOfPatientInstructions.html//取得該科別的所有衛教文章
		if(state.equals("getDepartmentList")){
			getDepartmentList = patientInstructionServer.getDepartmentList(datasource, department);
			response.getWriter().write(gson.toJson(getDepartmentList));	// 回傳
		}
		
		//AllOfPatientInstructions.html//取得該症狀的所有衛教文章
		if(state.equals("getSymptomList")){
			getSymptomList = patientInstructionServer.getSymptomList(datasource, department, select);
			response.getWriter().write(gson.toJson(getSymptomList));	// 回傳
		}
		
		//AllOfPatientInstructions.html//取得該醫生的所有衛教文章
		if(state.equals("getDoctorList")){
			getDoctorList = patientInstructionServer.getDoctorList(datasource, department, select);
			response.getWriter().write(gson.toJson(getDoctorList));	// 回傳
		}
				
		/*******************************************************************************************/

		//Subscription.html//取得訂閱的科別(症狀)
		if(state.equals("getSymptomDepartment")){
			getSymptomDepartment.put("departmentList", patientInstructionServer.getSymptomDepartment(datasource, patientID));
			response.getWriter().write(gson.toJson(getSymptomDepartment));	// 回傳
		}
		
		//Subscription.html//取得有訂閱的症狀
		if(state.equals("getSubscriptionSymptom")){
			getSubscriptionSymptom.put("symptomList", patientInstructionServer.getSubscriptionSymptom(datasource, patientID, select));
			response.getWriter().write(gson.toJson(getSubscriptionSymptom));	// 回傳
		}
		
		//Subscription.html//取得有訂閱的科別(醫生)
		if(state.equals("getDoctorDepartment")){
			getDoctorDepartment.put("departmentList", patientInstructionServer.getDoctorDepartment(datasource, patientID));
			response.getWriter().write(gson.toJson(getDoctorDepartment));	// 回傳
		}
		
		//Subscription.html//取得有訂閱的醫生
		if(state.equals("getSubscriptionDoctor")){
			getSubscriptionDoctor = patientInstructionServer.getSubscriptionDoctor(datasource, patientID, select);
			response.getWriter().write(gson.toJson(getSubscriptionDoctor));	// 回傳
		}
		
		//Subscription.html//取得該科別訂閱的所有衛教文章(症狀)
		if(state.equals("getSymptomDepartmentList")){
			response.getWriter().write(gson.toJson(patientInstructionServer.getSymptomDepartmentList(datasource, patientID, department)));	// 回傳
		}
		
		//Subscription.html//顯示該科別訂閱的所有衛教文章(醫生)
		if(state.equals("getDoctorDepartmentList")){
			response.getWriter().write(gson.toJson(patientInstructionServer.getDoctorDepartmentList(datasource, patientID, department)));	// 回傳
		}
		
		//Subscription.html//顯示訂閱的所有衛教文章(症狀或醫生)
		if(state.equals("getSubscriptionList")){
			getSubscriptionList = patientInstructionServer.getSubscriptionList(datasource, patientID, stateType);
			response.getWriter().write(gson.toJson(getSubscriptionList));	// 回傳
		}
		
		//Subscription.html//刪除訂閱
		if(state.equals("deleteSubscription")){
			response.getWriter().write(gson.toJson(patientInstructionServer.deleteSubscription(datasource, patientID, select, stateType)));	// 回傳
		}
		
		/*******************************************************************************************/
		//NewSubscription.html//此症狀是否訂閱//此醫生是否訂閱
		if(state.equals("isSubscription")){
			response.getWriter().write(gson.toJson(patientInstructionServer.isSubscription(datasource, patientID, select, stateType)));	// 回傳
		}
				
		//NewSubscription.html.html//新增訂閱
		if(state.equals("newSubscription")){
			response.getWriter().write(gson.toJson(patientInstructionServer.newSubscription(datasource, patientID, select, stateType)));	// 回傳
		}
		
		/*******************************************************************************************/

		//PatientInstruction.html//取得單一的衛教資訊
		if(state.equals("getInstruction")){
			getInstruction = patientInstructionServer.getInstruction(datasource, patientInstructionID);
			response.getWriter().write(gson.toJson(getInstruction));	// 回傳
		}
		
		//PatientInstruction.html//取得留言資訊
		if(state.equals("getComment")){
			getComment = patientInstructionServer.getComment(datasource, patientInstructionID, patientID);
			response.getWriter().write(gson.toJson(getComment));	// 回傳
		}
		
		//PatientInstruction.html//是否有收藏
		if(state.equals("isCollect")){
			response.getWriter().write(gson.toJson(patientInstructionServer.isCollect(datasource, patientInstructionID, patientID)));	// 回傳
		}
		
		//PatientInstruction.html//新增收藏
		if(state.equals("newCollect")){
			response.getWriter().write(gson.toJson(patientInstructionServer.newCollect(datasource, patientInstructionID, patientID)));	// 回傳
		}
		
		//PatientInstruction.html//刪除收藏
		if(state.equals("deleteCollect")){
			response.getWriter().write(gson.toJson(patientInstructionServer.deleteCollect(datasource, patientInstructionID, patientID)));	// 回傳
		}

		//PatientInstruction.html//取得圖片位置
		Connection conn = null;
		try {
			conn = datasource.getConnection();
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(state.equals("getSrc")){
			String srcPath = request.getParameter("srcPath");
	    	FileInputStream inputStream = null;  	
	    	ServletOutputStream outputStream = response.getOutputStream();
	    	byte[] buffer = new byte[4096];
	        int bytesRead;
	    	try {
	    		inputStream = PatientInstructionServer.getSrc(srcPath);   			
	    		if(inputStream!=null){
	        		while ((bytesRead = inputStream.read(buffer)) != -1) {
	        			outputStream.write(buffer, 0, bytesRead);
		            }
				} 
			}
			catch (FileNotFoundException e) {
				System.out.println("發生FileNotFoundException : " + e);
			}
	    	finally{
	    		if (inputStream!=null) inputStream.close();
	    		if (outputStream!=null){ 
	    			outputStream.flush();
	    			outputStream.close();
	    		}
	    		if (conn!=null) try {conn.close();}catch (Exception ignore) {}
	    	}
		}		
		
		/*******************************************************************************************/
		
		//NewComment.html//新增留言
		if(state.equals("newComment")){
			newComment = patientInstructionServer.getInstruction(datasource, patientInstructionID);
			newComment.put("name", patientInstructionServer.getPatientName(datasource, patientID));
			newComment.put("flag", patientInstructionServer.newComment(datasource, patientInstructionID, patientID, comment_1, hideImg));
			response.getWriter().write(gson.toJson(newComment));	// 回傳
		}
		
		/*******************************************************************************************/

		//PatientInstructionFavoriteList.html//取得收藏列表
		if(state.equals("getFavoriteList")){
			getFavoriteList = patientInstructionServer.getFavoriteList(datasource, patientID, sort);
			response.getWriter().write(gson.toJson(getFavoriteList));	// 回傳
		}
		if (conn!=null) try {conn.close();}catch (Exception ignore) {}
	}
}
