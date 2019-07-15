package bbdp.patient.controller;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.jdbc.pool.DataSource;

import com.google.gson.Gson;

import bbdp.patient.model.SettingServer;

/**
 * Servlet implementation class SettingServlet
 */
@WebServlet("/SettingServlet")
public class SettingServlet extends HttpServlet {
	public SettingServlet() {
		super();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		Gson gson = new Gson();
		//連接資料庫
		DataSource datasource = (DataSource) getServletContext().getAttribute("db");
		SettingServer setting = new SettingServer();
		/*******************************************************************************************/
		//設置原本所需參數//PersonalData.html
		String state = request.getParameter("state");
		String patientID = request.getParameter("patientID");
		//設置修改所需參數//PersonalData.html
		String account = request.getParameter("account");
		String password = request.getParameter("password");
		String passwordCheck = request.getParameter("passwordCheck");
		String name = request.getParameter("name");
		String birthday = request.getParameter("birthday");
		//生活作息原本所需參數//Lifestyle.html
		//生活作息修改所需參數//Lifestyle.html
		String getUp = request.getParameter("getUp");
		String breakfast = request.getParameter("breakfast");
		String lunch = request.getParameter("lunch");
		String dinner = request.getParameter("dinner");
		String sleep = request.getParameter("sleep");
		/*******************************************************************************************/
		HashMap accountDefault = new HashMap(); 	//設置原本
		HashMap accountChange = new HashMap(); 		//設置修改
		HashMap lifestyleDefault = new HashMap();	//生活作息原本
		HashMap lifestyleUpdate = new HashMap();	//生活作息修改
		/*******************************************************************************************/
		//設置原本//PersonalData.html
		if (state.equals("Default")) {
			System.out.println("在servlet中的傳入參數 state:"+ state +" patientID:"+patientID);
			accountDefault = setting.settingDefault(datasource, patientID);

			System.out.println("在servlet中的accountInfo : " + accountDefault);
			response.getWriter().write(gson.toJson(accountDefault));
		}
		
		//設置修改//PersonalData.html
		if (state.equals("Change")) {
			String show;
			System.out.println("在servlet中的傳入參數 state:"+ state +" patientID:"+patientID+" password:"+password+" passwordCheck:"+passwordCheck+" name:"+name +" birthday:"+birthday);
			show = setting.settingChange(datasource, patientID, password, passwordCheck, name, birthday);	//修改完畢
			accountChange = setting.settingDefault(datasource, patientID);	//再從db取得修改後的資訊
			accountChange.put("show", show);
			
			System.out.println("在servlet中的accountChange : " + accountChange);
			response.getWriter().write(gson.toJson(accountChange));
		}
		/*******************************************************************************************/
		//生活作息原本//Lifestyle.html
		if (state.equals("lifestyleDefault")) {
			System.out.println("在servlet中的傳入參數 state:"+ state +" patientID:"+patientID);
			lifestyleDefault = setting.lifestyleDefault(datasource, patientID);

			System.out.println("在servlet中的lifestyleDefault : " + lifestyleDefault);
			response.getWriter().write(gson.toJson(lifestyleDefault));
		}
		
		//生活作息修改//Lifestyle.html
		if (state.equals("lifestyleUpdate")) {
			String result;
			System.out.println("在servlet中的傳入參數 state:"+ state +" patientID:"+patientID);
			result = setting.lifestyleUpdate(datasource, patientID, getUp, breakfast, lunch, dinner, sleep);
			lifestyleUpdate = setting.lifestyleDefault(datasource, patientID);
			lifestyleUpdate.put("result", result);

			System.out.println("在servlet中的lifestyleUpdate : " + lifestyleUpdate);
			response.getWriter().write(gson.toJson(lifestyleUpdate));
		}
	}

}
