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

import bbdp.doctor.model.QuestionnairePoolServer;

@WebServlet("/QuestionnairePoolServlet")
public class QuestionnairePoolServlet extends HttpServlet {
	public QuestionnairePoolServlet() {
		super();
	}	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
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
			//新增題目
			case "newQuestion":{	
				String questionID = request.getParameter("QuestionID");
				String questionName = request.getParameter("QuestionName");
				String questionType = request.getParameter("QuestionType");
				String questionOptionType = request.getParameter("QuestionOptionType");
				String questionOption = request.getParameter("QuestionOption");
				int rs = QuestionnairePoolServer.newQuestion(conn,doctorID,questionID,questionName,questionType,questionOptionType,questionOption);
				response.getWriter().println(rs);
			        
				break;
			}
			//取得新的questionID
			case "getMaxQuestionID":{	
				String maxQuestionID = QuestionnairePoolServer.getMaxQuestionID(conn,doctorID); 
				response.getWriter().write(maxQuestionID);
				break;
			}
			//取得所有題目分類
			case "searchType":{
				ArrayList typeList = new ArrayList();
				response.getWriter().write(gson.toJson(QuestionnairePoolServer.searchType(conn,doctorID,typeList)));
				break;
			}
			//取得所有題目
			case "searchAllQuestion":{
				ArrayList questionList = new ArrayList();
				response.getWriter().write(gson.toJson(QuestionnairePoolServer.searchAllQuestion(conn,doctorID,questionList)));
				break;
			}
			//取得所選分類的題目			
			case "searchQuestion":{
				String type = request.getParameter("type");
				ArrayList questionList = new ArrayList();
				response.getWriter().write(gson.toJson(QuestionnairePoolServer.searchQuestion(conn,doctorID,type,questionList)));
				break;
			}
			//取的所選題目分類
			case "getType":{
				String questionID = request.getParameter("questionID");
				response.getWriter().write(QuestionnairePoolServer.getQuestion(conn,doctorID,questionID,"type"));
				break;
			}
			//取的所選題目類型
			case "getKind":{
				String questionID = request.getParameter("questionID");
				response.getWriter().write(QuestionnairePoolServer.getQuestion(conn,doctorID,questionID,"kind"));
				break;
			}
			//取的所選題目
			case "getQuestion":{
				String questionID = request.getParameter("questionID");
				response.getWriter().write(QuestionnairePoolServer.getQuestion(conn,doctorID,questionID,"question"));
				break;
			}
			//取的所選題目選項
			case "getOption":{
				String questionID = request.getParameter("questionID");
				response.getWriter().write(QuestionnairePoolServer.getQuestion(conn,doctorID,questionID,"option"));
				break;
			}
			//取的所選題目病歷
			case "getMedicalRecord":{
				String questionID = request.getParameter("questionID");
				response.getWriter().write(QuestionnairePoolServer.getMedicalRecord(conn,doctorID,questionID,"medicalRecord"));
				break;
			}
			//刪除題目
			case "deleteQuestion":{
				String questionID = request.getParameter("questionID");
				int rs = QuestionnairePoolServer.deleteQuestion(conn,doctorID,questionID);
				response.getWriter().println(rs);
				break;
			}
			//允許編輯題目
			case "allowUpdateQuestion":{	
				String questionID = request.getParameter("questionID");
				int rs = QuestionnairePoolServer.allowUpdateQuestion(conn,doctorID,questionID);
				response.getWriter().println(rs);
				break;
			}
			//修改題目
			case "updateQuestion":{	
				String questionID = request.getParameter("QuestionID");
				String questionName = request.getParameter("QuestionName");
				String questionType = request.getParameter("QuestionType");
				String questionOptionType = request.getParameter("QuestionOptionType");
				String questionOption = request.getParameter("QuestionOption");
				String medicalRecord = request.getParameter("MedicalRecord"); 
				int rs = QuestionnairePoolServer.updateQuestion(conn,doctorID,questionID,questionName,questionType,questionOptionType,questionOption,medicalRecord);
				response.getWriter().println(rs);
			        
				break;
			}
			//加入暫存區
			case "addTempStorage":{	
				String questionArray = request.getParameter("questionArray");
				int rs = QuestionnairePoolServer.addTempStorage(conn,doctorID,questionArray);
				response.getWriter().println(rs);        
				break;
			}
			//取得暫存區題目分類
			case "searchTempStorageType":{
				ArrayList typeList = new ArrayList();
				response.getWriter().write(gson.toJson(QuestionnairePoolServer.searchTempStorageType(conn,doctorID,typeList)));
				break;
			}
			//取得全部暫存區題目
			case "searchAllTempStorage":{
				ArrayList questionList = new ArrayList();
				response.getWriter().write(gson.toJson(QuestionnairePoolServer.searchAllTempStorage(conn,doctorID,questionList)));
				break;
			}
			//取得所選分類的暫存區題目			
			case "searchTempStorageQuestion":{
				String type = request.getParameter("type");
				ArrayList questionList = new ArrayList();
				response.getWriter().write(gson.toJson(QuestionnairePoolServer.searchTempStorageQuestion(conn,doctorID,type,questionList)));
				break;
			}
			//將題目從暫存區移除			
			case "removeTempStorage":{
				String questionArray = request.getParameter("questionArray");
				int rs = QuestionnairePoolServer.removeTempStorage(conn,doctorID,questionArray);
				response.getWriter().println(rs);    
				break;
			}
			//檢查題目ID
			case "checkID":{
				String questionID = request.getParameter("questionID");
				response.getWriter().write(QuestionnairePoolServer.checkID(conn,doctorID,questionID));
				break;
			}
			//刪除maxIDlist
			case "deleteQuestionIDList":{
				String questionID = request.getParameter("questionID");
				QuestionnairePoolServer.deleteQuestionIDList(conn,doctorID,questionID);
				break;
			}			
			default:{
				if (conn!=null) try {conn.close();}catch (Exception ignore) {} 
				System.out.print("QuestionnairePoolServlet default");
			}
		}
	}
}
