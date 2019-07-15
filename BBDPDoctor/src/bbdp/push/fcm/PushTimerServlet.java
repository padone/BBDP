package bbdp.push.fcm;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.jdbc.pool.DataSource;

@WebServlet("/PushTimerServlet")
public class PushTimerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		String option = request.getParameter("option");
		DataSource datasource = (DataSource) getServletContext().getAttribute("db");
		
		if (option.equals("setPushTimer1")) {		//設定推播計時器(type: 0-天, 1-週, 2-月, 3-秒*10(測試), 4-分(測試))(EX: 三(rate)天(type)一次，共三(times)次)
			int type = Integer.parseInt(request.getParameter("type"));
			int rate = Integer.parseInt(request.getParameter("rate"));
			int times = Integer.parseInt(request.getParameter("times"));
			String patientID = request.getParameter("patientID");
			String title = request.getParameter("title");
			String body = request.getParameter("body");
			String hyperlink = request.getParameter("hyperlink");
			String dbtableName = request.getParameter("dbtableName");
			String dbIDName = request.getParameter("dbIDName");
			String dbIDValue = request.getParameter("dbIDValue");
			PushTimerServer.setPushTimer(datasource, type, rate, times, patientID, title, body, hyperlink, dbtableName, dbIDName, dbIDValue);
		} else if (option.equals("setPushTimer2")) {		//time格式: yyyy-mm-dd hh:mm:ss
			String time = request.getParameter("time");
			String patientID = request.getParameter("patientID");
			String title = request.getParameter("title");
			String body = request.getParameter("body");
			String hyperlink = request.getParameter("hyperlink");
			String dbtableName = request.getParameter("dbtableName");
			String dbIDName = request.getParameter("dbIDName");
			String dbIDValue = request.getParameter("dbIDValue");
			PushTimerServer.setPushTimer(datasource, time, patientID, title, body, hyperlink, dbtableName, dbIDName, dbIDValue);
		} else if(option.equals("deleteSpecificPushTimer")) {		//刪除指定的timerTask
			String patientID = request.getParameter("patientID");
			String dbtableName = request.getParameter("dbtableName");
			String dbIDName = request.getParameter("dbIDName");
			String dbIDValue = request.getParameter("dbIDValue");
			PushTimerServer.deleteSpecificPushTimer(datasource, patientID, dbtableName, dbIDName, dbIDValue);
		} else if(option.equals("restartPushTimer")) {		//重新啟動timer
			PushTimerServer.restartPushTimer(datasource);
		}
	}
}