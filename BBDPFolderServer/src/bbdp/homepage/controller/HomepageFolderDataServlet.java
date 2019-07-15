package bbdp.homepage.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.tomcat.jdbc.pool.DataSource;

import bbdp.homepage.model.HomepageFolderDataServer;

@WebServlet("/HomepageFolderDataServlet")
public class HomepageFolderDataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		DataSource datasource = (DataSource) getServletContext().getAttribute("db");
		String doctorID = request.getParameter("doctorID");
		response.getWriter().print(HomepageFolderDataServer.getHomepageFolderData(datasource, doctorID));
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}