package bbdp.patient.controller;

import java.io.*;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import com.google.gson.Gson;

import bbdp.patient.model.RegisterServer;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
	public RegisterServlet() {
		super();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		Gson gson = new Gson();
		//連接資料庫
		DataSource datasource = (DataSource) getServletContext().getAttribute("db");
		RegisterServer register = new RegisterServer();
		/*******************************************************************************************/
		String account = request.getParameter("account");
		String password = request.getParameter("password");
		String passwordCheck = request.getParameter("passwordCheck");
		String name = request.getParameter("name");
		String birthday = request.getParameter("birthday");
		String agree = request.getParameter("agree");
		/*******************************************************************************************/
		HashMap registerAdd = new HashMap();						// 新增紀錄結果
		/*******************************************************************************************/

		registerAdd = register.registerAdd(datasource, account, password, passwordCheck, name, birthday, agree);
		System.out.println("在servlet中的registerAdd: " + registerAdd);

		response.getWriter().write(gson.toJson(registerAdd));
	}
}
