
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class Test {

    public static void aain(String[] args) {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        JSONObject jsonObject = new JSONObject();
        JSONArray array = new JSONArray();
        array.put("77140593557373952");
        jsonObject.put("entity_ids",array);
//        System.out.println(jsonObject.toJSONString());
        // 创建Post请求
        HttpPost httpPost = new HttpPost("https://x19mclobt.nie.netease.com/item-download-info/query/search-by-ids");
        // 设置请求头
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Accept", "application/json");
        // 设置签名
        // httpPost.setHeader("Netease-Server-Sign", sha256_HMAC("POST/ship-mc-item-order" + jsonObject.toJSONString(), sign));

        // 设置请求内容
        StringEntity entity = new StringEntity(jsonObject.toJSONString(), "UTF-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);

        // 响应模型
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);

            HttpEntity httpEntity = response.getEntity();

            String content = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);

//            System.out.println("请求返回: "+content);
            if(content == null){
                System.out.println("请求失败");
                return;
            }

            JSONObject result = (JSONObject) new JSONParser().parse(content);
            System.out.println(result);

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        if(response != null){
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            httpPost.releaseConnection();
            httpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static final String i = "https://bind.clay-api.com/confirm";
    public static final Pattern ALLATORIxDEMO = Pattern.compile("^[\\u4e00-\\u9fa5a-zA-Z0-9_]+$");

    public static void main(String[] args) throws Throwable{
        Thread.sleep(1000L);
        HttpURLConnection var3 = (HttpURLConnection)(new URL(i)).openConnection();
        var3.setConnectTimeout(5000);
        var3.setReadTimeout(5000);
        var3.setRequestProperty("Content-Type", "application/json");
        var3.setRequestMethod("POST");
        var3.setDoOutput(true);
        var3.setDoInput(true);
        var3.setUseCaches(false);
        if (var3 instanceof HttpsURLConnection) {
            SSLContext var4;
            (var4 = SSLContext.getInstance("TLSv1.2")).init(null, new TrustManager[]{new X509TrustManager(){

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection var10000 = (HttpsURLConnection)var3;
            var10000.setSSLSocketFactory(var4.getSocketFactory());
            var10000.setHostnameVerifier((ax, a1) -> true);
        }

        var3.connect();
        BufferedWriter var10001 = new BufferedWriter(new OutputStreamWriter(var3.getOutputStream(), StandardCharsets.UTF_8));
        var10001.write("{\"name\":\"nmsl\",\"uuid\":\""+ UUID.randomUUID() +"\"}");
        var10001.close();
        System.out.println(var3.getResponseCode());
    }

//    public static CompletableFuture<Result> ALLATORIxDEMO(String a2, String a3, String a4) {
//        a3 = String.format("{\"name\": \"%s\",\"uuid\": \"%s\",\"aim\": \"%s\"}", a2, a3, a4);
//        return z.ALLATORIxDEMO(int, a3);
//    }
}
