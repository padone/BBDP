package bbdp.encryption.base64;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class BBDPBase64 {
	public static String encode(String input) {
		byte[] textByte = null;
        textByte = input.getBytes(StandardCharsets.UTF_8);
        final String encodedText = Base64.getEncoder().encodeToString(textByte);
		return encodedText;
	}
	public static String decode(String input) {
		String output = "";
        output = new String(Base64.getDecoder().decode(input), StandardCharsets.UTF_8);
        return output;
	}
	/*public static void main(String args[]) {
		String input = "abcde";
		System.out.println(input);
		System.out.println("encode: " + encode(input));
		System.out.println("decode: " + decode(encode(input)));
	}*/
}