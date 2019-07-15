package bbdp.doctor.model;

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


public class DoctorProfilePictureServer {
	//取得大頭貼
	static public InputStream getDoctorProfilePicture(Connection conn, String doctorID){
		PreparedStatement statement = null;
		
		try {
            String sql = "SELECT photo FROM doctor WHERE doctorID = ?";
            statement = conn.prepareStatement(sql);
            statement.setString(1, doctorID);
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
            	System.out.println("找不到"+ doctorID+"的大頭貼");
            }
            
        }
		catch (SQLException ex) {
        	System.out.println("取得醫生大頭貼時發生SQLException");
        }
		finally {
            if (statement != null) try {statement.close();}catch (SQLException ignore) {}
            if (conn!=null) try {conn.close();}catch (Exception ignore) {}
        }
		return null; 
	}
	//上傳圖片
	static public void uploadProfilePicture(List<FileItem> items, Connection conn, String doctorID) throws IOException{
				
		File previewPicture = new File("previewPicture");;
				
		for (FileItem item : items) {
			if (item.isFormField()) {}
			else {					
				InputStream imageStream = item.getInputStream();
				BufferedImage image = javax.imageio.ImageIO.read(imageStream); 
				BufferedImage newImage = DoctorProfilePictureServer.scaleImage(image);
				ImageIO.write(newImage, "JPG", previewPicture);
						
				FileInputStream fis = new FileInputStream(previewPicture);		//縮圖
									
				//存圖片到資料庫
				saveProfilePictureToDB(conn, doctorID ,fis);
						
				if (imageStream != null){
					imageStream.close(); 
				}
				if (conn!=null) try {conn.close();}catch (Exception ignore) {}
			}
		}
	}
	//壓縮圖片
	static public BufferedImage scaleImage(BufferedImage bufferedImage) {
		Image scaledImage = bufferedImage.getScaledInstance(400, 400, Image.SCALE_SMOOTH);
	           // new ImageIcon(image); // load image
	           // scaledWidth = scaledImage.getWidth(null);
	           // scaledHeight = scaledImage.getHeight(null);
		BufferedImage scaledBI = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = scaledBI.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(scaledImage, 0, 0, null);
		g.dispose();
		return (scaledBI);
	}
		//存檔案資訊到資料庫
	static private void saveProfilePictureToDB(Connection conn, String doctorID, FileInputStream fis){
				
		PreparedStatement statement = null;
				
		try {			
			statement = conn.prepareStatement("UPDATE doctor SET photo = ? WHERE doctorID = ?");
			statement.setBlob(1, fis);				//photo
			statement.setString(2, doctorID);		//doctorID
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