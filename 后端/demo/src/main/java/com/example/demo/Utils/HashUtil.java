package com.example.demo.Utils;

import java.security.MessageDigest;

public class HashUtil {
    static String salt = "xyymyylyyfyymyyrmmhtlhtl";

    public static String commonHash(String str) {
        MessageDigest md5 = null;//创建一个md5算法对象
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];

        byte[] md5Bytes = md5.digest(byteArray);

        StringBuilder hexValue = new StringBuilder();

        for (byte md5Byte : md5Bytes) {
            int val = ((int) md5Byte) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }

        return hexValue.toString();
    }

    public static String saltHash(String str) {
        return commonHash(str + salt);
    }

    public static boolean verifyHash(String str, String hash) {
        return saltHash(str).equals(hash);
    }

    public static void main(String[] args) {
        System.out.println(saltHash("zhangsan"));
        System.out.println(saltHash("lisi"));
        System.out.println(saltHash("wangwu"));
        System.out.println(verifyHash("zhangsan", "8bb4833c0c62c6180d86674f6db9b59c"));
    }

}
