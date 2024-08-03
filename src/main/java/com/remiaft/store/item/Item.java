package com.remiaft.store.item;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import org.json.JSONObject;


/**
 * 商品
 */
@Getter @RequiredArgsConstructor
public class Item {

    private final long id; // 商品ID
    private final String uuid; // 玩家uuid
    private final long orderID; // 订单ID
    private final int num; // 购买数量
    private final String json; // 礼包命令
    private final long buyTime; // 购买时间戳
    private long performTime;

    @SneakyThrows
    public boolean perform(Player player){
        JSONObject jsonObject = new JSONObject(json);
        boolean result = false;
        for (ItemAdapter adapter : ItemAdapter.adapters) {
            for (int i = 0; i < num; i++) {
                if (adapter.perform(player,jsonObject)) {
                    result = true;
                }
            }
        }
        performTime = System.currentTimeMillis();
        return result;
    }


    @Override
    public String toString() {
        JSONObject jo = new JSONObject();
        jo.put("id",id);
        jo.put("number",num);
        jo.put("cmd",new JSONObject(json));
        jo.put("buyTime",buyTime);
        jo.put("performTime", performTime == 0L ? System.currentTimeMillis() /1000:performTime);
        return jo.toString();
    }
}
