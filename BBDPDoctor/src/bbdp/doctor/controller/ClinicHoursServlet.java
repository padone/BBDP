package bbdp.doctor.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.jdbc.pool.DataSource;

import bbdp.doctor.model.ClinicHoursServer;

@WebServlet("/ClinicHoursServlet")
public class ClinicHoursServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		String option = request.getParameter("option");
		DataSource datasource = (DataSource) getServletContext().getAttribute("db");
		
		HttpSession session = request.getSession();
		//session.setAttribute("doctorID", "1");		//先放假的
		String doctorID = (String) session.getAttribute("doctorID");
		
		if (option.equals("getClinicHours")) {		//取得門診時間
			response.getWriter().print(ClinicHoursServer.getClinicHours(datasource, doctorID));
		} else if (option.equals("updateClinicHours")) {		//更新門診時間
			String PS = request.getParameter("PS");
			String time = request.getParameter("time");
			String phone = request.getParameter("phone");
			ClinicHoursServer.updateClinicHours(datasource, doctorID, PS, time, phone);
		}
	}
}