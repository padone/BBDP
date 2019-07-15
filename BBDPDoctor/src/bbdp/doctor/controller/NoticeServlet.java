package bbdp.doctor.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.jdbc.pool.DataSource;

import bbdp.doctor.model.NoticeServer;

@WebServlet("/NoticeServlet")
public class NoticeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String option = request.getParameter("option");
		//取得session
    	HttpSession session = request.getSession();		
		String patientID = (String) session.getAttribute("patientID");
		String doctorID = (String) session.getAttribute("doctorID");
		//String doctorID ="1";
		String type = request.getParameter("type");
		String content = request.getParameter("content");
		String doctorNoticeID = request.getParameter("doctorNoticeID");
		String time = request.getParameter("time");
		
		Connection conn = null;
		DataSource datasource = (DataSource) getServletContext().getAttribute("db");
		try {
			conn = datasource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(option.equals("addNoticeItem")){									//新增醫生注意事項
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write(NoticeServer.addNoticeItem(conn, doctorID, type, content));		
		}
		else if(option.equals("editNoticeItem")){							//修改醫生注意事項
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write(NoticeServer.editNoticeItem(conn, doctorNoticeID, doctorID, type, content));		
		}
		else if(option.equals("deleteNoticeItem")){
			NoticeServer.deleteNoticeItem(conn, doctorNoticeID);
		}
		else if(option.equals("searchNoticeType")){							//搜尋類型
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write(NoticeServer.searchNoticeType(conn, doctorID));
		}
		else if(option.equals("getDoctorNitice")){							//取得醫生已新增的注意事項
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write(NoticeServer.getDoctorNitice(conn, doctorID));
		}
		else if(option.equals("getNoticeItem")){							//取得單筆的醫生注意事項
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write(NoticeServer.getNoticeItem(conn, doctorID, doctorNoticeID));
		}
		else if(option.equals("addPatientNotice")){							//新增注意事項給病患
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write(NoticeServer.addPatientNotice(conn, time, patientID, doctorID, content));
		}
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
