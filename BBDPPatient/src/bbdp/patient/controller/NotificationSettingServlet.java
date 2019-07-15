package bbdp.patient.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.jdbc.pool.DataSource;

import bbdp.patient.model.NotificationSettingServer;

@WebServlet("/NotificationSettingServlet")
public class NotificationSettingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		String option = request.getParameter("option");
		DataSource datasource = (DataSource) getServletContext().getAttribute("db");
		String patientID = request.getParameter("patientID");
		
		if (option.equals("getNotificationSetting")) {		//取得通知設定
			response.getWriter().print(NotificationSettingServer.getNotificationSetting(datasource, patientID));
		} else if (option.equals("modifyNotificationSetting")) {		//修改通知設定
			String notification = request.getParameter("notification");
			NotificationSettingServer.modifyNotificationSetting(datasource, patientID, notification);
		}
	}
}