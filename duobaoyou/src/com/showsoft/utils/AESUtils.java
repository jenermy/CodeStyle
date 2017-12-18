package com.showsoft.utils;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESUtils {


	    public static String encrypt(String seed, String clearText) {
	        byte[] result = null;
	        try {
	            byte[] rawkey = getRawKey(seed.getBytes());
	            result = encrypt(rawkey, clearText.getBytes());
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        String content = toHex(result);
	        return content;

	    }

	    public static String decrypt(String seed, String encrypted) {
	        // Log.d(TAG, "解密前的seed=" + seed + ",内容为:" + encrypted);
	        byte[] rawKey;
	        try {
	            rawKey = getRawKey(seed.getBytes());
	            byte[] enc = toByte(encrypted);
	            byte[] result = decrypt(rawKey, enc);
	            String coentn = new String(result);
	            // Log.d(TAG, "解密后的内容为:" + coentn);
	            return coentn;
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }

	    }

	    private static byte[] getRawKey(byte[] seed) throws Exception {
	        KeyGenerator kgen = KeyGenerator.getInstance("AES");
	        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");//Android4.22需要用这个
			sr.setSeed(seed);
	        kgen.init(128, sr);
	        SecretKey sKey = kgen.generateKey();
	        byte[] raw = sKey.getEncoded();

	        return raw;
	    }

	    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
	        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
	         Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(
	                new byte[cipher.getBlockSize()]));
	        byte[] encrypted = cipher.doFinal(clear);
	        return encrypted;
	    }

	    private static byte[] decrypt(byte[] raw, byte[] encrypted)
	            throws Exception {
	        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
	         Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(
	                new byte[cipher.getBlockSize()]));
	        byte[] decrypted = cipher.doFinal(encrypted);
	        return decrypted;
	    }

	    public static String toHex(String txt) {
	        return toHex(txt.getBytes());
	    }

	    public static String fromHex(String hex) {
	        return new String(toByte(hex));
	    }

	    public static byte[] toByte(String hexString) {
	        int len = hexString.length() / 2;
	        byte[] result = new byte[len];
	        for (int i = 0; i < len; i++)
	            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
	                    16).byteValue();
	        return result;
	    }

	    public static String toHex(byte[] buf) {
	        if (buf == null)
	            return "";
	        StringBuffer result = new StringBuffer(2 * buf.length);
	        for (int i = 0; i < buf.length; i++) {
	            appendHex(result, buf[i]);
	        }
	        return result.toString();
	    }

	    private static void appendHex(StringBuffer sb, byte b) {
	        final String HEX = "0123456789ABCDEF";
	        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
	    }




    /** 算法/模式/填充 **/
    private static final String CipherMode = "AES/CBC/PKCS5Padding";

    /** 创建密钥 **/
    private static SecretKeySpec createKey(String key) {
        byte[] data = null;
        if (key == null) {
            key = "";
        }
        StringBuffer sb = new StringBuffer(16);
        sb.append(key);
        while (sb.length() < 16) {
            sb.append("0");
        }
        if (sb.length() > 16) {
            sb.setLength(16);
        }
        try {
            data = sb.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new SecretKeySpec(data, "AES");
    }
//    private static SecretKeySpec createKey() {
//        String key = KEY;
//        byte[] data = null;
//        if (key == null) {
//            key = "";
//        }
//        StringBuffer sb = new StringBuffer(16);
//        sb.append(key);
//        while (sb.length() < 16) {
//            sb.append("0");
//        }
//        if (sb.length() > 16) {
//            sb.setLength(16);
//        }
//        try {
//            data = sb.toString().getBytes("UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        return new SecretKeySpec(data, "AES");
//    }


    private static IvParameterSpec createIV(String password) {
        byte[] data = null;
        if (password == null) {
            password = "";
        }
        StringBuffer sb = new StringBuffer(16);
        sb.append(password);
        while (sb.length() < 16) {
            sb.append("0");
        }
        if (sb.length() > 16) {
            sb.setLength(16);
        }


        try {
            data = sb.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new IvParameterSpec(data);
    }

//    private static IvParameterSpec createIV() {
//        String password = IV;
//        byte[] data = null;
//        if (password == null) {
//            password = "";
//        }
//        StringBuffer sb = new StringBuffer(16);
//        sb.append(password);
//        while (sb.length() < 16) {
//            sb.append("0");
//        }
//        if (sb.length() > 16) {
//            sb.setLength(16);
//        }
//        try {
//            data = sb.toString().getBytes("UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        return new IvParameterSpec(data);
//    }

    /** 加密字节数据 **/
    public static byte[] encrypt(byte[] content, String password, String iv) {
        try {
            SecretKeySpec key = createKey(password);
            Cipher cipher = Cipher.getInstance(CipherMode);
            cipher.init(Cipher.ENCRYPT_MODE, key, createIV(iv));
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
//    public static byte[] encrypt(byte[] content) {
//        try {
//            SecretKeySpec key = createKey();
//            Cipher cipher = Cipher.getInstance(CipherMode);
//            cipher.init(Cipher.ENCRYPT_MODE, key, createIV());
//            byte[] result = cipher.doFinal(content);
//            return result;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }


    /** 加密(结果为16进制字符串) **/
    public static String encrypt(String content, String password, String iv) {
        byte[] data = null;
        try {
            data = content.getBytes("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        data = encrypt(data, password, iv);
        String result = byte2hex(data);
        return result;
    }
//    public static String encrypt(String content) {
//        byte[] data = null;
//        try {
//            data = content.getBytes("UTF-8");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        data = encrypt(data);
//        String result = byte2hex(data);
//        return result;
//    }


    /** 解密字节数组 **/
    public static byte[] decrypt(byte[] content, String password, String iv) {
        try {
            SecretKeySpec key = createKey(password);
            Cipher cipher = Cipher.getInstance(CipherMode);
            cipher.init(Cipher.DECRYPT_MODE, key, createIV(iv));
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
//    public static byte[] decrypt(byte[] content) {
//        try {
//            SecretKeySpec key = createKey();
//            Cipher cipher = Cipher.getInstance(CipherMode);
//            cipher.init(Cipher.DECRYPT_MODE, key, createIV());
//            byte[] result = cipher.doFinal(content);
//            return result;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }


    /** 解密(输出结果为字符串) **/
    public static String decrypt(String content, String password, String iv) {
        byte[] data = null;
        try {
            data = hex2byte(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        data = decrypt(data, password, iv);
        if (data == null)
            return null;
        String result = null;
        try {
            result = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
//    public static String decrypt(String content) {
//        byte[] data = null;
//        try {
//            data = hex2byte(content);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        data = decrypt(data);
//        if (data == null)
//            return null;
//        String result = null;
//        try {
//            result = new String(data, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }


    /** 字节数组转成16进制字符串 **/
    public static String byte2hex(byte[] b) { // 一个字节的数，
        StringBuffer sb = new StringBuffer(b.length * 2);
        String tmp = "";
        for (int n = 0; n < b.length; n++) {
            // 整数转成十六进制表示
            tmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (tmp.length() == 1) {
                sb.append("0");
            }
            sb.append(tmp);
        }
        return sb.toString().toUpperCase(); // 转成大写
    }


    /** 将hex字符串转换成字节数组 **/
    private static byte[] hex2byte(String inputString) {
        if (inputString == null || inputString.length() < 2) {
            return new byte[0];
        }
        inputString = inputString.toLowerCase();
        int l = inputString.length() / 2;
        byte[] result = new byte[l];
        for (int i = 0; i < l; ++i) {
            String tmp = inputString.substring(2 * i, 2 * i + 2);
            result[i] = (byte) (Integer.parseInt(tmp, 16) & 0xFF);
        }
        return result;
    }
}
	
