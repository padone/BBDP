package bbdp.folder.controller;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class UploadVideoContextListener implements ServletContextListener{
	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext sc = event.getServletContext();
		String appRootPath = sc.getInitParameter("appRootPath");
		sc.setAttribute("videoRootPath", sc.getRealPath(appRootPath));

		System.out.println("檔案夾影片存放路徑 : " + sc.getRealPath(appRootPath));		
		
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		sc.setAttribute("DeleteVideoExecutor", executor);
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		ThreadPoolExecutor executor = (ThreadPoolExecutor) servletContextEvent  
                .getServletContext().getAttribute("executor");  
        executor.shutdown();
	}
}
