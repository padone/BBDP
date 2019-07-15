package bbdp.doctor.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.jdbc.pool.DataSource;

import bbdp.doctor.model.DoctorProfilePictureServer;

@WebServlet("/DoctorProfilePictureServlet")
public class DoctorProfilePictureServlet extends HttpServlet {
	private static final int BUFFER_SIZE = 4096;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();	
		String doctorID = (String) session.getAttribute("doctorID");
		
		String option = request.getParameter("option");
		
		//System.out.println("doctorID : " + doctorID);
		//System.out.println("option : " + option);
		
		Connection conn = null;
		DataSource datasource = (DataSource) getServletContext().getAttribute("db");	
		try {
			conn = datasource.getConnection();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		if(option.equals("getDoctorID")){
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write(doctorID);
		}
		else if(option.equals("getDoctorProfilePicture")){		//取得大頭貼
			doctorID = request.getParameter("doctorID");
			
			response.setContentType("image/*");
        	
        	InputStream inputStream = null;
        	OutputStream outStream = null;
        	
        	inputStream = DoctorProfilePictureServer.getDoctorProfilePicture(conn, doctorID);
        	outStream = response.getOutputStream();
	        	
        	byte[] buffer = new byte[BUFFER_SIZE];
        	int bytesRead = -1;
        	
	        if(inputStream!=null){  
	        	while ((bytesRead = inputStream.read(buffer)) != -1) {
	        		outStream.write(buffer, 0, bytesRead);
	        	}
	        }
        	if (inputStream != null){
        		inputStream.close(); 
        	}
        	if (outStream != null) {
        		outStream.close();
        	}  
		}
		else if(option.equals("uploadProfilePicture")){	//上傳大頭貼
			boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			if(isMultipart){
				try {
					FileItemFactory factory = new DiskFileItemFactory(); 
					
					ServletFileUpload upload = new ServletFileUpload(factory); 
									
					List <FileItem> items = upload.parseRequest(request);
			
					DoctorProfilePictureServer.uploadProfilePicture(items, conn,doctorID);
				} 
				catch (FileUploadException e) {
					System.out.println("上傳大頭貼時發生FileUploadException");
					e.printStackTrace();
				}
				finally{
					if (conn!=null) try {conn.close();}catch (Exception ignore) {}
				}
			}			
		}
		if (conn!=null) try {conn.close();}catch (Exception ignore) {}
	}

}
