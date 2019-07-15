package bbdp.doctor.controller;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebServlet;

@WebServlet("/QuestionImageServletListener")
public class QuestionImageServletListener implements ServletContextListener{
	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext servletContext = event.getServletContext();
		String imagePath = servletContext.getRealPath("/");
		String questionImagePath = imagePath + "QuestionImage";
		File path = new File(questionImagePath);

		if (!path.exists()) {
			path.mkdirs();
		}

		System.out.println("QuestionImage is located here: " + questionImagePath);
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	
	}
}