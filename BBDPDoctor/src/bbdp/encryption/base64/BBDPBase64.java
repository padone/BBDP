package bbdp.encryption.base64;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class BBDPBase64 {
	public static String encode(String input) {
		byte[] textByte = null;
		try {
			textByte = input.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			System.out.println("BBDPBase64 encode UnsupportedEncodingException: " + e);
		}
		final String encodedText = Base64.getEncoder().encodeToString(textByte);
		return encodedText;
	}
	public static String decode(String input) {
		String output = "";
		try {
			output = new String(Base64.getDecoder().decode(input), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			System.out.println("BBDPBase64 decode UnsupportedEncodingException: " + e);
		}
		return output;
	}
	/*public static void main(String args[]) {
		String input = "abcde";
		System.out.println(input);
		System.out.println("encode: " + encode(input));
		System.out.println("decode: " + decode(encode(input)));
	}*/
}