package com.remiaft.store.listener;

import com.remiaft.store.Store;
import com.remiaft.store.item.Item;
import com.remiaft.store.object.StoreService;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

@AllArgsConstructor
public class PlayerListener implements Listener {
    StoreService storeService;

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Bukkit.getScheduler().runTaskAsynchronously(Store.getInstance(),()->{
            List<Item> query = storeService.query(e.getPlayer());
            if (query.size() != 0){
                sendMessage(e.getPlayer(), "你有 %d 个充值订单未处理，请前往快递员领取.",query.size());
            }
        });

    }
    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Store.getLastUse().remove(e.getPlayer());
    }

    private static void sendMessage(CommandSender sender, String str, Object... obj){
        sender.sendMessage(Store.getPrefix()+String.format(str,obj));
    }
}
