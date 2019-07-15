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

import bbdp.patient.model.ForgotPasswordServer;

@WebServlet("/ForgotPasswordServlet")
public class ForgotPasswordServlet extends HttpServlet {
    public ForgotPasswordServlet() {
        super();
    }

    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		Gson gson = new Gson();	
		//連接資料庫
		DataSource datasource = (DataSource) getServletContext().getAttribute("db");
		ForgotPasswordServer forgot = new ForgotPasswordServer();

		String account = request.getParameter("account");
		String birthday = request.getParameter("birthday");
		
		response.getWriter().write(gson.toJson(forgot.forgotPassword(datasource, account, birthday)));	//回傳
	}
}
