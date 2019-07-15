package bbdp.doctor.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bbdp.doctor.model.NoticeServer;

@WebServlet("/GetSessionServlet")
public class GetSessionServlet extends HttpServlet {

    public GetSessionServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//取得session
    	HttpSession session = request.getSession();		
		String patientID = (String) session.getAttribute("patientID");
		String doctorID = (String) session.getAttribute("doctorID");
		String jsonString = "[{\"patientID\":\"" + patientID + "\",\"doctorID\":\"" + doctorID + "\"}]";
		
		String option = request.getParameter("option");  
		if(option.equals("getSession")){									
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write(jsonString);		
		}
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
