package com.tamboot.common.utils;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class EncodeUtils {
	private static final Logger logger = LoggerFactory.getLogger(EncodeUtils.class);

	private static final String DEFAULT_URL_ENCODING = "UTF-8";

	private static final char[] BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

	private static final String EMPTY_STRING = "";

	private static final byte[] EMPTY_BYTES = new byte[0];

	public static String encodeHex(byte[] input) {
		if (input == null) {
			return EMPTY_STRING;
		}
		return Hex.encodeHexString(input);
	}

	public static byte[] decodeHex(String input) {
		if (input == null) {
			return EMPTY_BYTES;
		}
		
		try {
			return Hex.decodeHex(input.toCharArray());
		} catch (DecoderException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}
		
		return EMPTY_BYTES;
	}

	public static String encodeBase64(byte[] input) {
		return Base64.encodeBase64String(input);
	}

	public static String encodeUrlSafeBase64(byte[] input) {
		return Base64.encodeBase64URLSafeString(input);
	}

	public static byte[] decodeBase64(String input) {
		return Base64.decodeBase64(input);
	}

	public static String encodeBase62(byte[] input) {
		char[] chars = new char[input.length];
		for (int i = 0; i < input.length; i++) {
			chars[i] = BASE62[(input[i] & 0xFF) % BASE62.length];
		}
		return new String(chars);
	}

	public static String urlEncode(String part) {
		return urlEncode(part, null);
	}
	
	public static String urlEncode(String part, String enc) {
		if (StringUtils.isEmpty(enc)) {
			enc = DEFAULT_URL_ENCODING;
		}
		
		try {
			return URLEncoder.encode(part, enc);
		} catch (UnsupportedEncodingException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}
		
		return EMPTY_STRING;
	}

	public static String urlDecode(String part) {
		return urlDecode(part, null);
	}
	
	public static String urlDecode(String part, String enc) {
		if (StringUtils.isEmpty(enc)) {
			enc = DEFAULT_URL_ENCODING;
		}

		try {
			return URLDecoder.decode(part, enc);
		} catch (UnsupportedEncodingException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}
		
		return EMPTY_STRING;
	}

	public static SecretKey gen3DESKey(byte[] keys) {
		return new SecretKeySpec(keys, "DESede");
	}

	public static byte[] encryptWith3DES(SecretKey key, byte[] keywords) {
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("DESede");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return cipher.doFinal(keywords);
		} catch (NoSuchAlgorithmException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		} catch (NoSuchPaddingException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		} catch (InvalidKeyException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		} catch (IllegalBlockSizeException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		} catch (BadPaddingException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}
		
		return EMPTY_BYTES;
	}

	public static byte[] decryptWith3DES(SecretKey key, byte[] keywords) {
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("DESede");
			cipher.init(Cipher.DECRYPT_MODE, key);
			return cipher.doFinal(keywords);
		} catch (NoSuchAlgorithmException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		} catch (NoSuchPaddingException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		} catch (InvalidKeyException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		} catch (IllegalBlockSizeException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		} catch (BadPaddingException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}
		
		return EMPTY_BYTES;
	}
	
	public static String encryptWith3DESAndBase64(String key,String keyword) {
		SecretKey secretKey = gen3DESKey(key.getBytes());
		return encodeBase64(encryptWith3DES(secretKey,keyword.getBytes()));
	}
	
	public static String decryptWith3DESAndBase64(String key,String keyword) {
		SecretKey secretKey = gen3DESKey(key.getBytes());
		byte[] keywords = decodeBase64(keyword);
		return new String(decryptWith3DES(secretKey,keywords));
	}

	public static String getSHA1(String token, String timestamp, String nonce, String encrypt) {
		try {
			String[] array = new String[] { token, timestamp, nonce, encrypt };

			StringBuffer sb = new StringBuffer();
			Arrays.sort(array);
			for (int i = 0; i < 4; i++) {
				sb.append(array[i]);
			}
			String str = sb.toString();

			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(str.getBytes());
			byte[] digest = md.digest();

			StringBuffer hexstr = new StringBuffer();
			String shaHex = "";
			for (int i = 0; i < digest.length; i++) {
				shaHex = Integer.toHexString(digest[i] & 0xFF);
				if (shaHex.length() < 2) {
					hexstr.append(0);
				}
				hexstr.append(shaHex);
			}
			return hexstr.toString();
		} catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}
		
		return EMPTY_STRING;
	}
}
