package bbdp.doctor.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.jdbc.pool.DataSource;

import com.google.gson.Gson;

import bbdp.doctor.model.PatientQuestionnaireServer;

@WebServlet("/PatientQuestionnaireServlet")
public class PatientQuestionnaireServlet extends HttpServlet {
	public PatientQuestionnaireServlet() {
		super();
	}	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");

    	HttpSession session = request.getSession();		
		String doctorID = (String) session.getAttribute("doctorID");
		String patientID = (String) session.getAttribute("patientID");
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
			//發送問卷給病患
			case "newPatientQuestionnaire":{
				String questionnaireID = request.getParameter("questionnaireID");
				String times = request.getParameter("times");
				String cycleType = request.getParameter("cycleType");
				String totalTimes = request.getParameter("totalTimes");
				String today = request.getParameter("today");
				int rs = PatientQuestionnaireServer.newPatientQuestionnaire(datasource,conn,doctorID,patientID,questionnaireID,times,cycleType,totalTimes,today);
				response.getWriter().println(rs);	
				break;
			}
			//取的問卷答案所有分類
			case "searchPatientQuestionnaireType":{
				ArrayList typeList = new ArrayList();
				response.getWriter().write(gson.toJson(PatientQuestionnaireServer.searchPatientQuestionnaireType(conn,doctorID,patientID,typeList)));
				break;
			}
			//取得所選問卷答案分類 all全部 0自評 1後評 not未填寫
			case "selectQuestionnaire":{
				String patientSelect = request.getParameter("patientSelect");
				ArrayList typeList = new ArrayList();
				if(patientSelect.equals("all"))
					response.getWriter().write(gson.toJson(PatientQuestionnaireServer.searchPatientQuestionnaireType(conn,doctorID,patientID,typeList)));
				else if(patientSelect.equals("not"))
					response.getWriter().write(gson.toJson(PatientQuestionnaireServer.unfilledquestionnaire(conn,doctorID,patientID,typeList)));
				else
					response.getWriter().write(gson.toJson(PatientQuestionnaireServer.selectQuestionnaire(conn,doctorID,patientID,patientSelect,typeList)));
				break;
			}
			//取得所選問卷答案日期 all全部 0自評 1後評 not未填寫
			case "selectQuestionnaireDate":{
				String patientSelect = request.getParameter("patientSelect");
				String type = request.getParameter("type");
				String dateRange = request.getParameter("dateRange");
				ArrayList dateList = new ArrayList();
				response.getWriter().write(gson.toJson(PatientQuestionnaireServer.selectQuestionnaireDate(conn,doctorID,patientID,patientSelect,type,dateRange,dateList)));
				break;
			}
			//取得問卷列表(左邊共用)
			case "getQuestionnaireList":{
				String patientSelect = request.getParameter("patientSelect");
				String type = request.getParameter("type");
				String dateRange = request.getParameter("dateRange");
				String date = request.getParameter("date");
				ArrayList questionnaireList = new ArrayList();
				response.getWriter().write(gson.toJson(PatientQuestionnaireServer.getQuestionnaireList(conn,doctorID,patientID,patientSelect,type,dateRange,date,questionnaireList)));
				break;
			}
			//檢查答案網址
			case "checkAnswerID":{
				String answerID = request.getParameter("answerID");
				response.getWriter().write(PatientQuestionnaireServer.checkAnswerID(conn,doctorID,patientID,answerID));
				break;
			}
			//檢查待填問卷網址
			case "checkUnfilledID":{
				String unfilledID = request.getParameter("unfilledID");
				response.getWriter().write(PatientQuestionnaireServer.checkUnfilledID(conn,doctorID,patientID,unfilledID));
				break;
			}	
			//取得待填問卷
			case "getUnfilledQuestionnaire":{
				String unfilledID = request.getParameter("unfilledID");
				ArrayList questionnaireList = new ArrayList();
				response.getWriter().write(gson.toJson(PatientQuestionnaireServer.getUnfilledQuestionnaire(conn,doctorID,patientID,unfilledID,questionnaireList)));
				break;
			}
			//刪除已發送問卷
			case "cancelQuestionnaire":{
				String unfilledID = request.getParameter("unfilledID");
				int rs = PatientQuestionnaireServer.cancelQuestionnaire(datasource,conn,patientID,unfilledID);
				response.getWriter().println(rs);				
				break;
			}
			//取得問卷答案
			case "getQuestionnaireAnswer":{
				String answerID = request.getParameter("answerID");
				ArrayList questionnaireList = new ArrayList();
				response.getWriter().write(gson.toJson(PatientQuestionnaireServer.getQuestionnaireAnswer(conn,doctorID,patientID,answerID,questionnaireList)));			
				break;
			}
			//取得問卷答案(所填陣列)
			case "getOptionAnswer":{
				String answerID = request.getParameter("answerID");
				response.getWriter().write(PatientQuestionnaireServer.getOptionAnswer(conn,doctorID,patientID,answerID));
				break;
			}
			//取得問卷答案(自評)
			case "getPatientOptionAnswer":{
				String answerID = request.getParameter("answerID");
				response.getWriter().write(PatientQuestionnaireServer.getPatientOptionAnswer(conn,doctorID,patientID,answerID));
				break;
			}
			//取得問卷病歷
			case "getMedicalRecord":{
				String questionnaireID = request.getParameter("questionnaireID");
				ArrayList medicalRecordList = new ArrayList();
				response.getWriter().write(gson.toJson(PatientQuestionnaireServer.getMedicalRecord(conn,doctorID,patientID,questionnaireID,medicalRecordList)));
				break;
			}
			//取得自評按鈕(判斷有沒有後評)
			case "getMoreButton":{
				String answerID = request.getParameter("answerID");
				response.getWriter().write(PatientQuestionnaireServer.getMoreButton(conn,doctorID,patientID,answerID));
				break;
			}
			//新增後評
			case "newAnswer":{
				String answerID = request.getParameter("answerID");
				String questionnaireID = request.getParameter("questionnaireID");
				String doctorAnswer = request.getParameter("doctorAnswer");
				String selfDescription = request.getParameter("selfDescription");
				response.getWriter().write(PatientQuestionnaireServer.newAnswer(conn,doctorID,patientID,answerID,questionnaireID,doctorAnswer,selfDescription));
				break;
			}
			//刪除後評
			case "deleteDoctorAnswer":{
				String answerID = request.getParameter("answerID");
				int rs = PatientQuestionnaireServer.deleteDoctorAnswer(conn,doctorID,patientID,answerID);
				response.getWriter().println(rs);	
				break;
			}
			//取得問卷編號及最新的答案及日期
			case "getNewestAnswer":{
				ArrayList patientAnswer = new ArrayList();
				response.getWriter().write(gson.toJson(PatientQuestionnaireServer.getNewestAnswer(conn,doctorID,patientID,patientAnswer)));
				break;
			}			
			default:{
				if (conn!=null) try {conn.close();}catch (Exception ignore) {}
				System.out.print("PatientQuestionnaireServlet default");
			}
		}
	}
}

