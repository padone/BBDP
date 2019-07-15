package bbdp.doctor.controller;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebServlet;

@WebServlet("/InstructionFroalaServletListener")
public class InstructionFroalaServletListener implements ServletContextListener{
	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext servletContext = event.getServletContext();
		String filePath = servletContext.getRealPath("/");
		String InstructionFroalaPath = filePath + "Instruction";
		File path = new File(InstructionFroalaPath);

		if (!path.exists()) {
			path.mkdirs();
		}

		System.out.println("InstructionFroala is located here: " + InstructionFroalaPath);
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	
	}
}