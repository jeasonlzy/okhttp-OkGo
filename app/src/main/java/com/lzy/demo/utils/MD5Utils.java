package com.lzy.demo.utils;

import java.io.File;
import java.io.FileInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2015/10/11
 * 描    述：MD5加密工具类
 * 修订历史：
 * ================================================
 */
public class MD5Utils {

    private MD5Utils() {
    }

    /**
     * 获取字符串的 MD5
     */
    public static String encode(String str) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(str.getBytes("UTF-8"));
            byte messageDigest[] = md5.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                hexString.append(String.format("%02X", b));
            }
            return hexString.toString().toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取文件的 MD5
     */
    public static String encode(File file) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            FileInputStream inputStream = new FileInputStream(file);
            DigestInputStream digestInputStream = new DigestInputStream(inputStream, messageDigest);
            //必须把文件读取完毕才能拿到md5
            byte[] buffer = new byte[4096];
            while (digestInputStream.read(buffer) > -1) {
            }
            MessageDigest digest = digestInputStream.getMessageDigest();
            digestInputStream.close();
            byte[] md5 = digest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : md5) {
                sb.append(String.format("%02X", b));
            }
            return sb.toString().toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
