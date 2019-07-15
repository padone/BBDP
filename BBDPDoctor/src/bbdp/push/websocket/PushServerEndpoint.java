package bbdp.push.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONException;
import org.json.JSONObject;

//json form example: {"doctorID":"1","patientID":"1000"}, if the messsage send from doctor, the form will be {"doctorID":"1","patientID":"doctor"}

@ServerEndpoint("/PushServerEndpoint")
public class PushServerEndpoint {
	static List<Session> usernameList = Collections.synchronizedList(new ArrayList<Session>());

	@OnOpen
	public void onOpen(Session userSession) {
		usernameList.add(userSession);
		//System.out.println("websocket onOpen!");
	}

	@OnMessage
	public void onMessage(String message, Session userSession) throws IOException {
		String username = (String) userSession.getUserProperties().get("username");
		ListIterator<Session> listIterator = usernameList.listIterator();
		//convert message string to json
		String doctor = null;
		String patient = null;
		try {
			JSONObject obj = new JSONObject(message);
			doctor = obj.getString("doctorID");
			patient = obj.getString("patientID");
		} catch (JSONException e) {
			System.out.println("JSONObject exception!");
			userSession.getBasicRemote().sendText("JSONObject exception!");
		}
		
		if (username == null && patient.equals("doctor")) {		//第一次的醫生 -> 放ID到List裡
			userSession.getUserProperties().put("username", doctor);
			userSession.getBasicRemote().sendText(message);
		} else if (username != null && patient.equals("doctor")) {		//不是第一次的醫生 -> 不做事
			userSession.getBasicRemote().sendText(message);
		} else if (!patient.equals("doctor")) {		//是病患
			while (listIterator.hasNext()) {		//找病患給的醫生ID是否在List裡，有就把message傳給該醫生
				String temp = (String) listIterator.next().getUserProperties().get("username");
				if(doctor.equals(temp)) {
					listIterator.previous().getBasicRemote().sendText(message);
					listIterator.next();
				}
			}
		} else {		//都不是 -> 不做事
			userSession.getBasicRemote().sendText(message);
		}
	}
	
	@OnError
	public void OnError(Session userSession, Throwable t) {
		System.out.println("websocket OnError: " + (String) userSession.getUserProperties().get("username"));
		System.out.println("websocket OnError Throwable: " + t);
		usernameList.remove(userSession);
	}
	
	@OnClose
	public void onClose(Session userSession) {
		//System.out.println("websocket onClose: " + (String) userSession.getUserProperties().get("username"));
		usernameList.remove(userSession);
	}
}