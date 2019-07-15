package bbdp.patient.controller;

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

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.jdbc.pool.DataSource;

import bbdp.patient.model.ProfilePictureServer;

@WebServlet("/ProfilePictureServlet")
public class ProfilePictureServlet extends HttpServlet {
	private static final int BUFFER_SIZE = 4096;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		//資料庫連線
		Connection conn = null;
		DataSource datasource = (DataSource) getServletContext().getAttribute("db");	
		try {
			conn = datasource.getConnection();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		String option = request.getParameter("option");
		String patientID = request.getParameter("patientID");
		
		//System.out.println("ProfilePictureServlet 參數option："+option+" patientID : "+patientID);
		
		if(option.equals("uploadProfilePicture")){	//上傳大頭貼
			boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			if(isMultipart){
				try {
					FileItemFactory factory = new DiskFileItemFactory(); 
					
					ServletFileUpload upload = new ServletFileUpload(factory); 
									
					List <FileItem> items = upload.parseRequest(request);
					
					ProfilePictureServer.uploadProfilePicture(items, conn);
				} 
				catch (FileUploadException e) {
					System.out.println("發生FileUploadException");
					e.printStackTrace();
				}
			}			
		}
		else if(option.equals("getProfilePicture")){		//取得大頭貼
			
			response.setContentType("image/*");
        	
        	InputStream inputStream = null;
        	OutputStream outStream = null;
        	
        	inputStream = ProfilePictureServer.getProfilePicture(conn, patientID);
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
		
		if (conn!=null) try {conn.close();}catch (Exception ignore) {}
	}

}
