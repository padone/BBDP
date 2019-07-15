package bbdp.patient.controller;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import bbdp.patient.model.SystemServer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

@WebServlet("/SystemServlet")
public class SystemServlet extends HttpServlet {
	public SystemServlet() {
		super();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		Gson gson = new Gson();
		//連接資料庫
		DataSource datasource = (DataSource) getServletContext().getAttribute("db");
		SystemServer system = new SystemServer();
		/*******************************************************************************************/
		String state = request.getParameter("state");

		String patientID = request.getParameter("patientID");
		/*******************************************************************************************/
		/*******************************************************************************************/
		// 更新system patients
		if(state.equals("updatePatientsAddNew")){
			String newPatients = system.newpatients(datasource, patientID);
			response.getWriter().write(gson.toJson(system.updatePatients(datasource, patientID, newPatients)));
		}
		// 判斷該病患ID是否在patients裡面
		if(state.equals("isInPatients")){
			response.getWriter().write(gson.toJson(system.isInPatients(datasource, patientID)));
		}
		// 移除病患ID
		if(state.equals("removePatientID")){
			response.getWriter().write(gson.toJson(system.removePatientID(datasource, patientID)));
		}
		// 更新優先順序至最新	// 先刪掉在新增，就會變成最新的了
		if(state.equals("updatePatientsToFirst")){
			String firstPatients = system.firstPatients(datasource, patientID);			
			response.getWriter().write(gson.toJson(system.updatePatients(datasource, patientID, firstPatients)));
		}
	}
}
