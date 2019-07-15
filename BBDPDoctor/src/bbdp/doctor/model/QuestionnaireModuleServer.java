package bbdp.doctor.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

public class QuestionnaireModuleServer {

	public static ArrayList searchType(Connection conn, String doctorID, ArrayList typeList) {
		
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select distinct type FROM questionnaire where doctorID='"+doctorID+"'ORDER BY CAST(questionnaireID AS UNSIGNED) DESC");
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
	public static ArrayList addTempStorageQuestion(Connection conn, String doctorID, ArrayList questionList) {
		
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select * FROM question where doctorID='"+doctorID+"' and tempstorage = 1");
		    while (rs.next()) {
				questionList.add(rs.getString("questionID"));
		    	questionList.add(rs.getString("question"));
				questionList.add(rs.getString("option"));
		    }
			int returnInt = st.executeUpdate("UPDATE question SET tempstorage = 0 WHERE doctorID = '"+doctorID+"' and tempstorage = 1");
			rs.close();
		    st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return questionList;
	}
	synchronized public static int newQuestionnaire(Connection conn, String doctorID, String questionnaireName, String questionnaireType, String partArray, String partName, String scoring) {
		int returnInt = 0;
		int insertSort;
		String result = null;
		int sortCount = 1;
		doctorID = "1";
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select * FROM questionnaire where doctorID='"+doctorID+"'and name = '"+questionnaireName+"'");
			if(rs.next()){
				returnInt = 2;	//題目重複
			}else{
				rs = st.executeQuery("select ifNULL(max(questionnaireID+0),0)+1 FROM questionnaire");
				if(rs.next()){
					result = rs.getString("ifNULL(max(questionnaireID+0),0)+1");
				}
				returnInt = st.executeUpdate("INSERT INTO questionnaire (questionnaireID,doctorID,name,type,partName,medicalRecord,scoring) select '"+result+"','"+doctorID+"','"+questionnaireName+"','"+questionnaireType+"','"+partName+"','','"+scoring+"'");
				JSONArray questionArray = null;
				try {
					questionArray = new JSONArray(partArray);
					for(int i=0; i<questionArray.length(); i++){
						String[] tokens = questionArray.getString(i).split(",");
						for (int j=0; j<tokens.length; j++){
							if(tokens[j].length() > 0){
								insertSort = st.executeUpdate("INSERT INTO questionsort (questionnaireID, questionID, partNumber, sortNumber) VALUES ('"+result+"', '"+tokens[j]+"', '"+i+"', '"+sortCount+"')");
								sortCount += 1;
							}
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
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
	public static ArrayList searchAllQuestionnaire(Connection conn, String doctorID, ArrayList questionnaireList) {
		
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select * FROM questionnaire where doctorID='"+doctorID+"'ORDER BY CAST(questionnaireID AS UNSIGNED) DESC");
		    while (rs.next()) {
		    	questionnaireList.add(rs.getString("questionnaireID"));
				questionnaireList.add(rs.getString("name"));
				questionnaireList.add(rs.getString("type"));
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
	public static ArrayList searchQuestionnaire(Connection conn, String doctorID, String type, ArrayList questionnaireList) {
		
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select * FROM questionnaire where doctorID='"+doctorID+"' and type='"+type+"'ORDER BY CAST(questionnaireID AS UNSIGNED) DESC");
		    while (rs.next()) {
		    	questionnaireList.add(rs.getString("questionnaireID"));
		    	questionnaireList.add(rs.getString("name"));
		    	questionnaireList.add(rs.getString("type"));
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
	public static String getQuestionnaire(Connection conn, String doctorID, String questionnaireID, String item) {
		String returnString = "";
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select "+item+" FROM questionnaire where doctorID='"+doctorID+"' and questionnaireID = '"+questionnaireID+"'");
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
	public static ArrayList getQuestionList(Connection conn, String doctorID, String questionnaireID, ArrayList questionnaireList) {
		
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select * FROM questionsort where questionnaireID = '"+questionnaireID+"' ORDER BY CAST(sortNumber AS UNSIGNED)");
		    while (rs.next()) {
		    	questionnaireList.add(rs.getString("partNumber"));
		    	questionnaireList.add(rs.getString("questionID"));
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
	public static ArrayList searchQuestion(Connection conn, String doctorID, String questionID, ArrayList questionList) {
		
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select * FROM question where doctorID='"+doctorID+"' and questionID = '"+questionID+"'");
		    while (rs.next()) {
		    	questionList.add(rs.getString("questionID"));
		    	questionList.add(rs.getString("question"));
				questionList.add(rs.getString("option"));
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
	synchronized public static int updateQuestionnaire(Connection conn, String doctorID, String questionnaireID, String questionnaireName, String questionnaireType, String partArray, String partName, String scoring, String medicalRecord) {
		int returnInt = 0;
		int insertSort;
		int sortCount = 1;
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select * FROM questionnaire where doctorID='"+doctorID+"'and questionnaireID <> '"+questionnaireID+"' and name = '"+questionnaireName+"'");
			if(rs.next()){
				returnInt = 2;	//問卷名稱重複
			}else{
				returnInt = st.executeUpdate("UPDATE questionnaire SET name = '"+questionnaireName+"' , type = '"+questionnaireType+"' , partName = '"+partName+"', scoring ='"+scoring+"', medicalRecord = '"+medicalRecord+"' WHERE questionnaireID = '"+questionnaireID+"'");
				int temp = st.executeUpdate("DELETE FROM questionsort where questionnaireID = '"+questionnaireID+"'");
				JSONArray questionArray = null;
				try {
					questionArray = new JSONArray(partArray);
					for(int i=0; i<questionArray.length(); i++){
						String[] tokens = questionArray.getString(i).split(",");
						for (int j=0; j<tokens.length; j++){
							if(tokens[j].length() > 0){
								insertSort = st.executeUpdate("INSERT INTO questionsort (questionnaireID, questionID, partNumber, sortNumber) VALUES ('"+questionnaireID+"', '"+tokens[j]+"', '"+i+"', '"+sortCount+"')");
								sortCount += 1;
							}
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
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
	public static String allowUpdateQuestionnaire(Connection conn, String doctorID, String questionnaireID) {
		String returnString = "";
		
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select occupied FROM questionnaire where doctorID='"+doctorID+"'and questionnaireID = '"+questionnaireID+"'");
		    while (rs.next()) {
		        returnString = rs.getString("occupied");
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
	public static ArrayList searchSymptom(Connection conn, String doctorID, ArrayList symptomList) {
		
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select symptom FROM questionnaire where doctorID='"+doctorID+"' ORDER BY CAST(questionnaireID AS UNSIGNED) DESC");
		    while (rs.next()) {
		    	symptomList.add(rs.getString("symptom"));
		    }
			rs.close();
		    st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return symptomList;
	}
	synchronized public static int addFirstVisitQuestionnaire(Connection conn, String doctorID, String questionnaireID, String symptom) {
		int returnInt = 0;
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select * FROM questionnaire where doctorID='"+doctorID+"'and symptom = '"+symptom+"'");
		    if(rs.next()){
				returnInt = 2;	//重複
			}else{
				returnInt = st.executeUpdate("UPDATE questionnaire SET symptom = '"+symptom+"' WHERE doctorID='"+doctorID+"' and questionnaireID = '"+questionnaireID+"'");
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
	synchronized public static int updateFirstVisitQuestionnaire(Connection conn, String doctorID, String questionnaireID, String symptom) {
		int returnInt = 0;
		try {
			Statement st = conn.createStatement();
			returnInt = st.executeUpdate("UPDATE questionnaire SET symptom = NULL WHERE doctorID='"+doctorID+"' and symptom = '"+symptom+"'");	
			returnInt = st.executeUpdate("UPDATE questionnaire SET symptom = '"+symptom+"' WHERE doctorID='"+doctorID+"' and questionnaireID = '"+questionnaireID+"'");	
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return returnInt;
	}
	public static int deleteQuestionnaire(Connection conn, String doctorID, String questionnaireID) {
		int returnInt = 0;
		String symptomID ="";
		int temp;
		try {
			Statement st = conn.createStatement();
			returnInt = st.executeUpdate("DELETE FROM questionsort where questionnaireID = '"+questionnaireID+"'");
			temp = st.executeUpdate("DELETE FROM questionnaire where doctorID='"+doctorID+"'and questionnaireID = '"+questionnaireID+"'");	
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return returnInt;
	}
	public static ArrayList getFirstVisitQuestionnaire(Connection conn, String doctorID, ArrayList questionnaireList) {
		
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select * FROM questionnaire where doctorID='"+doctorID+"'and symptom is not null ORDER BY CAST(questionnaireID AS UNSIGNED) DESC");
		    while (rs.next()) {
		    	questionnaireList.add(rs.getString("questionnaireID"));
		    	questionnaireList.add(rs.getString("name"));
		    	questionnaireList.add(rs.getString("symptom"));
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
	public static int removeFirstVisitQuestionnaire(Connection conn, String doctorID, String questionnaireID) {
		int returnInt = 0;
		String symptomID ="";
		int temp;
		try {
			Statement st = conn.createStatement();
			returnInt = st.executeUpdate("UPDATE questionnaire SET symptom = NULL WHERE doctorID='"+doctorID+"' and questionnaireID = '"+questionnaireID+"'");	
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		      if (conn!=null) try {conn.close();}catch (Exception ignore) {}
		}
		return returnInt;
	}
	public static ArrayList searchQuestionMedicalRecord(Connection conn, String doctorID, String questionID, ArrayList questionList) {
		
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select * FROM question where doctorID='"+doctorID+"' and questionID = '"+questionID+"'");
		    while (rs.next()) {
				questionList.add(rs.getString("option"));
				questionList.add(rs.getString("medicalRecord"));
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
	public static String checkID(Connection conn, String doctorID, String questionnaireID) {
		String returnString = "";
		
		try {
			Statement st = conn.createStatement();
		    ResultSet rs = st.executeQuery("select * FROM questionnaire where doctorID='"+doctorID+"' and questionnaireID = '"+questionnaireID+"'");
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
