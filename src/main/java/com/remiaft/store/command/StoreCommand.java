package com.remiaft.store.command;

import cn.rmc.libs.api.command.annotation.Command;
import cn.rmc.libs.api.command.annotation.CommandExecutor;
import cn.rmc.libs.api.command.annotation.Sender;
import cn.rmc.libs.common.database.KeyValue;
import com.remiaft.store.Store;
import com.remiaft.store.item.Item;
import com.remiaft.store.object.StoreService;
import lombok.SneakyThrows;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@CommandExecutor(name = "store",permission = "group.default")
public class StoreCommand {
    @Command(subName = "request",usage = "请求获得充值物品",sync = false)
    public static void request(@Sender Player player){
        if (System.currentTimeMillis() - Store.getLastUse().getOrDefault(player,0L) < 5000){
            sendMessage(player,"请不要频繁尝试处理订单!");
            return;
        }
        Store.getLastUse().put(player,System.currentTimeMillis());
        StoreService storeService = Store.getStoreService();
        sendMessage(player,"正在请求你的订单数据...");
        List<Item> query = storeService.query(player);

        if (query.size() == 0){
            sendMessage(player,"§c你没有未处理的订单!");
            return;
        }
        sendMessage(player,"开始处理你的所有订单, 订单数量: %d",query.size());
        List<Item> success = new ArrayList<>();
        for (Item item : query) {
            boolean perform = false;
            try {
                perform = item.perform(player);
            }catch (Exception ignore){}
            if (perform){
                success.add(item);
                KeyValue add = new KeyValue().add("OrderID", item.getOrderID()).add("UUID", player.getUniqueId().toString())
                        .add("Number",item.getNum()).add("ProductID",item.getId())
                        .add("Command",item.getJson()).add("BuyTimeStamp",item.getBuyTime());
                Store.getDataBase().dbInsert("order",add);
                sendMessage(player,"§a处理订单 #%s 成功!",item.getOrderID());
            }else {
                sendMessage(player,"§c处理订单 #%s 失败! 请联系管理员, 商品ID: %s",item.getOrderID(),item.getId());
            }
        }
        storeService.done(player,success);

        sendMessage(player,"订单处理完毕, 感谢支持!");

    }
    @Command(subName = "setNPC",permission = "store.admin") @SneakyThrows
    public void setNPC(@Sender Player player,int id){
        Store.setNPCID(id);
        Store.getCfg().set("npcid",id);
        Store.getCfg().save();
        sendMessage(player,"设置NPCID成功!");
    }
    private static void sendMessage(CommandSender sender,String str,Object... obj){
        sender.sendMessage(Store.getPrefix()+String.format(str,obj));
    }

}
