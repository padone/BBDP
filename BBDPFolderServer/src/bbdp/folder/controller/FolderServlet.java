package bbdp.folder.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.io.IOUtils;
import org.apache.tomcat.jdbc.pool.DataSource;

import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bbdp.folder.model.FolderServer;

@WebServlet("/FolderServlet")
public class FolderServlet extends HttpServlet {
 
    private static final int BUFFER_SIZE = 4096;   
     
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    		throws ServletException, IOException {
        String option = request.getParameter("option");
    	String patientID = request.getParameter("patientID");
        String time = request.getParameter("time"); 
        String videoPath = request.getParameter("videoPath");
        String doctorID = request.getParameter("doctorID");
        //ThreadPoolExecutor屬性
        ThreadPoolExecutor executor = (ThreadPoolExecutor) getServletContext().getAttribute("DeleteVideoExecutor");
        
        //資料庫連線
        DataSource datasource = (DataSource) getServletContext().getAttribute("db");
        Connection conn = null;
		try {
			conn = datasource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
             
        if(option.equals("getAllFileInfo")){				//取得所有檔案資訊
        	response.setContentType("application/json;charset=UTF-8");
        	String photoInfo = FolderServer.getAllFileInfo(conn, patientID);
        	response.getWriter().write(photoInfo);
	    }
        else if(option.equals("getSelectFileInfo")){		//取得特定檔案資訊
        	response.setContentType("application/json;charset=UTF-8");
        	String photoInfo = FolderServer.getSelectFileInfo(conn, patientID, time);
        	response.getWriter().write(photoInfo);
	    }
        else if(option.equals("getPhoto")){					//顯示照片
        	response.setContentType("image/*");
        	
        	InputStream inputStream = null;
        	OutputStream outStream = null;
        	
        	inputStream = FolderServer.getPhoto(conn, patientID, time);
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
        else if(option.equals("getSmallPhoto")){					//顯示縮圖
        	response.setContentType("image/*");
        	
        	InputStream inputStream = null;
        	OutputStream outStream = null;
        	
        	inputStream = FolderServer.getSmallPhoto(conn, patientID, time);
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
        else if(option.equals("getVideo")){		//顯示影片   
        	String videoType = videoPath.substring(videoPath.lastIndexOf(46) + 1, videoPath.length());
        	if(videoType.equals("mov")||videoType.equals("MOV")){
        		response.setContentType("video/quicktime");
        	}
        	else{
        		response.setContentType("video/" + videoPath.substring(videoPath.lastIndexOf(46) + 1, videoPath.length()));
        	}
        	
        	FileInputStream inputStream = null;  	
        	ServletOutputStream outputStream = response.getOutputStream();
        	byte[] buffer = new byte[4096];
            int bytesRead;
            
            String outputType = "firstType";
        	String browserDetails = request.getHeader("User-Agent").toLowerCase();
        	
        	if(browserDetails.indexOf("iphone") >= 0 || browserDetails.indexOf("ipad") >= 0){
        		outputType = "secondType";		//iphone or ipad
        	}
        	else if(browserDetails.indexOf("macintosh") >= 0 && !browserDetails.contains("chrome")){
       		 	outputType = "secondType";		//mac + safari
       	 	}
        	else if (browserDetails.indexOf("windows") >= 0 || browserDetails.toLowerCase().indexOf("android") >= 0){
        		 outputType = "firstType";		//windows or android
        	} 
        	else if(browserDetails.indexOf("macintosh") >= 0 && browserDetails.contains("chrome")){
        		 outputType = "firstType";		//mac + chrome
        	}
        	
        	try {
        		inputStream = FolderServer.getVideo(videoPath);
        		
        		if(outputType.equals("firstType") && inputStream!=null){            		  			
    	    		if(inputStream!=null){
    	        		while ((bytesRead = inputStream.read(buffer)) != -1) {
    	        			outputStream.write(buffer, 0, bytesRead);
    		            }
        			}
            	}
        		else if(outputType.equals("secondType") && inputStream!=null){
        			String range = request.getHeader("Range");
                    if( range != null && !range.equals("bytes=0-")) {
                        String[] ranges = range.split("=")[1].split("-");
                        int from = Integer.parseInt(ranges[0]);
                        int to = Integer.parseInt(ranges[1]);
                        int len = to - from + 1 ;

                        response.setStatus(206);
                        response.setHeader("Accept-Ranges", "bytes");
                        File f = new File(videoPath);
                        String responseRange = String.format("bytes %d-%d/%d", from, to, f.length());
                        response.setHeader("Connection", "close");
                        response.setHeader("Content-Range", responseRange);
                        response.setDateHeader("Last-Modified", new Date().getTime());
                        response.setContentLength(len);

                        byte[] buf = new byte[4096];
                        inputStream.skip(from);
                        while( len != 0) {
                            int read = inputStream.read(buf, 0, len >= buf.length ? buf.length : len);
                            if( read != -1) {
                            	outputStream.write(buf, 0, read);
                                len -= read;
                            }
                        }
                    }
        		}
    		}
    		catch (FileNotFoundException e) {
    			System.out.println("發生FileNotFoundException : " + e);
    		}
        	finally{
        		if(inputStream!=null){
        			inputStream.close();
        			System.out.println("顯示影片inputStream.close()");
        		}
        		if (outputStream != null) {
        			outputStream.flush();
        			outputStream.close();
        			System.out.println("顯示影片outStream.close()");
        		}
        		if (conn!=null) try {conn.close();}catch (Exception ignore) {}
        	}               	
        }       
        else if(option.equals("deletePhoto")){			 //刪除照片
        	response.setContentType("text/html;charset=UTF-8");
        	String deleteResult = "";      	
        	deleteResult = FolderServer.deletePhoto(conn, patientID, time);
        	response.getWriter().write(deleteResult);
        }
        else if(option.equals("deleteVideo")){  	   //刪除影片
        	response.setContentType("text/html;charset=UTF-8");
	       	response.getWriter().write(FolderServer.deletePhoto(conn, patientID, time));
	       	
	       	executor.execute(new Runnable() {
	             @Override
	             public void run() {
	            	File file = new File(videoPath);
	         		if (!file.exists()) {
	         		    System.out.println("影片不存在");
	         		} 
	         		else {
	         			System.out.println("刪除影片中...");
	         			while(!file.delete()){}
	         			System.out.println("已刪除"+videoPath);
	         		}
	             }
	         });
       }  
       else if(option.equals("editPhoto")){				 //編輯照片
        	String description = request.getParameter("description");
        	response.setContentType("text/html;charset=UTF-8");
        	String editResult = "";        	
        	editResult = FolderServer.editPhoto(conn, patientID, time, description);
        	response.getWriter().write(editResult);
       }
       else if(option.equals("getDoctorFileInfo")){		//特定醫生檔案列表
           	response.setContentType("application/json;charset=UTF-8");
           	String jsonString = FolderServer.getDoctorFileInfo(conn, patientID, doctorID);
           	response.getWriter().write(jsonString);
       }
       
        
       if (conn!=null) try {conn.close();}catch (Exception ignore) {}

    }
}