package bbdp.doctor.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.jdbc.pool.DataSource;

import bbdp.doctor.model.HomepageServer;

@WebServlet("/HomepageServlet")
public class HomepageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		String option = request.getParameter("option");
		DataSource datasource = (DataSource) getServletContext().getAttribute("db");
		HttpSession session = request.getSession();
		String doctorID = (String) session.getAttribute("doctorID");
		if (option.equals("getPatientName")) {
			String patientID = request.getParameter("patientID");
			response.getWriter().print(HomepageServer.getPatientName(datasource, patientID));
		} else if (option.equals("getData")) {
			response.getWriter().print(HomepageServer.getHomepageData(datasource, doctorID));
		}
	}
}