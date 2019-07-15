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

import bbdp.patient.model.GetInfoServer;

@WebServlet("/GetInfoServlet")
public class GetInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public GetInfoServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String option = request.getParameter("option");
		String patientID = request.getParameter("patientID");
		//資料庫連線
        DataSource datasource = (DataSource) getServletContext().getAttribute("db");
        Connection conn = null;
		try {
			conn = datasource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(option.equals("searchHospital")){	//上傳用
			response.setContentType("application/json;charset=UTF-8");
			String departmentInfo = GetInfoServer.searchHospital(conn);
			response.getWriter().write(departmentInfo);	   
		}
		else if(option.equals("searchDepartment")){	//上傳用
			String hospital = request.getParameter("hospital");
			response.setContentType("application/json;charset=UTF-8");
			String departmentInfo = GetInfoServer.searchDepartment(conn, hospital);
			response.getWriter().write(departmentInfo);	   
		}
		else if(option.equals("searchDoctor")){		//上傳用
			String hospital = request.getParameter("hospital");
			String department = request.getParameter("department");
			response.setContentType("application/json;charset=UTF-8");
			String doctorInfo = GetInfoServer.searchDoctor(conn, hospital, department);
			response.getWriter().write(doctorInfo);	   
		}
		else if(option.equals("getPatientName")){	//回傳病患姓名
			response.setContentType("text/html;charset=UTF-8");
			String patientName = GetInfoServer.getPatientName(conn, patientID);
			response.getWriter().write(patientName);
		}
		
		if (conn!=null) try {conn.close();}catch (Exception ignore) {}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
