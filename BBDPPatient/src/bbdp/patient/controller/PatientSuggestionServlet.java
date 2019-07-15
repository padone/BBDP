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

import org.apache.tomcat.jdbc.pool.DataSource;

import com.google.gson.Gson;

import bbdp.patient.model.PatientSuggestionServer;

@WebServlet("/PatientSuggestionServlet")
public class PatientSuggestionServlet extends HttpServlet {
	public PatientSuggestionServlet() {
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
			//將系統回報存到資料庫
			case "newPatientSuggestion":{	
				String email = request.getParameter("email");
				String content = request.getParameter("content");
				response.getWriter().write(PatientSuggestionServer.newPatientSuggestion(conn, patientID, email, content));		        
				break;
			}
			default:{
				System.out.print("PatientSuggestionServlet default");
				if (conn!=null) try {conn.close();}catch (Exception ignore) {}
			}
		}
	}
}
