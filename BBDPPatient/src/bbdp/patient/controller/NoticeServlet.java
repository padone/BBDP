package bbdp.patient.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.jdbc.pool.DataSource;

import bbdp.patient.model.FamilyServer;
import bbdp.patient.model.NoticeServer;

@WebServlet("/NoticeServlet")
public class NoticeServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");
		//收到的參數
		String option = request.getParameter("option");
		String time = request.getParameter("time");
		String patientID = request.getParameter("patientID");
		String content = request.getParameter("content");
		String noticeID = request.getParameter("noticeID");
		String doctorID = request.getParameter("doctorID");
		String senderID = request.getParameter("senderID");
		
		Connection conn = null;
		DataSource datasource = (DataSource) getServletContext().getAttribute("db");
		try {
			conn = datasource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(option.equals("addPatientNotice")){									//病患新增注意事項
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write(NoticeServer.addPatientNotice(conn, time, patientID, content));
		}
		else if(option.equals("addFamilyNotice")){								//家屬新增注意事項
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write(NoticeServer.addFamilyNotice(conn, time, patientID, senderID, content));
		}
		else if(option.equals("deleteNoticeItem")){								//刪除注意事項
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write(NoticeServer.deleteNoticeItem(conn, noticeID));
		}
		else if(option.equals("getPatientNitice")){								//取得所有病患注意事項
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write(NoticeServer.getPatientNitice(conn, patientID));
		}
		else if(option.equals("getDoctorName")){								//取得醫生姓名
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write(NoticeServer.getDoctorName(conn, doctorID));
		}
		else if(option.equals("getFamilyName")){								//取得家屬姓名
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write(FamilyServer.getFamilyName(conn, patientID, senderID));
		}
		
		if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		
	}


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
