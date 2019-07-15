package bbdp.patient.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.jdbc.pool.DataSource;

import bbdp.patient.model.ClinicHoursServer;

@WebServlet("/ClinicHoursServlet")
public class ClinicHoursServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		String option = request.getParameter("option");
		DataSource datasource = (DataSource) getServletContext().getAttribute("db");
		
		if (option.equals("getHospital")) {		//取得現有醫生的醫院科別資料
			response.getWriter().print(ClinicHoursServer.getHospital(datasource));
		} else if (option.equals("getClinicHours")) {		//取得門診時間
			String doctorID = request.getParameter("doctorID");
			response.getWriter().print(ClinicHoursServer.getClinicHours(datasource, doctorID));
		}
	}
}