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

import bbdp.doctor.model.PatientMedicalRecordServer;

@WebServlet("/PatientMedicalRecordServlet")
public class PatientMedicalRecordServlet extends HttpServlet {
	public PatientMedicalRecordServlet() {
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
			//新增病歷
			case "newMedicalRecord":{
				String medicalRecord = request.getParameter("medicalRecord");
				int rs = PatientMedicalRecordServer.newMedicalRecord(conn,doctorID,patientID,medicalRecord);
				response.getWriter().println(rs);					
				break;
			}
			//取得所選病歷日期
			case "selectMedicalRecordDate":{
				String dateRange = request.getParameter("dateRange");
				ArrayList dateList = new ArrayList();
				response.getWriter().write(gson.toJson(PatientMedicalRecordServer.selectMedicalRecordDate(conn,doctorID,patientID,dateRange,dateList)));
				break;
			}
			//取得所選病歷
			case "getMedicalRecordList":{
				String dateRange = request.getParameter("dateRange");
				String date = request.getParameter("date");
				ArrayList medicalRecordList = new ArrayList();
				response.getWriter().write(gson.toJson(PatientMedicalRecordServer.getMedicalRecordList(conn,doctorID,patientID,dateRange,date,medicalRecordList)));
				break;
			}
			//檢查病歷ID
			case "checkMedicalRecordID":{
				String medicalRecordID = request.getParameter("medicalRecordID");
				response.getWriter().write(PatientMedicalRecordServer.checkMedicalRecordID(conn,doctorID,patientID,medicalRecordID));
				break;
			}
			//取得病歷
			case "getMedicalRecord":{
				String medicalRecordID = request.getParameter("medicalRecordID");
				ArrayList medicalRecordList = new ArrayList();
				response.getWriter().write(gson.toJson(PatientMedicalRecordServer.getMedicalRecord(conn,doctorID,patientID,medicalRecordID,medicalRecordList)));
				break;
			}
			//修改病歷
			case "editMedicalRecord":{
				String medicalRecordID = request.getParameter("medicalRecordID");
				String medicalRecord = request.getParameter("medicalRecord");
				int rs = PatientMedicalRecordServer.editMedicalRecord(conn,doctorID,patientID,medicalRecordID,medicalRecord);
				response.getWriter().println(rs);					
				break;
			}
			default:{
				if (conn!=null) try {conn.close();}catch (Exception ignore) {}
				System.out.print("PatientMedicalRecordServlet default");
			}
		}
	}
}
