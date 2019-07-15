package bbdp.doctor.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class QuestionnairePoolServer {

	public static int newQuestion(Connection conn, String doctorID, String questionID, String questionName, String questionType, String questionOptionType, String questionOption) {
		int returnInt = 0;
		String result;
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select * FROM question where doctorID='"+doctorID+"'and question = '"+questionName+"'");
			if(rs.next()){
				returnInt = 2;	//題目重複
			}else{
				returnInt = st.executeUpdate("INSERT INTO question (questionID,doctorID,type,kind,question,option) VALUES('"+questionID+"','"+doctorID+"','"+questionType+"','"+questionOptionType+"','"+questionName+"','"+questionOption+"')");	
			}
			rs.close();
		    st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return returnInt;
	}
	synchronized public static String getMaxQuestionID(Connection conn, String doctorID) {
		int tempMax = -1;
		String returnString = "";
		
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select ifNULL(max(questionID+0),0)+1 FROM question where doctorID='"+doctorID+"'");
		    if (rs.next()) {
		    	tempMax = Integer.parseInt(rs.getString("ifNULL(max(questionID+0),0)+1"));
			    while(true){
			    	rs = st.executeQuery("select useQuestionID FROM questionIdList where doctorID ='"+doctorID+"' and useQuestionID = '"+tempMax+"'");
				    if (rs.next()) {
				    	tempMax += 1;
				    }else{
				    	st.executeUpdate("INSERT INTO questionIdList (useQuestionID,doctorID) VALUES('"+tempMax+"','"+doctorID+"')");	
				    	break;
				    }
			    }
		    }
			rs.close();
		    st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}

		if (tempMax != 0) 
			returnString = new String(""+tempMax);
		return returnString;
	}
	public static void deleteQuestionIDList(Connection conn, String doctorID, String questionID) {
		try {
			Statement st = conn.createStatement();
			int returnInt = st.executeUpdate("DELETE FROM questionIdList where doctorID='"+doctorID+"'and useQuestionID = '"+questionID+"'");
		    st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
	}
	public static ArrayList searchType(Connection conn, String doctorID, ArrayList typeList) {
		
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select distinct type FROM question where doctorID='"+doctorID+"'ORDER BY CAST(questionID AS UNSIGNED) DESC");
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
	public static ArrayList searchAllQuestion(Connection conn, String doctorID, ArrayList questionList) {
		
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select * FROM question where doctorID='"+doctorID+"'ORDER BY CAST(questionID AS UNSIGNED) DESC");
		    while (rs.next()) {
				questionList.add(rs.getString("questionID"));
		    	questionList.add(rs.getString("question"));
				questionList.add(rs.getString("type"));
		    }
			rs.close();
		    st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return questionList;
	}
	public static ArrayList searchQuestion(Connection conn, String doctorID, String type, ArrayList questionList) {
		
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select * FROM question where doctorID='"+doctorID+"' and type='"+type+"'ORDER BY CAST(questionID AS UNSIGNED) DESC");
		    while (rs.next()) {
				questionList.add(rs.getString("questionID"));
		    	questionList.add(rs.getString("question"));
				questionList.add(rs.getString("type"));
		    }
			rs.close();
		    st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return questionList;
	}
	public static String getQuestion(Connection conn, String doctorID, String questionID, String item) {
		String returnString = "";
		
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select "+item+" FROM question where doctorID='"+doctorID+"' and questionID = '"+questionID+"'");
		    while (rs.next()) {
		        returnString = rs.getString(item);
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
	public static String getMedicalRecord(Connection conn, String doctorID, String questionID, String item) {
		String returnString = "";
		
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select medicalRecord FROM question where doctorID='"+doctorID+"' and questionID = '"+questionID+"' and medicalRecord IS NOT NULL");
		    while (rs.next()) {
		        returnString = rs.getString(item);
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
	public static int deleteQuestion(Connection conn, String doctorID, String questionID) {
		int returnInt = 0;
		
		try {
			Statement st = conn.createStatement();
			returnInt = st.executeUpdate("DELETE FROM question where doctorID='"+doctorID+"'and questionID = '"+questionID+"'");
		    st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return returnInt;
	}
	public static int allowUpdateQuestion(Connection conn, String doctorID, String questionID) {
		int returnInt = 0;
		
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select * FROM questionsort natural join questionnaire where doctorID = '"+doctorID+"' and questionID = '"+questionID+"'");
			if(rs.next()){
				returnInt = 1;	//題目使用中
			}
			rs.close();
		    st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return returnInt;
	}
	public static int updateQuestion(Connection conn, String doctorID, String questionID, String questionName, String questionType, String questionOptionType, String questionOption, String medicalRecord) {
		int returnInt = 0;
		String result;
		
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select * FROM question where doctorID='"+doctorID+"'and questionID <> '"+questionID+"' and question = '"+questionName+"'");
			if(rs.next()){
				returnInt = 2;	//題目重複
			}else{
				returnInt = st.executeUpdate("UPDATE question SET question= '"+questionName+"' , type = '"+questionType+"' , kind = '"+questionOptionType+"', option ='"+questionOption+"', medicalRecord = '"+medicalRecord+"' WHERE questionID = '"+questionID+"' and doctorID = '"+doctorID+"'");	
			}
			rs.close();
		    st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return returnInt;
	}
	public static int addTempStorage(Connection conn, String doctorID, String questionArray) {
		int returnInt = 0;
		String[] tempArray = questionArray.split(",");
		try {
			Statement st = conn.createStatement();
			for(int i=0; i<tempArray.length; i++){
				returnInt += st.executeUpdate("UPDATE question SET tempstorage = 1 WHERE doctorID = '"+doctorID+"' and tempstorage = 0  and questionID = '"+tempArray[i]+"'");	
			}
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			  if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return returnInt;
	}
	public static ArrayList searchTempStorageType(Connection conn, String doctorID, ArrayList typeList) {
		
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select distinct type FROM question where doctorID='"+doctorID+"' and tempstorage = 1 ORDER BY CAST(questionID AS UNSIGNED) DESC");
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
	public static ArrayList searchAllTempStorage(Connection conn, String doctorID, ArrayList questionList) {
		
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select * FROM question where doctorID='"+doctorID+"' and tempstorage = 1 ORDER BY CAST(questionID AS UNSIGNED) DESC");
		    while (rs.next()) {
				questionList.add(rs.getString("questionID"));
		    	questionList.add(rs.getString("question"));
				questionList.add(rs.getString("type"));
		    }
			rs.close();
		    st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return questionList;
	}
	public static ArrayList searchTempStorageQuestion(Connection conn, String doctorID, String type, ArrayList questionList) {
		
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select * FROM question where doctorID='"+doctorID+"' and type='"+type+"' and tempstorage = 1 ORDER BY CAST(questionID AS UNSIGNED) DESC");
		    while (rs.next()) {
				questionList.add(rs.getString("questionID"));
		    	questionList.add(rs.getString("question"));
				questionList.add(rs.getString("type"));
		    }
			rs.close();
		    st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return questionList;
	}
	public static int removeTempStorage(Connection conn, String doctorID, String questionArray) {
		int returnInt = 0;
		String[] tempArray = questionArray.split(",");
		try {
			Statement st = conn.createStatement();
			for(int i=0; i<tempArray.length; i++){
				returnInt += st.executeUpdate("UPDATE question SET tempstorage = 0 WHERE doctorID = '"+doctorID+"' and questionID = '"+tempArray[i]+"'");	
			}
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			  if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return returnInt;
	}
	public static String checkID(Connection conn, String doctorID, String questionID) {
		String returnString = "";
		
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select * FROM question where doctorID='"+doctorID+"' and questionID = '"+questionID+"'");
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
}