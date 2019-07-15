package bbdp.doctor.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.json.JSONException;
import org.json.JSONObject;

import bbdp.doctor.model.NotificationServer;
import bbdp.doctor.model.PatientBasicInformationServer;

@WebServlet("/NotificationServlet")
public class NotificationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		String option = request.getParameter("option");
		DataSource datasource = (DataSource) getServletContext().getAttribute("db");
		
		HttpSession session = request.getSession();
		//session.setAttribute("doctorID", "1");		//先放假的
		String doctorID = (String) session.getAttribute("doctorID");
		
		if (option.equals("getDoctorID")) {
			response.getWriter().print(doctorID);
		} else if (option.equals("getPatientName")) {		//取得某病患姓名
			String pID = request.getParameter("patientID");
			response.getWriter().print(PatientBasicInformationServer.getPatientName(datasource, pID));
		} else if (option.equals("getAllNotification")) {		//取得該醫生所有通知 
			response.getWriter().print(NotificationServer.getNotification(datasource, doctorID));
		} else if (option.equals("clearAllNotification")) { 	//刪除該醫生所有通知
			NotificationServer.clearAllNotification(datasource, doctorID);
		} else if (option.equals("newClinicPush")) {		//新增診間推播通知
			String message = request.getParameter("message");
			String dID = null;
			String pID = null;
			try {
				JSONObject obj = new JSONObject(message);
				dID = obj.getString("doctorID");
				pID = obj.getString("patientID");
			} catch (JSONException e) {
				System.out.println("NotificationServlet newClinicPush JSONObject exception");
			}
			//存通知
			NotificationServer.newClinicPush(datasource, dID, pID);
			//取得病患姓名
			response.getWriter().print(PatientBasicInformationServer.getPatientName(datasource, pID));
		} else if (option.equals("newRemindPush")) {		//新增提醒推播通知(問卷、檔案夾)
			String message = request.getParameter("message");
			String dID = null;
			String pID = null;
			String title = null;
			String body = null;
			String hyperlink = null;
			try {
				JSONObject obj = new JSONObject(message);
				dID = obj.getString("doctorID");
				pID = obj.getString("patientID");
				title = obj.getString("title");
				body = obj.getString("body");
				hyperlink = obj.getString("hyperlink");
			} catch (JSONException e) {
				System.out.println("NotificationServlet newRemindPush JSONObject exception");
			}
			//存通知
			NotificationServer.newRemindPush(datasource, dID, pID, title, body, hyperlink);
			//取得病患姓名
			response.getWriter().print(PatientBasicInformationServer.getPatientName(datasource, pID));
		}  else if (option.equals("clickNotification")) {		//點擊通知
			session.setAttribute("patientID", request.getParameter("patientID"));
		} else if (option.equals("getNotificationSetting")) {		//取得通知設定
			response.getWriter().print(NotificationServer.getNotificationSetting(datasource, doctorID));
		} else if (option.equals("modifyNotificationSetting")) {		//修改通知設定
			String notification = request.getParameter("notification");
			NotificationServer.modifyNotificationSetting(datasource, doctorID, notification);
		}
	}
}