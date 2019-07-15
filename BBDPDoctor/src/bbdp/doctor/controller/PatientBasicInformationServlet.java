package bbdp.doctor.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.jdbc.pool.DataSource;

import bbdp.doctor.model.PatientBasicInformationServer;

@WebServlet("/PatientBasicInformationServlet")
public class PatientBasicInformationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		String option = request.getParameter("option");
		DataSource datasource = (DataSource) getServletContext().getAttribute("db");
		
		HttpSession session = request.getSession();
		//session.setAttribute("doctorID", "1");		//先放假的
		//session.setAttribute("patientID", "1000");		//先放假的
		String doctorID = (String) session.getAttribute("doctorID");
		String patientID = (String) session.getAttribute("patientID");
		
		if (option.equals("patientName")) {
			response.getWriter().print(PatientBasicInformationServer.getPatientName(datasource, patientID));
		} else if (option.equals("patientBasicInformation")) {
			response.getWriter().println(PatientBasicInformationServer.getPatientBasicInformation(datasource, patientID, doctorID));
		}
	}
}