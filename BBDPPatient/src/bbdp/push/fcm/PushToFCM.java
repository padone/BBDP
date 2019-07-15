package bbdp.push.fcm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class PushToFCM {
	private static String POST_URL = "https://fcm.googleapis.com/fcm/send";
	//private static String FCM_KEY = "AIzaSyDkFFJMsw8_XllqXljoSfW3lcdzv96-BA8";
	private static String FCM_KEY = "AIzaSyDxbyoWc-IWZGaLzBROvjcJeyYaSipTjTk";

	public static void sendNotification(String title, String body, String patientID, String hyperlink) {
		String message = "{\"data\": {\"body\": \"" + body + "\",\"title\": \"" + title + "\",\"hyperlink\": \"" + hyperlink + "\"},\"notification\": {\"body\": \"" + body + "\",\"title\": \"" + title + "\",\"click_action\": \"FCM_PLUGIN_ACTIVITY\",\"sound\": \"default\"},\"to\": \"/topics/patient" + patientID + "\"}";
		
		try {
			URL obj = new URL(POST_URL);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
			con.setRequestProperty("Authorization", "key=" + FCM_KEY);
			
			//System.out.println("send message: " + message);
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(wr, StandardCharsets.UTF_8));
			writer.write(message);
			writer.close();
			wr.close();

			int responseCode = con.getResponseCode();
			//System.out.println("POST Response Code: " + responseCode);

			if (responseCode == HttpURLConnection.HTTP_OK) {		//success
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				//System.out.println(response.toString());		//print result
			} else {
				System.out.println("PushToFCM POST request not worked");
			}
		} catch (MalformedURLException e) {
			System.out.println("PushToFCM MalformedURLException");
		} catch (ProtocolException e) {
			System.out.println("PushToFCM ProtocolException");
		} catch (IOException e) {
			System.out.println("PushToFCM IOException");
		}
	}
	
	public static void sendNotification(String title, String body, String patientID) {
		String hyperlink = "Setting.html";
		String message = "{\"data\": {\"body\": \"" + body + "\",\"title\": \"" + title + "\",\"hyperlink\": \"" + hyperlink + "\"},\"notification\": {\"body\": \"" + body + "\",\"title\": \"" + title + "\",\"click_action\": \"FCM_PLUGIN_ACTIVITY\",\"sound\": \"default\"},\"to\": \"/topics/patient" + patientID + "\"}";
		
		try {
			URL obj = new URL(POST_URL);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
			con.setRequestProperty("Authorization", "key=" + FCM_KEY);
			
			//System.out.println("send message: " + message);
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(wr, StandardCharsets.UTF_8));
			writer.write(message);
			writer.close();
			wr.close();

			int responseCode = con.getResponseCode();
			//System.out.println("POST Response Code: " + responseCode);

			if (responseCode == HttpURLConnection.HTTP_OK) {		//success
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				//System.out.println(response.toString());		//print result
			} else {
				System.out.println("PushToFCM POST request not worked");
			}
		} catch (MalformedURLException e) {
			System.out.println("PushToFCM MalformedURLException");
		} catch (ProtocolException e) {
			System.out.println("PushToFCM ProtocolException");
		} catch (IOException e) {
			System.out.println("PushToFCM IOException");
		}
	}
}