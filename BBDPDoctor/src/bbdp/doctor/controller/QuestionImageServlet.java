package bbdp.doctor.controller;

import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;
 
import javax.servlet.annotation.MultipartConfig;
 
import com.froala.editor.Image;
import com.google.gson.Gson;

@MultipartConfig
@WebServlet("/QuestionImageServlet")
public class QuestionImageServlet extends HttpServlet {
    public QuestionImageServlet() {
        super();
    }
    private static final long serialVersionUID = 1L;
    @Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		String state = request.getParameter("state");
		String questionNum = request.getParameter("questionNum");
    	HttpSession session = request.getSession();		
		String doctorID = (String) session.getAttribute("doctorID");
		String imagePath = getServletContext().getRealPath("/");
		String questionImagePath = imagePath + "QuestionImage/" + doctorID +"/"+ questionNum;
		File path = new File(questionImagePath);
		if (!path.exists()) {
			path.mkdirs();
		}		
		switch(state) { 
			case "upload":{
		        Gson gson = new Gson();
		        String fileRoute = "QuestionImage/"+doctorID+"/"+questionNum+"/";
			        Map<Object, Object> responseData;
			        try {
			            responseData = Image.upload(request, fileRoute);
			            Thread.currentThread().sleep(5000);
			            
			        } catch (Exception e) {
			            e.printStackTrace();
			            responseData = new HashMap<Object, Object>();
			            responseData.put("QuestionImageServlet upload error", e.toString());
			            
			        }
			        response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        response.getWriter().write(gson.toJson(responseData));
			        
				break;
			}
			case "delete":{
				String src = request.getParameter("src");
				
				try {
					Image.delete(request, src);
				} catch (Exception e) {
					e.printStackTrace();
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					return;
				}
		        String jsonResponseData = new Gson().toJson("Success");
		        response.setContentType("application/json");
		        response.setCharacterEncoding("UTF-8");
		        response.getWriter().write(jsonResponseData);
			        
				break;
			}
			case "deleteImageFolder":{
				String questionID = request.getParameter("questionID");
				File deletePath = new File(imagePath + "QuestionImage\\"+ doctorID+ "\\" + questionID);
				if (deletePath.exists()){
					deleteAll(deletePath);
				}
				break;
			}
			
			default:
				System.out.print("QuestionImageServlet default");
		}
 
	}
	public void deleteAll(File path) {
        if (!path.exists()) {
            return;
        }
        if (path.isFile()) {
            path.delete();
            return;
        }
        File[] files = path.listFiles();
        for (int i = 0; i < files.length; i++) {
            deleteAll(files[i]);
        }
        path.delete();
		
	}

}