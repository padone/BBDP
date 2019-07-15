package bbdp.doctor.controller;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import com.google.gson.Gson;

import bbdp.doctor.model.LoginVerification;

@WebServlet("/LoginVerificationServlet")
public class LoginVerificationServlet extends HttpServlet {
	public LoginVerificationServlet() {
		super();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");

		String state = request.getParameter("state");
		String account = request.getParameter("account");
		String password = request.getParameter("password");

		Gson gson = new Gson();
		HashMap loginResult = new HashMap();	// 登入結果

		DataSource datasource = (DataSource) getServletContext().getAttribute("db");

		LoginVerification login = new LoginVerification();
		
		// 登入
		if(state.equals("login")){
			loginResult = login.verification(datasource, account, password);
	
			//如果帳密正確，則把docterID放入session裡
			if(loginResult.get("result").equals("登入成功")){
				HttpSession session = request.getSession();
				session.setAttribute("doctorID", loginResult.get("doctorID"));
			}
			// 回傳json型態
			System.out.println("LoginVerification response in servlet" + loginResult);
			response.getWriter().write(gson.toJson(loginResult));
		}
		
		//驗證是否登入
		if(state.equals("judgeLogin")){
			Boolean judgeLogin = false;
			HttpSession session = request.getSession();	
			String doctorID = (String) session.getAttribute("doctorID");
			if(doctorID != null){	//已登入
				judgeLogin = true;
			}
			else{	//尚未登入
				judgeLogin = false;
			}
			response.getWriter().write(gson.toJson(judgeLogin));
		}
	}
}
