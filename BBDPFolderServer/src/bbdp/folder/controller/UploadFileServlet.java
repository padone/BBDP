package bbdp.folder.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import bbdp.folder.model.FolderServer;

@WebServlet("/UploadFileServlet")
public class UploadFileServlet extends HttpServlet {
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		Connection conn = null;
		if(isMultipart){
			try {
				//List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
				
				//Create a factory for disk-based file items 
				FileItemFactory factory = new DiskFileItemFactory(); 
				
				//Create a new file upload handler 
				ServletFileUpload upload = new ServletFileUpload(factory); 
								
				//Parse the request 
				List <FileItem> items = upload.parseRequest(request);
				
				DataSource datasource = (DataSource) getServletContext().getAttribute("db");	
				conn = datasource.getConnection();
				
				FolderServer.uploadPhoto(items, conn);
			} 
			catch (FileUploadException e) {
				System.out.println("發生FileUploadException");
				e.printStackTrace();
			}
			catch (SQLException e) {
				e.printStackTrace();
			} 
			finally {
			      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
			}	
		}
	}

}