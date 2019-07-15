package bbdp.doctor.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;

import com.froala.editor.Image;
import com.google.gson.Gson;


@MultipartConfig
@WebServlet("/InstructionFroalaServlet")
public class InstructionFroalaServlet extends HttpServlet {
    public InstructionFroalaServlet() {
        super();
    }    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 
		response.setContentType("text/html;charset=UTF-8");
		Gson gson = new Gson();

		String state = request.getParameter("state");
    	HttpSession session = request.getSession();		
		String doctorID = (String) session.getAttribute("doctorID");
		String patientInstructionID = request.getParameter("patientInstructionID");

		/*******************************************************************************************/

		String filePath = getServletContext().getRealPath("/");
		String InstructionFroalaPath = filePath + "Instruction\\"+ doctorID+ "\\" + patientInstructionID;
		File path = new File(InstructionFroalaPath);

		System.out.println("state : " + state);
		System.out.println("filePath : " + filePath);
		System.out.println("InstructionFroalaPath : " + InstructionFroalaPath);
		
		/*******************************************************************************************/

		if (!path.exists()) {	//如果路徑上沒有此資料夾，創一個
			System.out.println("!path.exists()");
			path.mkdirs();
		}
		
		/*******************************************************************************************/

		if(state.equals("uploadVideo")){
			System.out.println("uploadVedio");

			// The route on which the file is saved.
		       File uploads = new File(InstructionFroalaPath);
				System.out.println("uploads : " + uploads);

		       String multipartContentType = "multipart/form-data";
		       String fieldname = "file";
		       Part filePart = request.getPart(fieldname);
		       System.out.println("filePart : " + filePart);

		 
		       // Create path components to save the file.
		       Map < Object, Object > responseData;
		       final PrintWriter writer = response.getWriter();
		       String linkName = null;
		       
		       try {
					System.out.println("try");

		           // Check content type.
		           if (request.getContentType() == null ||
		               request.getContentType().toLowerCase().indexOf(multipartContentType) == -1) {
		 
		               throw new Exception("Invalid contentType. It must be " + multipartContentType);
		           }
		 
		           // Get file Part based on field name and also file extension.
		           String type = filePart.getContentType();
			       System.out.println("type : " + type);
		           type = type.substring(type.lastIndexOf("/") + 1);
			       System.out.println("type.substring : " + type);

		 
		           // Generate random name.
		           String extension = type;
		           extension = (extension != null && extension != "") ? "." + extension : extension;
			       System.out.println("extension : " + extension);
		           String name = UUID.randomUUID().toString() + extension;
		 
					System.out.println("name : " + name);

		           // Get absolute server path.
		           String absoluteServerPath = uploads + name;
		 
					System.out.println("absoluteServerPath : " + absoluteServerPath);

		           // Create link.
		           String vedioPath = request.getHeader("referer");
		           //linkName = vedioPath + "\\Instruction\\"+ doctorID+ "\\" + patientInstructionID + "\\" + name;
		           //linkName = vedioPath + "files/" + name;
		           String fileRoute = "Instruction/"+doctorID+"/"+patientInstructionID+"/";
		           System.out.println("fileRoute : " + fileRoute);
		           linkName = fileRoute + name;


		           System.out.println("vedioPath : " + vedioPath);

		 
		           // Validate file.
		           String mimeType = filePart.getContentType();
		           String[] allowedExts = new String[] {
		               "mp4",
		               "webm",
		               "ogg"
		           };
		           String[] allowedMimeTypes = new String[] {
		               "video/mp4",
		               "video/webm",
		               "video/ogg"
		           };
		 
		           if (!ArrayUtils.contains(allowedExts, FilenameUtils.getExtension(absoluteServerPath)) ||
		               !ArrayUtils.contains(allowedMimeTypes, mimeType.toLowerCase())) {
						
		        	   System.out.println("if");

		               // Delete the uploaded file.
		               File file = new File(absoluteServerPath);
		 
		               if (file.exists()) {
		                   file.delete();
		               }
		 
		               throw new Exception("File does not meet the validation.");
		           }
		 
		           // Save the file on server.
		           File file = new File(uploads, name);
		 
		           try (InputStream input = filePart.getInputStream()) {
		        	   System.out.println("try copy");
		        	   System.out.println("input : " + input);
		        	   System.out.println("file.toPath() : " + file.toPath().toString());

		               Files.copy(input, file.toPath());
		           } catch (Exception e) {
		            writer.println("<br/> ERROR: " + e);
		           }
		 
		       } catch (Exception e) {
		           e.printStackTrace();
		           writer.println("You either did not specify a file to upload or are " +
		               "trying to upload a file to a protected or nonexistent " +
		               "location.");
		           writer.println("<br/> ERROR: " + e.getMessage());
		           responseData = new HashMap < Object, Object > ();
		           responseData.put("error", e.toString());
		 
		       } finally {
					System.out.println("finally");
		           responseData = new HashMap < Object, Object > ();
		           responseData.put("link", linkName);
					System.out.println("linkName : " + linkName);

		           // Send response data.
		           String jsonResponseData = new Gson().toJson(responseData);
					System.out.println("jsonResponseData : " + jsonResponseData);

		           response.setContentType("application/json");
		           response.setCharacterEncoding("UTF-8");
		           response.getWriter().write(jsonResponseData);
		       }
		}
		if(state.equals("deleteVideoStillHasProblem")){
			String src = request.getParameter("src");
			System.out.println("src : " + src);
			
	        File file = new File(filePath + src);
			System.out.println("filePath + src : " + filePath + src);

	        try {
				System.out.println("try delete");
				System.out.println("file.toPath() : " + file.toPath().toString());

	            Files.delete(file.toPath());
	        } catch (Exception e) {
	            e.printStackTrace();
	            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	            return;
	        }
	        response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        response.getWriter().write(gson.toJson("Success"));
		}
		
		/*******************************************************************************************/

		if(state.equals("uploadImage")){
	        String fileRoute = "Instruction/"+doctorID+"/"+patientInstructionID+"/";
			System.out.println("fileRoute : " + fileRoute);
	
	        Map<Object, Object> responseData;
	        try {
	            responseData = Image.upload(request, fileRoute);	//上傳
	            Thread.currentThread().sleep(5000);	//等五秒
	        } catch (Exception e) {
	            e.printStackTrace();
	            responseData = new HashMap<Object, Object>();
	            responseData.put("InstructionFroalaServlet image upload error", e.toString());
	        }
	        response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        response.getWriter().write(gson.toJson(responseData));	//回傳例子{"link":"Instruction/1/5/e0cc7b71e9bf62b758889fa83aa7335372f79de0.jpg"}
		}
		if(state.equals("deleteImage")){
			String src = request.getParameter("src");           
	        try {
	            Image.delete(request, src);
	        } catch (Exception e) {
	            e.printStackTrace();
	            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	            return;
	        }
	        response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        response.getWriter().write(gson.toJson("Success"));
		}
		if(state.equals("deleteFolder")){
			File deletePath = new File(InstructionFroalaPath);
			if (deletePath.exists()){
				deleteAll(deletePath);
			}
		}
		if(state.equals("deleteNotInFolder")){
			String[] srcList = request.getParameterValues("srcImgVideo[]");
			System.out.println("1.newVideo : " + gson.toJson(srcList));

			File deletePath = new File(InstructionFroalaPath);
			if (deletePath.exists()){
				for(int i = 0; i < srcList.length; i++){
					srcList[i] = filePath + srcList[i];
					System.out.println(i + ".srcList[i] : " + srcList[i]);
					//newVideoList[i].replace("\\\\\\\\", "\\\\");
					//newVideoList[i].replace("E:", "!!!");
					System.out.println("filePath : " + filePath);
					System.out.println("2.srcList : " + gson.toJson(srcList));
				}
				deleteNotInFolder(deletePath, srcList);
			}
		}
	}

	
	
	public void deleteAll(File path) {
		System.out.println("deleteAll");

        if (!path.exists()) {
    		System.out.println("!path.exists()");
            return;
        }
        if (path.isFile()) {
    		System.out.println("path.isFile()");
    		System.out.println("path : " + path);
            path.delete();
    		System.out.println("path.delete() : " + path);
            return;
        }
        File[] files = path.listFiles();
		System.out.println("path.listFiles().length : " + files.length);

        for (int i = 0; i < files.length; i++) {
        	System.out.println("path.listFiles() : " + files);
            deleteAll(files[i]);
        }
        path.delete();
	}

	public void deleteNotInFolder(File path, String[] srcList) {
		System.out.println("deleteNotInFolder");
		Gson gson = new Gson();

        if (!path.exists()) {
    		System.out.println("!path.exists()");
            return;
        }
        if (path.isFile()) {
    		System.out.println("path.isFile()");
    		System.out.println("path : " + path);
            //path.delete();
    		System.out.println("path.delete() : " + path);
    		boolean flag = false;
    		for(int i = 0; i < srcList.length; i++){
				System.out.println(i + ".newVideoList[i] : " + srcList[i]);
    			if(srcList[i].equals(path.toString())){	//paht要用toString，不然無法當成字串比較
    	    		System.out.println("是一樣的喔喔喔!!!!!!!!!");
    				flag = true;
    				break;
    			}
    		}
    		System.out.println("flag : " + flag);
    		if(!flag){	//如果檔案夾裡的影片並不再要儲存的src裡面，就刪掉該影片
    			path.delete();
    		}

            return;
        }
        File[] files = path.listFiles();
		System.out.println("path.listFiles().length : " + files.length);

        for (int i = 0; i < files.length; i++) {
        	System.out.println(i + ".path.listFiles() : " + files);
        	deleteNotInFolder(files[i], srcList);
        }
        path.delete();
	}

}
