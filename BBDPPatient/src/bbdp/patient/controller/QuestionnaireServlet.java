package bbdp.patient.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.jdbc.pool.DataSource;
import com.google.gson.Gson;

import bbdp.patient.model.MedicalRecordServer;
import bbdp.patient.model.QuestionnaireServer;

@WebServlet("/QuestionnaireServlet")
public class QuestionnaireServlet extends HttpServlet {
	public QuestionnaireServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		
		String state = request.getParameter("state");
    	String patientID = request.getParameter("patientID");
        DataSource datasource = (DataSource) getServletContext().getAttribute("db");
        Gson gson = new Gson();
        
        Connection conn = null;
		try {
			conn = datasource.getConnection();
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		switch(state) { 
			//取得病患待填問卷
			case "getQuestionnaireList":{	
				ArrayList questionList = new ArrayList();
				response.getWriter().write(gson.toJson(QuestionnaireServer.getQuestionnaireList(conn,patientID,questionList)));		        
				break;
			}
			//搜尋全部醫院
			case "searchHospital":{
				ArrayList searchList = new ArrayList();
				response.getWriter().write(gson.toJson(QuestionnaireServer.searchHospital(conn,searchList)));		        
				break;				
			}
			//根據醫院選科別
			case "searchDepartment":{
				String hospital = request.getParameter("hospital");
				ArrayList searchList = new ArrayList();
				response.getWriter().write(gson.toJson(QuestionnaireServer.searchDepartment(conn,hospital,searchList)));		        
				break;				
			}
			//根據科別選醫生
			case "searchDoctor":{
				String hospital = request.getParameter("hospital");
				String department = request.getParameter("department");
				ArrayList searchList = new ArrayList();
				response.getWriter().write(gson.toJson(QuestionnaireServer.searchDoctor(conn,hospital,department,searchList)));		        
				break;				
			}
			//根據醫生選症狀
			case "searchSymptom":{
				String doctorID = request.getParameter("doctorID");
				ArrayList searchList = new ArrayList();
				response.getWriter().write(gson.toJson(QuestionnaireServer.searchSymptom(conn,doctorID,searchList)));		        
				break;				
			}
			//搜尋問卷編號
			case "searchQuestionnaireID":{
				String doctorID = request.getParameter("doctorID");
				String symptom = request.getParameter("symptom");
				response.getWriter().write(QuestionnaireServer.searchQuestionnaireID(conn,doctorID,symptom));		        
				break;				
			}
			//新增病患名字
			case "getPatientName":{	
				response.getWriter().write(QuestionnaireServer.getPatientName(conn,patientID));		              
				break;
			}
			//取得問卷資訊
			case "getQuestionnaire":{
				String doctorID = request.getParameter("doctorID");
				String questionnaireID = request.getParameter("questionnaireID");
				ArrayList questionList = new ArrayList();
				response.getWriter().write(gson.toJson(QuestionnaireServer.getQuestionnaire(conn,doctorID,questionnaireID,questionList)));		        
				break;
			}
			//取得問卷題目
			case "getQuestionList":{
				String doctorID = request.getParameter("doctorID");
				String questionnaireID = request.getParameter("questionnaireID");
				ArrayList questionList = new ArrayList();
				response.getWriter().write(gson.toJson(QuestionnaireServer.getQuestionList(conn,doctorID,questionnaireID,questionList)));		        
				break;
			}
			//取得問卷段落名稱
			case "getPartName":{
				String doctorID = request.getParameter("doctorID");
				String questionnaireID = request.getParameter("questionnaireID");
				response.getWriter().write(QuestionnaireServer.getPartName(conn,questionnaireID));		        
				break;
			}
			//取的題目名稱及選項
			case "getNameAndOption":{
				String doctorID = request.getParameter("doctorID");
				String questionID = request.getParameter("questionID");
				ArrayList questionList = new ArrayList();
				response.getWriter().write(gson.toJson(QuestionnaireServer.getNameAndOption(conn,doctorID,questionID,questionList)));		        
				break;
			}
			//取的題目圖片
			case "getImage":{
				String imagePath = request.getParameter("imagePath");
	        	FileInputStream inputStream = null;  	
	        	ServletOutputStream outputStream = response.getOutputStream();
	        	byte[] buffer = new byte[4096];
	            int bytesRead;
	        	try {
	        		inputStream = QuestionnaireServer.getImage(imagePath);   			
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
				break;
			}
			//移除待填問卷
			case "removeUnfilledQuestionnaire":{
				String unfilledID = request.getParameter("unfilledID");
				QuestionnaireServer.removeUnfilledQuestionnaire(conn,unfilledID);		        
				break;
			}
			//新增問卷答案
			case "newQuestionnaireAnswer":{	
				String doctorID = request.getParameter("doctorID");
				String questionnaireID = request.getParameter("questionnaireID");
				String answerArray = request.getParameter("answerArray");
				String describe = request.getParameter("describe");
				response.getWriter().write(QuestionnaireServer.newQuestionnaireAnswer(conn,patientID,questionnaireID,answerArray,describe));		        
				break;
			}
			//搜尋有病歷醫院
			case "searchMRHospital":{
				ArrayList searchList = new ArrayList();
				response.getWriter().write(gson.toJson(MedicalRecordServer.searchHospital(conn,patientID,searchList)));		        
				break;				
			}
			//搜尋有病歷科別
			case "searchMRDepartment":{
				String hospital = request.getParameter("hospital");
				ArrayList searchList = new ArrayList();
				response.getWriter().write(gson.toJson(MedicalRecordServer.searchDepartment(conn,patientID,hospital,searchList)));		        
				break;				
			}
			//搜尋有病歷醫生
			case "searchMRDoctor":{
				String hospital = request.getParameter("hospital");
				String department = request.getParameter("department");
				ArrayList searchList = new ArrayList();
				response.getWriter().write(gson.toJson(MedicalRecordServer.searchDoctor(conn,patientID,hospital,department,searchList)));		        
				break;				
			}
			//搜尋有病歷日期
			case "searchMRDate":{
				String doctorID = request.getParameter("doctorID");
				ArrayList searchList = new ArrayList();
				response.getWriter().write(gson.toJson(MedicalRecordServer.searchDate(conn,patientID,doctorID,searchList)));		        
				break;				
			}
			//取得病歷
			case "getMedicalRecord":{
				String medicalRecordID = request.getParameter("medicalRecordID");
				ArrayList searchList = new ArrayList();
				response.getWriter().write(gson.toJson(MedicalRecordServer.getMedicalRecord(conn,patientID,medicalRecordID,searchList)));		        
				break;				
			}
			//取得歷史問卷
			case "getQuestionnaireHistory":{
				ArrayList questionnaireList = new ArrayList();
				response.getWriter().write(gson.toJson(QuestionnaireServer.getQuestionnaireHistory(conn,patientID,questionnaireList)));		        
				break;				
			}
			default:{
				System.out.print("QuestionnaireServlet default");
				if (conn!=null) try {conn.close();}catch (Exception ignore) {}
			}
		}
	}
}

