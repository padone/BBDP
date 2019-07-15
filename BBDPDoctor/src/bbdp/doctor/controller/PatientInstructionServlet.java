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

import bbdp.doctor.model.PatientInstructionServer;

@WebServlet("/PatientInstructionServlet")
public class PatientInstructionServlet extends HttpServlet {
    public PatientInstructionServlet() {
        super();
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		Gson gson = new Gson();	
		//連接資料庫
		DataSource datasource = (DataSource) getServletContext().getAttribute("db");
		PatientInstructionServer patientInstructionServer = new PatientInstructionServer();
		/*******************************************************************************************/
		
		// 所需參數
		HttpSession session = request.getSession();	
		String doctorID = (String) session.getAttribute("doctorID");
		
		//PatientInstruction.html
		String state = request.getParameter("state");
		String select = request.getParameter("select");

		//NewPatientInstruction.html
		String title = request.getParameter("title");
		String type = request.getParameter("type");
		String symptom = request.getParameter("symptom");
		String html = request.getParameter("html");
		
		//EditPatientInstruction.html
		String patientInstructionID = request.getParameter("patientInstructionID");
		
		//EditPatientInstruction - 編輯.html
		
		//EditPatientInstruction - 留言.html
		String commentID = request.getParameter("commentID");
		String comment_2 = request.getParameter("comment_2");

		/*******************************************************************************************/
		
		HashMap searchAllInstruction = new HashMap();			 //搜尋所有衛教資訊
		HashMap typeSelect = new HashMap();						 //選取分類後的項目
		HashMap getTypeSymptom = new HashMap();					 //取得type跟symptom
		HashMap getInstruction = new HashMap();					 //取得衛教資訊
		HashMap getComment = new HashMap();					 	 //取得留言資訊

		/*******************************************************************************************/
		
		//PatientInstruction.html//搜尋所有衛教資訊
		if(state.equals("searchAllInstruction")){
			searchAllInstruction = patientInstructionServer.getTitleDate(datasource, "select patientInstructionID, title, date from patientinstruction where doctorID='"+doctorID+"' order by date desc");
			searchAllInstruction.put("typeList", patientInstructionServer.getType(datasource, doctorID));
			
			response.getWriter().write(gson.toJson(searchAllInstruction));	// 回傳
		}
		
		//PatientInstruction.html//選取分類後的項目
		if(state.equals("typeSelect")){
			typeSelect = patientInstructionServer.getTitleDate(datasource, "select patientInstructionID, title, date from patientinstruction where doctorID='"+doctorID+"' and type='"+select+"'  order by date desc");
			response.getWriter().write(gson.toJson(typeSelect));	// 回傳
		}	
		
		/*******************************************************************************************/

		//NewPatientInstruction.html//取得type跟symptom
		if(state.equals("getTypeSymptom")){
			getTypeSymptom.put("symptomList", patientInstructionServer.getSymptom(datasource));
			getTypeSymptom.put("typeList", patientInstructionServer.getType(datasource, doctorID));
			
			response.getWriter().write(gson.toJson(getTypeSymptom));	// 回傳
		}
		
		//NewPatientInstruction.html//取得getMaxInstructionID
		if(state.equals("getMaxInstructionID")){
			response.getWriter().write(gson.toJson(patientInstructionServer.getMaxInstructionID(datasource)));	// 回傳
		}
		
		//NewPatientInstruction.html//新增
		if(state.equals("newInstruction")){
			response.getWriter().write(gson.toJson(patientInstructionServer.newInstruction(datasource, doctorID, title, type, symptom, html)));	// 回傳
		}
		
		/*******************************************************************************************/

		//EditPatientInstruction.html//取得衛教資訊
		if(state.equals("getInstruction")){
			getInstruction = patientInstructionServer.getInstruction(datasource, doctorID, patientInstructionID);
			response.getWriter().write(gson.toJson(getInstruction));	// 回傳
		}
		
		//EditPatientInstruction.html//刪除衛教資訊和留言和收藏文章的人
		if(state.equals("deleteInstruction")){
			response.getWriter().write(gson.toJson(patientInstructionServer.deleteInstruction(datasource, doctorID, patientInstructionID)));	// 回傳
		}
		
		//EditPatientInstruction.html//檢查InstructionID
		if(state.equals("checkInstructionID")){
			response.getWriter().write(gson.toJson(patientInstructionServer.checkInstructionID(datasource, doctorID, patientInstructionID)));	// 回傳
		}
		
		/*******************************************************************************************/

		//EditPatientInstruction - 編輯.html//更新
		if(state.equals("updateInstruction")){
			response.getWriter().write(gson.toJson(patientInstructionServer.updateInstruction(datasource, doctorID, patientInstructionID, title, type, symptom, html)));	// 回傳
		}
		
		/*******************************************************************************************/
		//EditPatientInstruction - 留言.html//取得留言
		if(state.equals("getComment")){
			getComment = patientInstructionServer.getComment(datasource, patientInstructionID, doctorID);
			response.getWriter().write(gson.toJson(getComment));	// 回傳
		}
		
		//EditPatientInstruction - 留言.html//醫生回復
		if(state.equals("replyComment")){
			response.getWriter().write(gson.toJson(patientInstructionServer.replyComment(datasource, commentID, comment_2)));	// 回傳
		}
		
		//EditPatientInstruction - 留言.html//刪除醫生回復
		if(state.equals("deleteReplyComment")){
			response.getWriter().write(gson.toJson(patientInstructionServer.deleteReplyComment(datasource, commentID)));	// 回傳
		}
		//EditPatientInstruction - 留言.html//刪除病患留言
		if(state.equals("deleteComment")){
			response.getWriter().write(gson.toJson(patientInstructionServer.deleteComment(datasource, commentID)));	// 回傳
		}
	}
}
