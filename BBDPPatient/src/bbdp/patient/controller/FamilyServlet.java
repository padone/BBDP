package bbdp.patient.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import bbdp.patient.model.FamilyServer;

@WebServlet("/FamilyServlet")
public class FamilyServlet extends HttpServlet {

	


	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");
		String option = request.getParameter("option");
		String account = request.getParameter("account");	//身分證字號
		String userID = request.getParameter("userID");
		String familyID = request.getParameter("familyID");
		String recipient = request.getParameter("recipient");	//接受者
		String accepted = request.getParameter("accepted");		//被接受者
		
		//上傳檔案傳推播用
		String patientID = request.getParameter("patientID");
		String time = request.getParameter("time");	
		
		if(account!=null){
			try{
				account = bbdp.encryption.base64.BBDPBase64.decode(account);	//解密
			}
			catch(IllegalArgumentException e){
				System.out.println("解密時發生IllegalArgumentException");
				account = "";
			}
		}
		
		Connection conn = null;
		DataSource datasource = (DataSource) getServletContext().getAttribute("db");
		try {
			conn = datasource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(option.equals("sendInvitation")){			//發送邀請
			response.setContentType("text/html;charset=UTF-8");			
			response.getWriter().write(FamilyServer.sendInvitation(conn, familyID, userID));		
		}
		else if(option.equals("checkInvitation")){			//檢查邀請
			response.setContentType("text/html;charset=UTF-8");			
			response.getWriter().write(FamilyServer.checkInvitation(conn, familyID, userID));		
		}
		else if(option.equals("acceptInvitation")){		//接受邀請		
			FamilyServer.acceptInvitation(conn, recipient, accepted);
		}
		else if(option.equals("refuseInvitation")){		//拒絕邀請
			response.setContentType("text/html;charset=UTF-8");
			FamilyServer.refuseInvitation(conn, userID, familyID);
		}
		else if(option.equals("getFamilyList")){		//取得家屬名單
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write(FamilyServer.getFamilyList(conn, userID));
		}
		else if(option.equals("getInvitationList")){	//取得收到的邀請名單
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write(FamilyServer.getInvitationList(conn, userID));
		}
		else if(option.equals("getSentInvitationList")){	//取得自己送出的邀請名單
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write(FamilyServer.getSentInvitationList(conn, userID));
		}
		else if (option.equals("editLimit")){				//修改權限
			int healthtrackingLimit = Integer.parseInt(request.getParameter("healthtrackingLimit"));
			int fileLimit = Integer.parseInt(request.getParameter("fileLimit"));
			int medicalrecordLimlt = Integer.parseInt(request.getParameter("medicalrecordLimlt"));
			FamilyServer.editLimit(conn, userID, familyID, healthtrackingLimit, fileLimit, medicalrecordLimlt);
		}
		else if(option.equals("getLimit")){					//取得自己(familyID)對家屬(userID)的權限
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write(FamilyServer.getLimit(conn, userID, familyID));
		}
		else if(option.equals("editKinship")){
			String kinship = request.getParameter("kinship");
			FamilyServer.editKinship(conn, userID, familyID, kinship);
		}
		else if(option.equals("getPatientName")){			//回傳病患姓名
	    	 response.setContentType("text/html;charset=UTF-8");
	    	 String patientName = FamilyServer.getPatientName(conn, account);
	    	 response.getWriter().write(patientName);
	    }
		else if(option.equals("getPatientNameByID")){		//回傳病患姓名
			response.setContentType("text/html;charset=UTF-8");
			String patientName = FamilyServer.getPatientNameByID(conn, familyID);
			response.getWriter().write(patientName);
	    }
		else if(option.equals("getKinship")){				//取得稱謂
			response.setContentType("text/html;charset=UTF-8");
			String kinship = FamilyServer.getKinship(conn, userID, familyID);
			response.getWriter().write(kinship);
		}
		else if(option.equals("searchPatientID")){
			 response.setContentType("text/html;charset=UTF-8");
			 String patientName = FamilyServer.searchPatientID(conn, account);
			 response.getWriter().write(patientName);
		}
		else if(option.equals("sendFilePush")){
			FamilyServer.sendFilePush(conn, patientID, time);
		}
		
		if (conn!=null) try {conn.close();}catch (Exception ignore) {}
	}


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
