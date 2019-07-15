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

import bbdp.doctor.model.AccountSettingServer;


@WebServlet("/AccountSettingServlet")
public class AccountSettingServlet extends HttpServlet {
    public AccountSettingServlet() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		Gson gson = new Gson();

		HttpSession session = request.getSession();	
		String doctorID = (String) session.getAttribute("doctorID");
		// 設置原本所需參數
		String state = request.getParameter("state");
		// 設置修改所需參數
		String account = request.getParameter("account");
		String password = request.getParameter("password");
		String passwordCheck = request.getParameter("passwordCheck");
		String name = request.getParameter("name");
		String hospital = request.getParameter("hospital");
		String department = request.getParameter("department");

		HashMap result = new HashMap(); // 設置結果
		HashMap accountDefault = new HashMap(); // 設置原本
		HashMap accountChange = new HashMap(); // 設置修改
		AccountSettingServer setting = new AccountSettingServer();
		
		DataSource datasource = (DataSource) getServletContext().getAttribute("db");//取得存在context的datasource
		
		// 設置原本
		if (state.equals("Default")) {
			accountDefault = setting.settingDefault(datasource, doctorID);
			response.getWriter().write(gson.toJson(accountDefault));
		}
		
		// 設置修改
		if (state.equals("Change")) {
			String show;
			show = setting.settingChange(datasource, doctorID, password, passwordCheck);	//修改完畢
			accountChange = setting.settingDefault(datasource, doctorID);	//再從db取得修改後的資訊
			accountChange.put("show", show);
			response.getWriter().write(gson.toJson(accountChange));
		}	
		
		// 設置修改
		if (state.equals("Change2")) {
			String show;
			show = setting.settingChange2(datasource, doctorID, name, hospital, department);	//修改完畢
			accountChange = setting.settingDefault(datasource, doctorID);	//再從db取得修改後的資訊
			accountChange.put("show", show);
			
			System.out.println("在servlet中的accountChange : " + accountChange);
			response.getWriter().write(gson.toJson(accountChange));
		}
	}
}
