package bbdp.push.fcm;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.jdbc.pool.DataSource;

@WebServlet("/PushTimerTest")
public class PushTimerTest extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		DataSource datasource = (DataSource) getServletContext().getAttribute("db");
		System.out.println("in PushTimerTest");
		PushTimerServer.setPushTimer(datasource, 3, 3, 3, "1000", "測試title", "測試body", "Setting.html", "dbtableName", "dbIDName", "dbIDValue");		//30秒一次，共3次(3, 3, 3), 3分一次，共3次(4, 3, 3)
		//PushTimerServer.setPushTimer(datasource, "2017-07-14 10:44:00", "1000", "測試title", "測試body", "Setting.html", "dbtableName", "dbIDName", "dbIDValue");
	}
}