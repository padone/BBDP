package bbdp.patient.model;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.fileupload.FileItem;

public class ProfilePictureServer {
	//取得大頭貼
	static public InputStream getProfilePicture(Connection conn, String patientID){
		PreparedStatement statement = null;
			
		try {
			String sql = "SELECT photo FROM patient WHERE patientID = ?";
			statement = conn.prepareStatement(sql);
			statement.setString(1, patientID);
			ResultSet resultSet = statement.executeQuery();
			InputStream inputStream = null;
			
			if (resultSet.next()) {
				Blob blob = resultSet.getBlob("photo");
				if(blob!=null){
					inputStream = blob.getBinaryStream();
				}
				if (resultSet != null) try {resultSet.close();} catch (SQLException ignore) {}           
				return inputStream;
			}
			else {
				System.out.println("找不到"+ patientID+"的大頭貼");
			}
	            
		}
		catch (SQLException ex) {
			System.out.println("取得大頭貼時發生SQLException");
		}
		finally {
			if (statement != null) try {statement.close();}catch (SQLException ignore) {}
			if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return null; 
	}
	
	//上傳大頭貼
	static public void uploadProfilePicture(List<FileItem> items, Connection conn) throws IOException{
		String patientID = "";
			
		File previewPicture = new File("previewPicture");

        for (FileItem item : items) {
			if (item.isFormField()) {
				String fieldName = item.getFieldName();
				String fieldValue = item.getString("UTF-8");
				if(fieldName.equals("patientID")){
					patientID = fieldValue; 
				}
			}
			else {					
				int size = 200;		// size of the new image.
				InputStream imageStream = item.getInputStream();
				BufferedImage image = javax.imageio.ImageIO.read(imageStream); 
				BufferedImage newImage = ProfilePictureServer.scaleImage(image, size);
				ImageIO.write(newImage, "JPG", previewPicture);
					
				FileInputStream fis = new FileInputStream(previewPicture);		//縮圖
				
				//存圖片到資料庫
				saveProfilePictureToDB(conn, patientID ,fis);
					
				if (imageStream != null){
					imageStream.close(); 
				}
				if (conn!=null) try {conn.close();}catch (Exception ignore) {}
			}
		}
	}
	
	static public BufferedImage scaleImage(BufferedImage bufferedImage, int size) {
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
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(scaledImage, 0, 0, null);
        g.dispose();
        return (scaledBI);
   }
		//存檔案資訊到資料庫
	static private void saveProfilePictureToDB(Connection conn, String patientID, FileInputStream fis){
			
		PreparedStatement statement = null;
			
		try {			
			statement = conn.prepareStatement("UPDATE patient SET photo = ? WHERE patientID = ?");
			statement.setBlob(1, fis);				//大頭貼
			statement.setString(2, patientID);		//病患
			statement.execute();		
		}
		catch (SQLException e) {
			System.out.println("上傳大頭貼時發生SQLException");
		}
		finally {
			if (statement != null) try {statement.close();}catch (SQLException ignore) {}
			if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
	}
}
