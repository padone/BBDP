package bbdp.doctor.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;

import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.tomcat.jdbc.pool.DataSource;

import com.google.gson.Gson;

import bbdp.doctor.model.QuestionnaireModuleServer;
import bbdp.doctor.model.QuestionnairePoolServer;

@WebServlet("/QuestionnaireModuleServlet")
public class QuestionnaireModuleServlet extends HttpServlet {
	public QuestionnaireModuleServlet() {
		super();
	}	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");

    	HttpSession session = request.getSession();		
		String doctorID = (String) session.getAttribute("doctorID");
		
		String state = request.getParameter("state");
		
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
			//取得所有問卷分類
			case "searchType":{
				ArrayList typeList = new ArrayList();
				response.getWriter().write(gson.toJson(QuestionnaireModuleServer.searchType(conn,doctorID,typeList)));
				break;
			}
			//加入暫存區題目
			case "addTempStorageQuestion":{
				ArrayList questionList = new ArrayList();
				response.getWriter().write(gson.toJson(QuestionnaireModuleServer.addTempStorageQuestion(conn,doctorID,questionList)));
				break;
			}
			//新增問卷
			case "newQuestionnaire":{	
				String questionnaireName = request.getParameter("questionnaireName");
				String questionnaireType = request.getParameter("questionnaireType");
				String partArray = request.getParameter("partArray");
				String partName = request.getParameter("partName");
				String scoring = request.getParameter("scoring");
				int rs = QuestionnaireModuleServer.newQuestionnaire(conn,doctorID,questionnaireName,questionnaireType,partArray,partName,scoring);
				response.getWriter().println(rs);
			        
				break;
			}
			//取得所有問卷
			case "searchAllQuestionnaire":{
				ArrayList questionnaireList = new ArrayList();
				response.getWriter().write(gson.toJson(QuestionnaireModuleServer.searchAllQuestionnaire(conn,doctorID,questionnaireList)));
				break;
			}
			//取得所選分類的問卷		
			case "searchQuestionnaire":{
				String type = request.getParameter("type");
				ArrayList questionnaireList = new ArrayList();
				response.getWriter().write(gson.toJson(QuestionnaireModuleServer.searchQuestionnaire(conn,doctorID,type,questionnaireList)));
				break;
			}
			//取得所選問卷分類
			case "getType":{
				String questionnaireID = request.getParameter("questionnaireID");
				response.getWriter().write(QuestionnaireModuleServer.getQuestionnaire(conn,doctorID,questionnaireID,"type"));
				break;
			}
			//取得所選問卷名稱
			case "getName":{
				String questionnaireID = request.getParameter("questionnaireID");
				response.getWriter().write(QuestionnaireModuleServer.getQuestionnaire(conn,doctorID,questionnaireID,"name"));
				break;
			}
			//取得所選問卷分數
			case "getScoring":{
				String questionnaireID = request.getParameter("questionnaireID");
				response.getWriter().write(QuestionnaireModuleServer.getQuestionnaire(conn,doctorID,questionnaireID,"scoring"));
				break;
			}
			//取得所選問卷病歷
			case "getMedicalRecord":{
				String questionnaireID = request.getParameter("questionnaireID");
				response.getWriter().write(QuestionnaireModuleServer.getQuestionnaire(conn,doctorID,questionnaireID,"medicalRecord"));
				break;
			}
			//取得所選問卷Part
			case "getPartName":{
				String questionnaireID = request.getParameter("questionnaireID");
				response.getWriter().write(QuestionnaireModuleServer.getQuestionnaire(conn,doctorID,questionnaireID,"partName"));
				break;
			}
			//取得所選問卷題目
			case "getQuestionList":{
				String questionnaireID = request.getParameter("questionnaireID");
				ArrayList questionnaireList = new ArrayList();
				response.getWriter().write(gson.toJson(QuestionnaireModuleServer.getQuestionList(conn,doctorID,questionnaireID,questionnaireList)));
				break;
			}
			//取得所選問卷題目內容
			case "searchQuestion":{
				String questionID = request.getParameter("questionID");
				ArrayList questionList = new ArrayList();
				response.getWriter().write(gson.toJson(QuestionnaireModuleServer.searchQuestion(conn,doctorID,questionID,questionList)));
				break;
			}
			//修改問卷
			case "updateQuestionnaire":{
				String questionnaireID = request.getParameter("questionnaireID");
				String questionnaireName = request.getParameter("questionnaireName");
				String questionnaireType = request.getParameter("questionnaireType");
				String partArray = request.getParameter("partArray");
				String partName = request.getParameter("partName");
				String scoring = request.getParameter("scoring");
				String medicalRecord = request.getParameter("medicalRecord");
				int rs = QuestionnaireModuleServer.updateQuestionnaire(conn,doctorID,questionnaireID,questionnaireName,questionnaireType,partArray,partName,scoring,medicalRecord);
				response.getWriter().println(rs);
				break;
			}
			//允許編輯問卷
			case "allowUpdateQuestionnaire":{	
				String questionnaireID = request.getParameter("questionnaireID");
				response.getWriter().write(QuestionnaireModuleServer.allowUpdateQuestionnaire(conn,doctorID,questionnaireID));
				break;
			}
			//刪除問卷
			case "deleteQuestionnaire":{	
				String questionnaireID = request.getParameter("questionnaireID");
				int rs = QuestionnaireModuleServer.deleteQuestionnaire(conn,doctorID,questionnaireID);
				response.getWriter().println(rs);		        
				break;
			}
			//取得醫生所有症狀
			case "searchSymptom":{
				ArrayList symptomList = new ArrayList();
				response.getWriter().write(gson.toJson(QuestionnaireModuleServer.searchSymptom(conn,doctorID,symptomList)));
				break;
			}
			//新增初診問卷
			case "addFirstVisitQuestionnaire":{	
				String questionnaireID = request.getParameter("questionnaireID");
				String symptom = request.getParameter("questionnaireSymptom");
				int rs = QuestionnaireModuleServer.addFirstVisitQuestionnaire(conn,doctorID,questionnaireID,symptom);
				response.getWriter().println(rs);		        
				break;
			}
			//初診問卷
			case "updateFirstVisitQuestionnaire":{	
				String questionnaireID = request.getParameter("questionnaireID");
				String symptom = request.getParameter("questionnaireSymptom");
				int rs = QuestionnaireModuleServer.updateFirstVisitQuestionnaire(conn,doctorID,questionnaireID,symptom);
				response.getWriter().println(rs);		        
				break;
			}
			//取得所有初診問卷
			case "getFirstVisitQuestionnaire":{
				ArrayList questionnaireList = new ArrayList();
				response.getWriter().write(gson.toJson(QuestionnaireModuleServer.getFirstVisitQuestionnaire(conn,doctorID,questionnaireList)));
				break;
			}
			//刪除所選初診問卷
			case "removeFirstVisitQuestionnaire":{
				String questionnaireID = request.getParameter("questionnaireID");
				response.getWriter().write(QuestionnaireModuleServer.removeFirstVisitQuestionnaire(conn,doctorID,questionnaireID));
				break;
			}
			//取得所有題目病歷
			case "searchQuestionMedicalRecord":{
				String questionID = request.getParameter("questionID");
				ArrayList questionList = new ArrayList();
				response.getWriter().write(gson.toJson(QuestionnaireModuleServer.searchQuestionMedicalRecord(conn,doctorID,questionID,questionList)));
				break;				
			}
			//檢查問卷ID
			case "checkID":{
				String questionnaireID = request.getParameter("questionnaireID");
				response.getWriter().write(QuestionnaireModuleServer.checkID(conn,doctorID,questionnaireID));	
				break;				
			}
			default:{
				if (conn!=null) try {conn.close();}catch (Exception ignore) {}
				System.out.print("QuestionnaireModuleServlet default");
			}
		}
	}
}
