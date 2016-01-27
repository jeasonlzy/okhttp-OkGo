package com.lzy.okhttpdemo.utils;

import android.util.Base64;
import android.util.Log;

import com.qiniu.android.common.Zone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/27
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class QiniuToken {
    private static final String MAC_NAME = "HmacSHA1";
    private static final String ENCODING = "UTF-8";
    private static final String AccessKey = "oz4KpN0t9aCyscKeyJrpqk1EreAEUTHcVGCXsSJo";
    private static final String SecretKey = "2v3Ox3GCcDou6WbJIQE0EcYXEJJa6ZeCy_hbh6Hd";

    public static String getToken() {
        try {
            //构建上传策略
            JSONObject jsonPutPolicy = new JSONObject();
            jsonPutPolicy.put("scope", "okhttpserver");   //上传的空间
            jsonPutPolicy.put("deadline", System.currentTimeMillis() + 3600 * 1000);  //上传请求授权的截止时间

            //将上传策略序列化成为JSON格式
            String putPolicy = jsonPutPolicy.toString();
            //对JSON编码的上传策略进行URL安全的Base64编码，得到待签名字符串
            String encodedPutPolicy = Base64.encodeToString(putPolicy.getBytes(ENCODING), Base64.DEFAULT);
            //使用SecretKey对上一步生成的待签名字符串计算HMAC-SHA1签名
            byte[] sign = hmac_sha1(encodedPutPolicy, SecretKey);
            //对签名进行URL安全的Base64编码：
            String encodedSign = Base64.encodeToString(sign, Base64.DEFAULT);
            //将AccessKey、encodedSign和encodedPutPolicy用:连接起来返回
            return AccessKey + ':' + encodedSign + ':' + encodedPutPolicy;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 使用 HMAC-SHA1 签名方法对对encryptText进行签名
     *
     * @param encryptText 被签名的字符串
     * @param encryptKey  密钥
     */
    public static byte[] hmac_sha1(String encryptText, String encryptKey) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        byte[] data = encryptKey.getBytes(ENCODING);
        SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);//根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        Mac mac = Mac.getInstance(MAC_NAME);//生成一个指定 Mac 算法 的 Mac 对象
        mac.init(secretKey);//用给定密钥初始化 Mac 对象
        byte[] text = encryptText.getBytes(ENCODING);
        return mac.doFinal(text);//完成 Mac 操作
    }

    public void upload() {
        Configuration config = new Configuration.Builder().chunkSize(256 * 1024)  //分片上传时，每片的大小。 默认 256K
                .putThreshhold(512 * 1024)  // 启用分片上传阀值。默认 512K
                .connectTimeout(10) // 链接超时。默认 10秒
                .responseTimeout(60) // 服务器响应超时。默认 60秒
//                .recorder(recorder)  // recorder 分片上传时，已上传片记录器。默认 null
//                .recorder(recorder, keyGen)  // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
                .zone(Zone.zone0) // 设置区域，指定不同区域的上传域名、备用域名、备用IP。默认 Zone.zone0
                .build();
        // 重用 uploadManager。一般地，只需要创建一个 uploadManager 对象
        UploadManager uploadManager = new UploadManager(config);
        String data = "File对象、或 文件路径、或 字节数组";
        String key = "指定七牛服务上的文件名，或 null";
        String token = "从服务端SDK获取";
        uploadManager.put(data, key, token, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject res) {
                //  res 包含hash、key等信息，具体字段取决于上传策略的设置。
                Log.i("qiniu", key + ",\r\n " + info + ",\r\n " + res);
            }
        }, null);
    }
}
