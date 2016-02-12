package de.hs.settlers.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {
	private static MessageDigest SHA1_DIGEST;

	static {
		try {
			SHA1_DIGEST = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param plaintext
	 * @return String of SHA1-Hash, null if no SHA1 algorithm is available
	 * @author Fabian
	 * @throws NoSuchAlgorithmException
	 */
	public static String hashFunctionSHA(String plaintext) {
		if (SHA1_DIGEST == null) {
			return null;
		}
		byte[] result = SHA1_DIGEST.digest(plaintext.getBytes());
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < result.length; i++) {
			stringBuffer.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
		}

		return stringBuffer.toString();
	}

}
