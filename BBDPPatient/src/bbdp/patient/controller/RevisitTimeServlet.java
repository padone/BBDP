package bbdp.patient.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.jdbc.pool.DataSource;

import bbdp.patient.model.RevisitTimeServer;

@WebServlet("/RevisitTimeServlet")
public class RevisitTimeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		String option = request.getParameter("option");
		DataSource datasource = (DataSource) getServletContext().getAttribute("db");
		String patientID = request.getParameter("patientID");
		
		if(option.equals("getLatestRevisitTime")) {		//取得最新的回診時間
			response.getWriter().print(RevisitTimeServer.getLatestRevisitTime(datasource, patientID));
		} else if(option.equals("getRevisitTimeList")) {		//取得回診時間列表
			response.getWriter().print(RevisitTimeServer.getRevisitTimeList(datasource, patientID));
		} else if(option.equals("deleteRevisitTime")) {		//刪除指定的回診時間
			String date = request.getParameter("date");
			int timePeriod = Integer.valueOf(request.getParameter("timePeriod"));
			response.getWriter().print(RevisitTimeServer.deleteRevisitTime(datasource, patientID, date, timePeriod));
		} else if(option.equals("getHospital")) {		//取得所有醫院和科別資料
			response.getWriter().print(RevisitTimeServer.getHospital(datasource));
		} else if(option.equals("checkRevisitTimeIsRepeat")) {		//檢查是否有重複新增同一日同一時段
			String date = request.getParameter("date");
			int timePeriod = Integer.valueOf(request.getParameter("timePeriod"));
			response.getWriter().print(RevisitTimeServer.checkRevisitTimeIsRepeat(datasource, patientID, date, timePeriod));
		} else if(option.equals("newRevisitTime")) {		//新增回診時間
			String inputJSONString = request.getParameter("inputJSONString");
			response.getWriter().print(RevisitTimeServer.newRevisitTime(datasource, inputJSONString));
			//新增回診時間時，加入推播
			//RevisitTimeServer.revisitTimePush(datasource, inputJSONString);
		}
	}
}