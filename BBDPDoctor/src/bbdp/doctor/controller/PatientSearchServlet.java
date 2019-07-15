package bbdp.doctor.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Base64;

import org.apache.tomcat.jdbc.pool.DataSource;
import bbdp.doctor.model.PatientSearchVerification;

@WebServlet("/PatientSearchServlet")
public class PatientSearchServlet extends HttpServlet {
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		String option = request.getParameter("option");
		
		Connection con = null;
		DataSource datasource = (DataSource) getServletContext().getAttribute("db");
		
		try {
			con = datasource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(option.equals("search")){
			response.setContentType("text/html;charset=UTF-8");
			//response.setContentType("application/json;charset=UTF-8");
						
			String inputString = request.getParameter("account");
			String searchResult = "";
			searchResult = PatientSearchVerification.searchVerification(con, inputString);
			//編碼
			final Base64.Encoder encoder = Base64.getEncoder();
			final byte[] textByte = searchResult.getBytes("UTF-8");
			final String encodedText = encoder.encodeToString(textByte);
	
			if(searchResult.equals("fail")){	//搜尋失敗
				response.getWriter().write("fail");
			}
			else if(searchResult.equals("SQLException")){
				response.getWriter().write("SQLException");
			}
			else{	//搜尋成功
				response.getWriter().write(encodedText);
			}
		}
		else if(option.equals("select")){
			String selectPatientID = request.getParameter("selectPatient");
			
			HttpSession session = request.getSession();
			//session.setMaxInactiveInterval(60*10);
			session.setAttribute("patientID", selectPatientID);
			System.out.println("patientID(session內容) : " + (String) session.getAttribute("patientID"));
		}
		if (con!=null) try {con.close();}catch (Exception ignore) {}
	}

}
