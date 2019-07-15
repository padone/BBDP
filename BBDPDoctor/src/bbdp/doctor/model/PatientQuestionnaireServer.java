package bbdp.doctor.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import org.apache.tomcat.jdbc.pool.DataSource;

public class PatientQuestionnaireServer {
	synchronized public static int newPatientQuestionnaire(DataSource datasource,Connection conn, String doctorID, String patientID, String questionnaireID, String times, String cycleType, String totalTimes, String today) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar now = Calendar.getInstance();
		int timesInt = Integer.parseInt(times);
		int totalTimesInt = Integer.parseInt(totalTimes);
		int addDay = 0;
		int addMonth = 0;
		int returnInt = 0;
		int tempInt = 0;
		String unfilledID = "";
		
		if(cycleType.equals("週"))
			addDay = timesInt*7;
		else if(cycleType.equals("天"))
			addDay = timesInt;
		else if(cycleType.equals("月"))
			addMonth = timesInt;
		
		if(today.equals("0")){
			now.add(Calendar.MONTH, addMonth); 
			now.add(Calendar.DATE, addDay);	
		}
		
		try {
			Statement st = conn.createStatement();
			ResultSet rs;
			for(int i=0; i<totalTimesInt; i++){

				rs = st.executeQuery("select * FROM unfilledquestionnaire where questionnaireID='"+questionnaireID+"' and patientID = '"+patientID+"' and sendDate = '"+sdf.format(now.getTime())+"'");
				if(!rs.next()){
					rs = st.executeQuery("select ifNULL(max(unfilledID+0),0)+1 FROM unfilledquestionnaire");
					if(rs.next()) unfilledID = rs.getString("ifNULL(max(unfilledID+0),0)+1");
					returnInt += st.executeUpdate("INSERT INTO unfilledquestionnaire (unfilledID,questionnaireID,patientID,sendDate) VALUES('"+unfilledID+"','"+questionnaireID+"','"+patientID+"','"+sdf.format(now.getTime())+"')");		
					tempInt = st.executeUpdate("UPDATE questionnaire SET occupied = 1 WHERE questionnaireID");
					bbdp.push.fcm.PushTimerServer.setPushTimer(datasource, sdf.format(now.getTime())+" 09:00:00", patientID, "BBDP", "您收到一份新的問卷", "FillInQuestionnaire.html?QID="+questionnaireID+"&DID="+doctorID+"&UNID="+unfilledID, "unfilledquestionnaire", "unfilledID", unfilledID);
				}
				now.add(Calendar.MONTH, addMonth); 
				now.add(Calendar.DATE, addDay);
			}
		    st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return returnInt;
	}
	public static ArrayList searchPatientQuestionnaireType(Connection conn, String doctorID, String patientID, ArrayList typeList) {
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select distinct type FROM answer join questionnaire where questionnaire.questionnaireID = answer.questionnaireID and questionnaire.doctorID='"+doctorID+"' and patientID = '"+patientID+"' ORDER BY CAST(answerID AS UNSIGNED) DESC");
		    while (rs.next()) {
		    	typeList.add(rs.getString("type"));
		    }
			rs.close();
		    st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return typeList;
	}
	public static ArrayList selectQuestionnaire(Connection conn, String doctorID, String patientID, String patientSelect, ArrayList typeList) {
		String sql = "";
		if(patientSelect.equals("0"))	//自評
			sql = "select distinct type FROM answer join questionnaire where questionnaire.questionnaireID = answer.questionnaireID and patientID = '"+patientID+"' and questionnaire.doctorID = '"+doctorID+"' and answer.doctorID is null ORDER BY CAST(answerID AS UNSIGNED) DESC";
		else	//後評
			sql = "select distinct type FROM answer join questionnaire where questionnaire.questionnaireID = answer.questionnaireID and patientID = '"+patientID+"' and questionnaire.doctorID = '"+doctorID+"' and answer.doctorID is not null ORDER BY CAST(answerID AS UNSIGNED) DESC";
		
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery(sql);
		    while (rs.next()) {
		    	typeList.add(rs.getString("type"));
		    }
			rs.close();
		    st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return typeList;
	}
	public static ArrayList unfilledquestionnaire(Connection conn, String doctorID, String patientID, ArrayList typeList) {
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select distinct type FROM unfilledquestionnaire natural join questionnaire where doctorID='"+doctorID+"' and patientID = '"+patientID+"' ORDER BY CAST(sendDate AS UNSIGNED) DESC");
		    while (rs.next()) {
		    	typeList.add(rs.getString("type"));
		    }
			rs.close();
		    st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return typeList;
	}
	public static ArrayList selectQuestionnaireDate(Connection conn, String doctorID, String patientID, String patientSelect, String type, String dateRange, ArrayList dateList) {
		ArrayList temp = new ArrayList();
		if(patientSelect.equals("all")){
			if(type.equals("")){
				try {
					Statement st = conn.createStatement();
				    ResultSet rs = st.executeQuery("select distinct date FROM answer join questionnaire where questionnaire.questionnaireID = answer.questionnaireID and questionnaire.doctorID='"+doctorID+"' and patientID = '"+patientID+"' ORDER BY CAST(date AS UNSIGNED) DESC");
				    while (rs.next()) {
				    	temp.add(rs.getString("date"));
				    }
					rs.close();
				    st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
				      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
				}
			}else{
				try {
					Statement st = conn.createStatement();
				    ResultSet rs = st.executeQuery("select distinct date FROM answer join questionnaire where questionnaire.questionnaireID = answer.questionnaireID and questionnaire.doctorID='"+doctorID+"' and patientID = '"+patientID+"' and type = '"+type+"' ORDER BY CAST(date AS UNSIGNED) DESC");
				    while (rs.next()) {
				    	temp.add(rs.getString("date"));
				    }
					rs.close();
				    st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
				      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
				}				
			}
		}else if(patientSelect.equals("not")){
			if(type.equals("")){
				try {
					Statement st = conn.createStatement();
				    ResultSet rs = st.executeQuery("select distinct sendDate FROM unfilledquestionnaire natural join questionnaire where doctorID='"+doctorID+"' and patientID = '"+patientID+"' ORDER BY CAST(sendDate AS UNSIGNED) DESC");
				    while (rs.next()) {
				    	temp.add(rs.getString("sendDate"));
				    }
					rs.close();
				    st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
				      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
				}
			}else{
				try {
					Statement st = conn.createStatement();
				    ResultSet rs = st.executeQuery("select distinct sendDate FROM unfilledquestionnaire natural join questionnaire where doctorID='"+doctorID+"' and patientID = '"+patientID+"' and type = '"+type+"' ORDER BY CAST(sendDate AS UNSIGNED) DESC");
				    while (rs.next()) {
				    	temp.add(rs.getString("sendDate"));
				    }
					rs.close();
				    st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
				      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
				}				
			}				
		}else if(patientSelect.equals("0")){
			if(type.equals("")){
				try {
					Statement st = conn.createStatement();
				    ResultSet rs = st.executeQuery("select distinct date FROM answer join questionnaire where questionnaire.questionnaireID = answer.questionnaireID and questionnaire.doctorID='"+doctorID+"' and patientID = '"+patientID+"' and answer.doctorID is null ORDER BY CAST(date AS UNSIGNED) DESC");
				    while (rs.next()) {
				    	temp.add(rs.getString("date"));
				    }
					rs.close();
				    st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
				      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
				}
			}else{
				try {
					Statement st = conn.createStatement();
					ResultSet rs = st.executeQuery("select distinct date FROM answer join questionnaire where questionnaire.questionnaireID = answer.questionnaireID and questionnaire.doctorID='"+doctorID+"' and patientID = '"+patientID+"' and answer.doctorID is null and type = '"+type+"' ORDER BY CAST(date AS UNSIGNED) DESC");
				    while (rs.next()) {
				    	temp.add(rs.getString("date"));
				    }
					rs.close();
				    st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
				      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
				}				
			}						
		}else {
			if(type.equals("")){
				try {
					Statement st = conn.createStatement();
				    ResultSet rs = st.executeQuery("select distinct date FROM answer join questionnaire where questionnaire.questionnaireID = answer.questionnaireID and questionnaire.doctorID='"+doctorID+"' and patientID = '"+patientID+"' and answer.doctorID is not null ORDER BY CAST(date AS UNSIGNED) DESC");
				    while (rs.next()) {
				    	temp.add(rs.getString("date"));
				    }
					rs.close();
				    st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
				      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
				}
			}else{
				try {
					Statement st = conn.createStatement();
					ResultSet rs = st.executeQuery("select distinct date FROM answer join questionnaire where questionnaire.questionnaireID = answer.questionnaireID and questionnaire.doctorID='"+doctorID+"' and patientID = '"+patientID+"' and answer.doctorID is not null and type = '"+type+"' ORDER BY CAST(date AS UNSIGNED) DESC");
				    while (rs.next()) {
				    	temp.add(rs.getString("date"));
				    }
					rs.close();
				    st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
				      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
				}			
			}				
		}
		String tempYear="";
		String tempMonth="";
		if(dateRange.equals("year")){		
			for(int i = 0;i<temp.size();i++){
				String[] AfterSplit = ((String)temp.get(i)).split("-");
				if(tempYear.equals(AfterSplit[0])){
				}else{
					dateList.add(AfterSplit[0]);
					tempYear = AfterSplit[0];
				}
			}			
		}else{
			for(int i = 0;i<temp.size();i++){
				String[] AfterSplit = ((String)temp.get(i)).split("-");
				if(tempYear.equals(AfterSplit[0]) && tempMonth.equals(AfterSplit[1])){
					
				}else{
					dateList.add(AfterSplit[0]+"/"+AfterSplit[1]);
					tempYear = AfterSplit[0];
					tempMonth = AfterSplit[1];
				}
			}				
		}
		return dateList;
	}
	public static ArrayList getQuestionnaireList(Connection conn, String doctorID, String patientID, String patientSelect, String type, String dateRange, String date, ArrayList questionnaireList){	
		String searchTemp="";

		if(date.equals("")){
			dateRange = "";		
		}		

		if(patientSelect.equals("all")){
			if(type.equals("")){
				 switch(dateRange){
					case "year":
						searchTemp = "select * FROM answer join questionnaire where questionnaire.questionnaireID = answer.questionnaireID and questionnaire.doctorID = '"+doctorID+"' and patientID = '"+patientID+"' and (date BETWEEN '"+date+"/01/01' and '"+date+"/12/31') ORDER BY CAST(date AS UNSIGNED) DESC";
					break;
					case "month":
						searchTemp = "select * FROM answer join questionnaire where questionnaire.questionnaireID = answer.questionnaireID and questionnaire.doctorID = '"+doctorID+"' and patientID = '"+patientID+"' and (date BETWEEN '"+date+"/01' and '"+date+"/31') ORDER BY CAST(date AS UNSIGNED) DESC";
					break;
					default: 
						searchTemp = "select * FROM answer join questionnaire where questionnaire.questionnaireID = answer.questionnaireID and questionnaire.doctorID = '"+doctorID+"' and patientID = '"+patientID+"' ORDER BY CAST(date AS UNSIGNED) DESC";
				 }
			}else{
				 switch(dateRange){
					case "year":
						searchTemp = "select * FROM answer join questionnaire where questionnaire.questionnaireID = answer.questionnaireID and questionnaire.doctorID = '"+doctorID+"' and patientID = '"+patientID+"' and type = '"+type+"' and (date BETWEEN '"+date+"/01/01' and '"+date+"/12/31') ORDER BY CAST(date AS UNSIGNED) DESC";
					break;
					case "month":
						searchTemp = "select * FROM answer join questionnaire where questionnaire.questionnaireID = answer.questionnaireID and questionnaire.doctorID = '"+doctorID+"' and patientID = '"+patientID+"' and type = '"+type+"' and (date BETWEEN '"+date+"/01' and '"+date+"/31') ORDER BY CAST(date AS UNSIGNED) DESC";
					break;
					default: 
						searchTemp = "select * FROM answer join questionnaire where questionnaire.questionnaireID = answer.questionnaireID and questionnaire.doctorID = '"+doctorID+"' and patientID = '"+patientID+"' and type = '"+type+"' ORDER BY CAST(date AS UNSIGNED) DESC";
				 }	
			}
        }else if(patientSelect.equals("not")){
			if(type.equals("")){
				 switch(dateRange){
					case "year":
						searchTemp = "select * FROM unfilledquestionnaire natural join questionnaire where doctorID='"+doctorID+"' and patientID = '"+patientID+"' and (sendDate BETWEEN '"+date+"/01/01' and '"+date+"/12/31') ORDER BY CAST(sendDate AS UNSIGNED)";
					break;
					case "month":
						searchTemp = "select * FROM unfilledquestionnaire natural join questionnaire where doctorID='"+doctorID+"' and patientID = '"+patientID+"' and (sendDate BETWEEN '"+date+"/01' and '"+date+"/31') ORDER BY CAST(sendDate AS UNSIGNED)";
					break;
					default: 
						searchTemp = "select * FROM unfilledquestionnaire natural join questionnaire where doctorID='"+doctorID+"' and patientID = '"+patientID+"' ORDER BY CAST(sendDate AS UNSIGNED)";
				 }
			}else{
				 switch(dateRange){
					case "year":
						searchTemp = "select * FROM unfilledquestionnaire natural join questionnaire where doctorID='"+doctorID+"' and patientID = '"+patientID+"' and type = '"+type+"' and (sendDate BETWEEN '"+date+"/01/01' and '"+date+"/12/31') ORDER BY CAST(sendDate AS UNSIGNED)";
					break;
					case "month":
						searchTemp = "select * FROM unfilledquestionnaire natural join questionnaire where doctorID='"+doctorID+"' and patientID = '"+patientID+"' and type = '"+type+"' and (sendDate BETWEEN '"+date+"/01' and '"+date+"/31') ORDER BY CAST(sendDate AS UNSIGNED)";
					break;
					default: 
						searchTemp = "select * FROM unfilledquestionnaire natural join questionnaire where doctorID='"+doctorID+"' and patientID = '"+patientID+"' and type = '"+type+"' ORDER BY CAST(sendDate AS UNSIGNED)";
				 }	
			}					
		}else{
			String temp = "is null";
			if(patientSelect.equals("1")) temp = "is not null";
			
			if(type.equals("")){
				 switch(dateRange){
					case "year":
						searchTemp = "select * FROM answer join questionnaire where questionnaire.questionnaireID = answer.questionnaireID and questionnaire.doctorID = '"+doctorID+"' and patientID = '"+patientID+"' and answer.doctorID "+temp+" and (date BETWEEN '"+date+"/01/01' and '"+date+"/12/31') ORDER BY CAST(date AS UNSIGNED) DESC";
					break;
					case "month":
						searchTemp = "select * FROM answer join questionnaire where questionnaire.questionnaireID = answer.questionnaireID and questionnaire.doctorID = '"+doctorID+"' and patientID = '"+patientID+"' and answer.doctorID "+temp+" and (date BETWEEN '"+date+"/01' and '"+date+"/31') ORDER BY CAST(date AS UNSIGNED) DESC";
					break;
					default: 
						searchTemp = "select * FROM answer join questionnaire where questionnaire.questionnaireID = answer.questionnaireID and questionnaire.doctorID = '"+doctorID+"' and patientID = '"+patientID+"' and answer.doctorID "+temp+" ORDER BY CAST(date AS UNSIGNED) DESC";
				 }
			}else{
				 switch(dateRange){
					case "year":
						searchTemp = "select * FROM answer join questionnaire where questionnaire.questionnaireID = answer.questionnaireID and questionnaire.doctorID = '"+doctorID+"' and patientID = '"+patientID+"' and answer.doctorID "+temp+" and type = '"+type+"' and (date BETWEEN '"+date+"/01/01' and '"+date+"/12/31') ORDER BY CAST(date AS UNSIGNED) DESC";
					break;
					case "month":
						searchTemp = "select * FROM answer join questionnaire where questionnaire.questionnaireID = answer.questionnaireID and questionnaire.doctorID = '"+doctorID+"' and patientID = '"+patientID+"' and answer.doctorID "+temp+" and type = '"+type+"' and (date BETWEEN '"+date+"/01' and '"+date+"/31') ORDER BY CAST(date AS UNSIGNED) DESC";
					break;
					default: 
						searchTemp = "select * FROM answer join questionnaire where questionnaire.questionnaireID = answer.questionnaireID and questionnaire.doctorID = '"+doctorID+"' and patientID = '"+patientID+"' and answer.doctorID "+temp+" and type = '"+type+"' ORDER BY CAST(date AS UNSIGNED) DESC";
				 }	
			}					
		}
		
		if(patientSelect.equals("not")){
			try {
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery(searchTemp);
				while (rs.next()) {
					questionnaireList.add("questionnairePink2");//0
					questionnaireList.add("未填寫");//1
					questionnaireList.add("un="+rs.getString("unfilledID"));//2
					questionnaireList.add(rs.getString("name"));//3
					questionnaireList.add(rs.getString("sendDate"));//4
				}
				rs.close();
				st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				  if (conn!=null) try {conn.close();}catch (Exception ignore) {}
			}		
		}else{
			try {
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery(searchTemp);
				while (rs.next()) {
					if(rs.getString("answer.doctorID") == null){
						questionnaireList.add("questionnairePink");//0
						questionnaireList.add("自評");//1
					}else{
						questionnaireList.add("questionnaireGreen");//0
						questionnaireList.add("後評");//1
					}
					questionnaireList.add("num="+rs.getString("answerID"));//2
					questionnaireList.add(rs.getString("name"));//3
					questionnaireList.add(rs.getString("date"));//4
				}
				rs.close();
				st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				  if (conn!=null) try {conn.close();}catch (Exception ignore) {}
			}				
		}
		return questionnaireList;
	}
	public static String checkAnswerID(Connection conn, String doctorID, String patientID, String answerID) {
		String returnString = "";
		
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select * FROM answer join questionnaire where answer.questionnaireID = questionnaire.questionnaireID and questionnaire.doctorID='"+doctorID+"' and patientID = '"+patientID+"'and answerID = '"+answerID+"'");
		    if(rs.next()) {
		        returnString = "Yes";
		    }
			rs.close();
		    st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return returnString;
	}
	public static String checkUnfilledID(Connection conn, String doctorID, String patientID, String unfilledID) {
		String returnString = "";
		
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select * FROM unfilledquestionnaire natural join questionnaire where doctorID='"+doctorID+"' and patientID = '"+patientID+"' and unfilledID = '"+unfilledID+"'");
		    if(rs.next()) {
		        returnString = "Yes";
		    }
			rs.close();
		    st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return returnString;
	}
	public static ArrayList getUnfilledQuestionnaire(Connection conn, String doctorID, String patientID, String unfilledID, ArrayList questionnaireList){
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("select * FROM unfilledquestionnaire natural join questionnaire where doctorID='"+doctorID+"' and patientID = '"+patientID+"' and unfilledID = '"+unfilledID+"'");
			while (rs.next()) {
				questionnaireList.add(rs.getString("name"));//0
				questionnaireList.add(rs.getString("sendDate"));//1
				questionnaireList.add(rs.getString("questionnaireID"));//2			
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			  if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		
		return questionnaireList;
	}
	public static int cancelQuestionnaire(DataSource datasource, Connection conn, String patientID, String unfilledID) {
		int returnInt = 0;
		SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-dd");
		java.text.DateFormat sdfDayTime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar now = Calendar.getInstance();
		Calendar c1 = Calendar.getInstance();
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("select * FROM unfilledquestionnaire where unfilledID='"+unfilledID+"' and sendDate >= '"+sdfDay.format(now.getTime())+"'");
			if (rs.next()) {
				try {
					c1.setTime(sdfDayTime.parse(rs.getString("sendDate")+" 09:00:00"));
					int result = c1.compareTo(now);
					if(result > 0){
						bbdp.push.fcm.PushTimerServer.deleteSpecificPushTimer(datasource, patientID, "unfilledquestionnaire", "unfilledID", unfilledID);
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			returnInt = st.executeUpdate("DELETE FROM unfilledquestionnaire where unfilledID='"+unfilledID+"'");
			rs.close();
		    st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}	
		return returnInt;
	}
	public static ArrayList getQuestionnaireAnswer(Connection conn, String doctorID, String patientID, String answerID, ArrayList questionnaireList){
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("select * FROM answer join questionnaire where answer.questionnaireID = questionnaire.questionnaireID and questionnaire.doctorID='"+doctorID+"' and patientID = '"+patientID+"'and answerID = '"+answerID+"'");
			while (rs.next()) {
				questionnaireList.add(rs.getString("questionnaire.name"));//0
				questionnaireList.add(rs.getString("date"));//1
				questionnaireList.add(rs.getString("answer.questionnaireID"));//2
				questionnaireList.add(rs.getString("scoring"));//3
				if(rs.getString("answer.doctorID") == null)
					questionnaireList.add("自評");//4
				else
					questionnaireList.add("後評");//4
				
				questionnaireList.add(rs.getString("selfDescription"));//5
				
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			  if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		
		return questionnaireList;
	}
	public static String getOptionAnswer(Connection conn, String doctorID, String patientID, String answerID) {
		String returnString = "";
		
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("select answer FROM answer where answerID = '"+answerID+"'");
			if (rs.next()) {
				returnString = rs.getString("answer");		
			}
			rs.close();
		    st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return returnString;
	}
	public static String getPatientOptionAnswer(Connection conn, String doctorID, String patientID, String answerID) {
		String returnString = "";
		String answerID_patient = "";
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("select answerID_patient FROM association where answerID_doctor = '"+answerID+"'");
			if (rs.next()) {
				answerID_patient = rs.getString("answerID_patient");
			}
			rs = st.executeQuery("select answer FROM answer where answerID = '"+answerID_patient+"'");
			if (rs.next()) {
				returnString = rs.getString("answer");
			}
			rs.close();
		    st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return returnString;
	}
	public static ArrayList getMedicalRecord(Connection conn, String doctorID, String patientID, String questionnaireID, ArrayList medicalRecordList) {
		
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("select * FROM patient where patientID = '"+patientID+"'");
			if (rs.next()) {		
				medicalRecordList.add(calculateAge(rs.getString("birthday"))+"歲"+rs.getString("sex")+"性");			
			}			
			rs = st.executeQuery("select medicalRecord FROM questionnaire where questionnaireID = '"+questionnaireID+"'");
			if (rs.next()) {
				medicalRecordList.add(rs.getString("medicalRecord"));		
			}
			rs.close();
		    st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return medicalRecordList;
	}

	//計算年齡
	private static int calculateAge(String birth) {
		int year = Integer.valueOf(birth.substring(0, 4));
		int month = Integer.valueOf(birth.substring(5, 7));
		int date = Integer.valueOf(birth.substring(8));
		LocalDate birthday = LocalDate.of(year, month, date);
		LocalDate today = LocalDate.now();
		Period period = Period.between(birthday, today);
		return period.getYears();
	}	
	
	public static String getMoreButton(Connection conn, String doctorID, String patientID, String answerID) {
		String returnString = "";
		
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select answerID_doctor FROM association where answerID_patient = '"+answerID+"'");
		    if(rs.next()) {
		        returnString = rs.getString("answerID_doctor");
		    }
			rs.close();
		    st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return returnString;
	}
	synchronized public static String newAnswer(Connection conn, String doctorID, String patientID, String answerID, String questionnaireID, String doctorAnswer, String selfDescription) {
		int tempInt = 0;
		String returnString = "";
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar now = Calendar.getInstance(); 		
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select ifNULL(max(answerID+0),0)+1 FROM answer");
		    if (rs.next()) {
		    	returnString = rs.getString("ifNULL(max(answerID+0),0)+1");
		    	tempInt = st.executeUpdate("INSERT INTO answer (answerID,questionnaireID,patientID,doctorID,date,selfDescription,answer) select ifNULL(max(answerID+0),0)+1,'"+questionnaireID+"','"+patientID+"','"+doctorID+"','"+sdf.format(now.getTime())+"','"+selfDescription+"','"+doctorAnswer+"'FROM answer");
		    	tempInt = st.executeUpdate("INSERT INTO association (answerID_patient,answerID_doctor) VALUES('"+answerID+"','"+returnString+"')");
		    }
			rs.close();
		    st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return returnString;
	}
	public static int deleteDoctorAnswer(Connection conn, String doctorID, String patientID, String answerID) {
		int returnInt = 0;

		try {
			Statement st = conn.createStatement();
			returnInt = st.executeUpdate("DELETE FROM association where answerID_doctor = '"+answerID+"'");
			returnInt = st.executeUpdate("DELETE FROM answer where answerID = '"+answerID+"'");
		    st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return returnInt;
	}
	public static ArrayList getNewestAnswer(Connection conn, String doctorID, String patientID, ArrayList patientAnswer) {
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select * FROM answer join questionnaire where questionnaire.questionnaireID = answer.questionnaireID and answer.doctorID is null and questionnaire.doctorID='"+doctorID+"' and patientID = '"+patientID+"' ORDER BY CAST(answerID AS UNSIGNED) DESC");
		    if (rs.next()) {
		    	System.out.println(rs.getString("answer.answerID"));
		    	patientAnswer.add(rs.getString("answer.questionnaireID"));	//0
		    	patientAnswer.add(rs.getString("questionnaire.name"));	//1
		    	patientAnswer.add(rs.getString("questionnaire.scoring"));	//2 	
		    	patientAnswer.add(rs.getString("answer.date"));		//3
		    	patientAnswer.add(rs.getString("answer.selfDescription"));	//4
		    	patientAnswer.add(rs.getString("answer.answerID"));	//5
		    }
			rs.close();
		    st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return patientAnswer;
	}
}
