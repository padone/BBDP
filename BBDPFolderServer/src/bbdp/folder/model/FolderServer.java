package bbdp.folder.model;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

public class FolderServer {
	//上傳圖片
	static public void uploadPhoto(List<FileItem> items, Connection conn) throws IOException{
		String time = "";
		String patientID = "";
		String doctorID = "";
		String description = "";
		String doctorInfoJsonString = "";
		
		File previewPicture = new File("previewPicture");
		
		for (FileItem item : items) {
			if (item.isFormField()) {
				String fieldName = item.getFieldName();
				String fieldValue = item.getString("UTF-8");
				if(fieldName.equals("time")){
					time = fieldValue;
					//System.out.println("上傳參數time : "+time);    
				}
				else if(fieldName.equals("patientID")){
					patientID = fieldValue; 
					//System.out.println("上傳參數patientID : "+patientID);
				}
				else if(fieldName.equals("doctorID")){
					doctorID = fieldValue; 
					//System.out.println("上傳參數doctorID : "+doctorID);
				}
				else if(fieldName.equals("description")){
					description = fieldValue; 
				        //System.out.println("上傳參數description : "+description);
				}
				else if(fieldName.equals("doctorInfoJsonString")){
					doctorInfoJsonString = fieldValue; 
					System.out.println("上傳參數doctorInfoJsonString : "+doctorInfoJsonString);
				}
			} 
			else {
				//String fieldName = item.getFieldName();
				//System.out.println("上傳參數fieldName : "+fieldName);
					
				InputStream imageStream = item.getInputStream();
				BufferedImage image = javax.imageio.ImageIO.read(imageStream); 
				BufferedImage newImage = FolderServer.scaleImage(image, 100, 0);
				ImageIO.write(newImage, "JPG", previewPicture);
					
		        InputStream is = item.getInputStream();	//原圖
		        FileInputStream fis = new FileInputStream(previewPicture);	//縮圖
		            
		        System.out.println("存doctorInfoJsonString到資料庫 : "+doctorInfoJsonString);
		        System.out.println("存time到資料庫 : "+time);
				//存圖片到資料庫
		        saveFileInfoToDB(conn, is, fis, time, patientID, doctorID, "", description, doctorInfoJsonString);
					
				if (imageStream != null){
					imageStream.close(); 
		       	}
			}
		}
	}
		
	//上傳影片
	static public void uploadVideo(List<FileItem> items, Connection conn ,String videoRootPath) throws IOException{
		String time = "";
		String patientID = "";
		String doctorID = "";
		String description = "";
		String videoFormat = "";
		String doctorInfoJsonString = "";
		
		for (FileItem item : items) {
			if (item.isFormField()) {
		        String fieldName = item.getFieldName();
		        String fieldValue = item.getString("UTF-8");
		        if(fieldName.equals("time")){
		        	time = fieldValue;
		        }
		        else if(fieldName.equals("patientID")){
		        	patientID = fieldValue; 
		        }
		        else if(fieldName.equals("doctorID")){
		        	doctorID = fieldValue; 
		        }
		        else if(fieldName.equals("description")){
		        	description = fieldValue; 
		        }
		        else if(fieldName.equals("videoFormat")){
		        	videoFormat = fieldValue; 
		        }
		        else if(fieldName.equals("doctorInfoJsonString")){
					doctorInfoJsonString = fieldValue; 
					System.out.println("上傳參數doctorInfoJsonString : "+doctorInfoJsonString);
				}
		    } 
			else {
				//String fileName = URLDecoder.decode(item.getName());	//原本的檔名
				//檔名
				String fileName = time;
				fileName = fileName.replaceAll("-", "");
				fileName = fileName.replaceAll(":", "");
				fileName = fileName.replaceAll(" ", "");
				fileName = fileName + "." + videoFormat;
				
				String videoPath = videoRootPath + "\\" + patientID ;		//寫入的目錄							
				videoPath = videoPath.replaceAll("\\\\", "/");
				
				File file = new File(videoPath, fileName);
				File parentFile = file.getParentFile();

				if (file.getParentFile() != null) {
					parentFile.mkdirs();
				}

				try {
					item.write(file);
					System.out.println("寫檔完成");
				} 
				catch (Exception e) {
					System.out.println("寫檔失敗");
				}
				//影片縮圖
				FileInputStream fis = randomGrabberFFmpegImage(videoPath + "/" + fileName);
		        //存影片資訊到資料庫
				saveFileInfoToDB(conn , null, fis , time, patientID, doctorID, videoPath + "/" + fileName, description, doctorInfoJsonString);
		    }
		}
	}
	
	//擷取影片
	public static FileInputStream randomGrabberFFmpegImage(String filePath) {
		FFmpegFrameGrabber ff = new FFmpegFrameGrabber(filePath);
		FileInputStream fis = null;
		double angle = 0;
		try {
			ff.start();
			
			if(ff.getVideoMetadata("rotate") != null){
				angle = Double.parseDouble(ff.getVideoMetadata("rotate"));
			}
			
			int ffLength = ff.getLengthInFrames();
			int randomSize = 1;
			List<Integer> randomGrab = random(ffLength, randomSize);
			int maxRandomGrab = randomGrab.get(randomGrab.size() - 1);
			
			Frame f = null;
			int i = 0;
			while (i < ffLength) {
				f = ff.grabImage();	
				if (randomGrab.contains(i)) {
					fis = doExecuteFrame(f, angle);
				}
				if (i >= maxRandomGrab) {
					break;
				}
				i++;
			}
		
			ff.stop();
		}
		catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
			System.out.println("影片縮圖發生Exception："+e);
		}
		
		return fis;
	}
	
	//產生縮圖
	public static FileInputStream doExecuteFrame(Frame f, double angle) {
		File output = new File("videoPreview");
		FileInputStream imageStream = null;
		
		if (null == f || null == f.image) {
			return null;
		}
		
		Java2DFrameConverter converter = new Java2DFrameConverter();

		BufferedImage image = converter.getBufferedImage(f);
		try {
			//原圖
			ImageIO.write(image, "JPG", output);
			imageStream = new FileInputStream(output);
			image = javax.imageio.ImageIO.read(imageStream);
			//縮圖
			image = FolderServer.scaleImage(image, 100, angle);
			ImageIO.write(image, "JPG", output);
			imageStream = new FileInputStream(output);			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return imageStream;
	}
	
	//產生亂數
	public static List<Integer> random(int baseNum, int length) {
		List<Integer> list = new ArrayList<>(length);
		while (list.size() < length) {
			Integer next = (int) (Math.random() * baseNum);
			if (list.contains(next)) {
				continue;
			}
			list.add(next);
		}
		Collections.sort(list);
		return list;
	}
	
	//存檔案資訊到資料庫
	static private void saveFileInfoToDB(Connection conn, InputStream is, FileInputStream fis, 
			String time, String patientID, String doctorID, String video, String description, String jsonString){
		
		PreparedStatement ps = null;
		
		try {			
			ps = conn.prepareStatement("INSERT INTO file VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
			ps.setString(1, time);			//time
			ps.setString(2, patientID);		//病患
			ps.setString(3, doctorID);		//醫生
			ps.setBlob(4, is);				//原圖
			ps.setBlob(5, fis);				//縮圖
			ps.setString(6, video);			//影片
			ps.setString(7, description);	//描述
			ps.setString(8, jsonString);	//醫生資料
			ps.execute();			
			
			if (is != null){				
				is.close();
        		//System.out.println("上傳照片InputStream.close()");
        	}
			if (fis != null){				
				fis.close();
        		//System.out.println("上傳照片FileInputStream.close()");
        	}
		
		}
		catch (SQLException e1) {
			System.out.println(e1);
		}catch (IOException e) {
			System.out.println(e);
		}
		finally {	
            if (ps != null) try {ps.close();}catch (SQLException ignore) {}
            if (conn!=null) try {conn.close();}catch (Exception ignore) {}
        }
	}
	
	//顯示影片
	static public FileInputStream getVideo(String videoPath){
		File downloadFile = new File(videoPath);
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(downloadFile);		
		}
		catch (FileNotFoundException e) {
			System.out.println("發生FileNotFoundException : " + e);
		}
		
		return inputStream;
	}
		
	//顯示圖片
	static public InputStream getPhoto(Connection conn, String patientID, String time){
		PreparedStatement statement = null;
		InputStream inputStream = null;
		
		try {
            String sql = "SELECT picture FROM file WHERE patientID = ? AND time = ?";
            statement = conn.prepareStatement(sql);
            statement.setString(1, patientID);
            statement.setString(2, time);
            ResultSet resultSet = statement.executeQuery();
                        
            if (resultSet.next()) {
                Blob blob = resultSet.getBlob("picture");		//picture欄位
                
                if(blob!=null){
                	inputStream = blob.getBinaryStream();
                }
                
                if (resultSet != null) try { resultSet.close();} catch (SQLException ignore) {}                          
            }
            else {
            	System.out.println("找不到"+ patientID + "的檔案");
            }
            
        }
		catch (SQLException ex) {
        	System.out.println("發生SQLException");
        }
		finally {
            if (statement != null) try {statement.close();}catch (SQLException ignore) {}
            if (conn!=null) try {conn.close();}catch (Exception ignore) {}
        }
		return inputStream; 
	}
	
	//顯示縮圖
	static public InputStream getSmallPhoto(Connection conn, String patientID, String time){
		PreparedStatement statement = null;
		InputStream inputStream = null;
		
		try {
			String sql = "SELECT preview FROM file WHERE patientID = ? AND time = ?";
			statement = conn.prepareStatement(sql);
			statement.setString(1, patientID);
			statement.setString(2, time);
			ResultSet resultSet = statement.executeQuery();			
			
			if (resultSet.next()) {
				Blob blob = resultSet.getBlob("preview");		//preview欄位
				
				if(blob!=null){
					inputStream = blob.getBinaryStream();
				}
				
				if (resultSet != null) try {resultSet.close();} catch (SQLException ignore) {}          			
			}
			else {
				System.out.println("找不到"+ patientID+"的檔案");
			}
	            
		}
		catch (SQLException ex) {
			System.out.println("顯示縮圖發生SQLException");
		}
		finally {
			if (statement != null) try {statement.close();}catch (SQLException ignore) {}
            if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return inputStream;
		
	}
	
	static public BufferedImage scaleImage(BufferedImage bufferedImage, int size, double angle) {
        double boundSize = size;
        int origWidth = bufferedImage.getWidth();
        int origHeight = bufferedImage.getHeight();
        double scale;
        if (origHeight > origWidth)
        	scale = boundSize / origHeight;
        else
        	scale = boundSize / origWidth;
            //* Don't scale up small images.
        if (scale > 1.0)
        	return (bufferedImage);
        int scaledWidth = (int) (scale * origWidth);
        int scaledHeight = (int) (scale * origHeight);
        Image scaledImage = bufferedImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
           // new ImageIcon(image); // load image
           // scaledWidth = scaledImage.getWidth(null);
           // scaledHeight = scaledImage.getHeight(null);
        BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = scaledBI.createGraphics();
        g.rotate(Math.toRadians(angle),scaledWidth/2,scaledHeight/2);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
       
        g.drawImage(scaledImage, 0, 0, null);
        g.dispose();
        return (scaledBI);
   }
	
	//編輯
	static public String editPhoto(Connection conn, String patientID, String time, String description){
		String sql = "UPDATE file SET description = ? WHERE patientID = ? AND time = ?";
		PreparedStatement statement = null;
		try {
			statement = conn.prepareStatement(sql);
			statement.setString(1, description);
			statement.setString(2, patientID);
			statement.setString(3, time);
			statement.executeUpdate(); 
		} catch (SQLException e) {
			return "編輯失敗";
		}
		finally {
            if (statement != null) try {statement.close();}catch (SQLException ignore) {}
            if (conn!=null) try {conn.close();}catch (Exception ignore) {}
        }
		return "編輯成功";
	}
	
	//刪除照片
	static public String deletePhoto(Connection conn, String patientID, String time){
		String sql = "DELETE FROM file WHERE patientID = ? AND time = ?";
		PreparedStatement statement = null;
		try {
			statement = conn.prepareStatement(sql);
			statement.setString(1, patientID);
			statement.setString(2, time);
			statement.executeUpdate(); 
		} catch (SQLException e) {
			return "刪除失敗";
		}
		finally {
            if (statement != null) try { statement.close();}catch (SQLException ignore) {}
            if (conn!=null) try {conn.close();}catch (Exception ignore) {}
        }
		return "刪除成功";
	}
			
	//取得特定檔案資訊
	static public String getSelectFileInfo(Connection conn, String patientID, String time){
		String jsonString = "";
		JSONArray fileArray = new JSONArray();
		PreparedStatement statement = null;

		try {
			String sql = "SELECT video, time, description FROM file WHERE patientID = ? AND time = ?";
			statement = conn.prepareStatement(sql);
			statement.setString(1, patientID);
			statement.setString(2, time);		            
			ResultSet resultSet = statement.executeQuery();
			//patientID video time description
			while (resultSet.next()) {
				JSONObject fileObject = new JSONObject();
				fileObject.put("patientID", patientID);
					
				if(resultSet.getString("video").equals("")){
					fileObject.put("video", "");            		
				}
				else{
					fileObject.put("video", resultSet.getString("video"));
				}
					
				if(resultSet.getString("description") == null){
					fileObject.put("description", "");
				}
				else{
					fileObject.put("description", resultSet.getString("description"));
				}
					
				fileObject.put("time", resultSet.getString("time"));
					
				fileArray.put(fileObject);
			}
			if (resultSet != null) try { resultSet.close(); } catch (SQLException ignore) {}
		            
			jsonString = fileArray.toString();
				
			return jsonString;
		}
		catch (SQLException ex) {
			System.out.println("發生SQLException");
		}
		catch (JSONException e) {
			System.out.println("發生JSONException: " + e);
		}
		finally {
			if (statement != null) try { statement.close();}catch (SQLException ignore) {}
			if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return jsonString;
	}
	
	/*******用到doctor的地方***********************************已改*****************************/
	//取得病患所有檔案資訊
	static public String getAllFileInfo(Connection conn, String patientID){		
		String jsonString = "";
		JSONArray FList = new JSONArray();
		PreparedStatement statement = null;        
        
		try {
			String sql = "SELECT video, time, description, doctorID, doctorInfo FROM file WHERE patientID = ? ORDER BY time DESC";
			statement = conn.prepareStatement(sql);           
			statement.setString(1, patientID);            
			ResultSet resultSet = statement.executeQuery();
			
			//patientID video doctorID hospital department name time description			
			while (resultSet.next()) {
				JSONObject doctorInfoJSONObject = new JSONObject(resultSet.getString("doctorInfo"));
				String hospital = doctorInfoJSONObject.getString("hospital");
				String department = doctorInfoJSONObject.getString("department");
				String name = doctorInfoJSONObject.getString("name");
				
				JSONObject FItem = new JSONObject();
				FItem.put("patientID", patientID);
			            	
				if(resultSet.getString("video").equals("")){
					FItem.put("video", "");            		
				}
				else{
					FItem.put("video", resultSet.getString("video"));
				}
			            	
				if(resultSet.getString("description") == null){
					FItem.put("description", "");
				}
				else{
					FItem.put("description", resultSet.getString("description"));
				}
			            	
				FItem.put("time", resultSet.getString("time"));
				FItem.put("doctorID", resultSet.getString("doctorID"));
				//FItem.put("hospital", resultSet.getString("hospital"));
				FItem.put("hospital", hospital);
				//FItem.put("department", resultSet.getString("department"));
				FItem.put("department", department);
				//FItem.put("name", resultSet.getString("name"));
				FItem.put("name", name);
							
				FList.put(FItem);
			}
			if (resultSet != null) try { resultSet.close(); } catch (SQLException ignore) {}
			  
			jsonString = FList.toString();
			//System.out.println(jsonString);
			return jsonString;
		}
		catch (SQLException ex) {
			System.out.println("發生SQLException");
		}
		catch (JSONException e) {
			System.out.println("JSONException: " + e);
		}
		finally {
			if (statement != null) try { statement.close(); }catch (SQLException ignore) {}
			if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return jsonString;
	}
	
	//取得特定醫生檔案資訊
	public static String getDoctorFileInfo(Connection conn, String patientID, String doctorID){
		String jsonString = "";
		JSONArray fileArray = new JSONArray();	
		PreparedStatement statement = null;
			
		try {
			String sql = "SELECT video, time, description FROM file WHERE patientID = ? AND doctorID = ? ORDER BY time DESC";
			statement = conn.prepareStatement(sql);	
			statement.setString(1, patientID);
			statement.setString(2, doctorID);		            
			ResultSet resultSet = statement.executeQuery();
			//patientID video time description
			while (resultSet.next()) {
				JSONObject fileObject = new JSONObject();
				fileObject.put("patientID", patientID);
					
				if(resultSet.getString("video").equals("")){
					fileObject.put("video", "");            		
				}
				else{
					fileObject.put("video", resultSet.getString("video"));
				}
					
				if(resultSet.getString("description") == null){
					fileObject.put("description", "");
				}
				else{
					fileObject.put("description", resultSet.getString("description"));
				}
					
				fileObject.put("time", resultSet.getString("time"));
					
				fileArray.put(fileObject);
			}
			if (resultSet != null) try { resultSet.close(); } catch (SQLException ignore) {}
		            
			jsonString = fileArray.toString();
				//System.out.println(jsonString);
			return jsonString;
		} 
		catch (SQLException ex) {
			System.out.println("發生SQLException");
		}
		catch (JSONException e) {
			System.out.println("發生JSONException: " + e);
		}
		finally {
			if (statement != null) try {statement.close();}catch (SQLException ignore) {}
			if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return jsonString;
	}
}
