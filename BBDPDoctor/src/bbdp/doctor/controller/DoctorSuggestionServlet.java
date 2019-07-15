package bbdp.doctor.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.jdbc.pool.DataSource;

import com.google.gson.Gson;

import bbdp.doctor.model.DoctorSuggestionServer;

@WebServlet("/DoctorSuggestionServlet")
public class DoctorSuggestionServlet extends HttpServlet {
	public DoctorSuggestionServlet() {
		super();
	}	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		
		String state = request.getParameter("state");
    	HttpSession session = request.getSession();		
		String doctorID = (String) session.getAttribute("doctorID");
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
			case "newDoctorSuggestion":{
				String email = request.getParameter("email");				
				String content = request.getParameter("content");
				response.getWriter().write(DoctorSuggestionServer.newDoctorSuggestion(conn, doctorID, email, content));		        
				break;
			}
			default:{
				System.out.print("DoctorSuggestionServlet default");
				if (conn!=null) try {conn.close();}catch (Exception ignore) {}
			}
		}
	}
}
