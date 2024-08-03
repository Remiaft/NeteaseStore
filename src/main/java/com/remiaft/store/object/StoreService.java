package com.remiaft.store.object;

import cn.rmc.libs.api.LibsAPIProvider;
import com.remiaft.store.item.Item;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class StoreService {

    @NonNull
    private final String query;
    @NonNull
    private final String done;
    @NonNull
    private final String gameID;
    @NonNull
    private final String sign;


    /**
     * 查询玩家所购买的物品
     *
     * @param player 玩家对象
     * @return 购买物品列表
     */

    public List<Item> query(Player player){
        return query(player.getUniqueId());
    }
    public List<Item> query(UUID puuid) {
        List<Item> items = new ArrayList<>();

        CloseableHttpClient httpClient = HttpClients.createDefault();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("gameid", gameID);
        jsonObject.put("uuid", puuid.toString());

        // 创建Post请求
        HttpPost httpPost = new HttpPost(query);
        // 设置请求头
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Accept", "application/json");
        // 设置签名
        httpPost.setHeader("Netease-Server-Sign", sha256_HMAC("POST/get-mc-item-order-list" + jsonObject.toJSONString(), sign));

        // 设置请求内容
        StringEntity entity = new StringEntity(jsonObject.toString(), "UTF-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);

        // 响应模型
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);

            HttpEntity httpEntity = response.getEntity();

            String content = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
//            System.out.println(content);
            //System.out.println("请求返回: "+content);

            if (content == null) {
                return items;
            }

            JSONObject bought = (JSONObject) new JSONParser().parse(content);

            // 有订单
            // 解析订单
            if (((Number) bought.get("code")).intValue() == 0) {
                JSONArray entities = (JSONArray) bought.get("entities");
                for (Object object : entities) {
                    JSONObject itemJson = (JSONObject) object;

                    long id = (long) itemJson.get("item_id");
                    String uuid = (String) itemJson.get("uuid");
                    int amount = ((Number) itemJson.get("item_num")).intValue();
                    long orderID = Long.parseLong((String) itemJson.get("orderid"));
                    String cmd = (String) itemJson.get("cmd");
                    long buyTime = Long.parseLong((String) itemJson.get("buy_time"));

                    Item item = new Item(id, uuid, orderID, amount, cmd, buyTime);

                    items.add(item);
                }
            }
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
        return items;
    }

    /**
     * 发送请求，告知已处理该订单
     *
     * @param player 玩家
     * @param items  订单列表
     */
//    public void done(Player player, List<Item> items){
//        done(player.getUniqueId(),items);
//    }

    public void done(Player player, List<Item> items) {
        UUID pUUID = player.getUniqueId();
        CloseableHttpClient httpClient = HttpClients.createDefault();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("gameid", gameID);
        jsonObject.put("uuid", pUUID.toString());
        jsonObject.put("orderid_list", items.stream().map(Item::getOrderID).collect(Collectors.toList()));
//        System.out.println(jsonObject.toJSONString());
        // 创建Post请求
        HttpPost httpPost = new HttpPost(done);
        // 设置请求头
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Accept", "application/json");
        // 设置签名
        httpPost.setHeader("Netease-Server-Sign", sha256_HMAC("POST/ship-mc-item-order" + jsonObject.toJSONString(), sign));

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
                System.out.println("[Store] 请求订单处理失败！ @method.done(), des: null content");
                return;
            }

            JSONObject result = (JSONObject) new JSONParser().parse(content);
            int code = ((Number) result.get("code")).intValue();
            if(code == 0){
                publishRedisMessage(player,items);
                System.out.println("[Store] 处理订单 "+items.stream().map(Item::getOrderID).collect(Collectors.toList())+" 成功！");
            }
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
    public void publishRedisMessage(Player player,List<Item> items){

        org.json.JSONArray array = new org.json.JSONArray();
        items.stream().map(Item::toString).map(org.json.JSONObject::new).forEach(array::put);
        LibsAPIProvider.get().getDatabaseManager().getJedisManager()
                    .sendChannelMessage("Store","STORE::"+player.getName()+"::"+player.getUniqueId()+"::"+array.toString());
    }

//    public static void main(String[] args) {
//        System.out.println("加密测试: " + sha256_HMAC("你妈死了", SIGN));
//    }

    private static String sha256_HMAC(String message, String secret) {
        String hash = "";
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] bytes = sha256_HMAC.doFinal(message.getBytes());
            hash = byteArrayToHexString(bytes);
        } catch (Exception e) {

            System.out.println("Error HmacSHA256 ===========" + e.getMessage());
        }
        return hash;
    }

    /**
     * 将加密后的字节数组转换成字符串
     *
     * @param b 字节数组
     * @return 字符串
     */
    private static String byteArrayToHexString(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b != null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1)
                hs.append('0');
            hs.append(stmp);
        }
        return hs.toString().toLowerCase();
    }

}
