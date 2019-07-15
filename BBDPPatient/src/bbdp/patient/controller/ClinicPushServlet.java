package bbdp.patient.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.jdbc.pool.DataSource;

import bbdp.patient.model.ClinicPushServer;

@WebServlet("/ClinicPushServlet")
public class ClinicPushServlet extends HttpServlet {


	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");
		String option = request.getParameter("option");
		String scanText = request.getParameter("scanText");
		
		Connection conn = null;
		DataSource datasource = (DataSource) getServletContext().getAttribute("db");
		try {
			conn = datasource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(option.equals("getDoctorName")){
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write(ClinicPushServer.getDoctorName(conn, scanText));
		}
		
		if (conn!=null) try {conn.close();}catch (Exception ignore) {}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	
		doGet(request, response);
	}

}
