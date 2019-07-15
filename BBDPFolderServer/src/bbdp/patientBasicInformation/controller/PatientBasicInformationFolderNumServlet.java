package bbdp.patientBasicInformation.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.jdbc.pool.DataSource;

import bbdp.patientBasicInformation.model.PatientBasicInformationFolderNumServer;

@WebServlet("/PatientBasicInformationFolderNumServlet")
public class PatientBasicInformationFolderNumServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		String option = request.getParameter("option");
		DataSource datasource = (DataSource) getServletContext().getAttribute("db");
		String doctorID = request.getParameter("doctorID");
		String patientID = request.getParameter("patientID");
		if (option.equals("getFNum")) {
			response.getWriter().println(PatientBasicInformationFolderNumServer.getRecentFolder(datasource, patientID, doctorID));
		}
	}
}