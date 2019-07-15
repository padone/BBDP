package bbdp.patient.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import bbdp.patient.model.LoginVerification;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

@WebServlet("/LoginVerificationServlet")
public class LoginVerificationServlet extends HttpServlet {
	public LoginVerificationServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		//super.doGet(req, resp);
		// NOTICE : this doGet function is for connection testing
		DataSource d = (DataSource) getServletContext().getAttribute("db");
		resp.getWriter().write("get method connected\n");
		try {
			Connection conn = d.getConnection();
			if(!conn.isClosed()){
				resp.getWriter().write("database connected");
				// print random sql statement result to check connection
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT patientID FROM healthtracking WHERE healthTrackingID = 1");
				//ResultSet r = stmt.executeQuery("SELECT min(patientID) from  healthtracking");
				//int r2 = stmt.executeUpdate("insert into healthtracking (healthTrackingID, patientID, itemID)"+"values (111, 222, 'sword')");
				PrintWriter out = resp.getWriter();

				while(rs.next()){
					out.println("\n" + rs.getString("patientID"));
				}/*
				while(r.next()){
					out.println("\n" + r.getInt(1));
				}*/
			}else{
				resp.getWriter().write("connection failed");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		/*
		response.getWriter().write("doPost() method connected");
		System.out.println("doPost() method connected");
		*/
		Gson gson = new Gson();
		//連接資料庫
		DataSource datasource = (DataSource) getServletContext().getAttribute("db");
		LoginVerification login = new LoginVerification();
		/*******************************************************************************************/
		String state = request.getParameter("state");
		String account = request.getParameter("account");
		String password = request.getParameter("password");
		String uuid = request.getParameter("uuid");

		String patientID = request.getParameter("patientID");
		/*******************************************************************************************/
		/*******************************************************************************************/
		// 登入驗證
		if(state.equals("login")){
			response.getWriter().write(gson.toJson(login.verification(datasource,account, password, uuid)));
		}
		// 判斷登入
		if(state.equals("judgeLogin")){
			response.getWriter().write(gson.toJson(login.judgeLogin(datasource, patientID, uuid)));
		}
		// 更新uuid
		if(state.equals("updateUUID")){
			response.getWriter().write(gson.toJson(login.updateUUID(datasource, patientID, uuid)));
		}


	}
}
