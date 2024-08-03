package com.remiaft.store.inventory;

import cn.rmc.libs.spigot.inventory.AbstractClickableItem;
import cn.rmc.libs.spigot.inventory.MenuBasic;
import cn.rmc.libs.spigot.item.ItemBuilder;
import com.remiaft.store.command.StoreCommand;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class RequestMenu extends MenuBasic {
    public RequestMenu(Player p) {
        super(p, "§b§l收货系统", 3);
    }

    @Override
    public void setup() {
        String id = p.getUniqueId().toString().substring(p.getUniqueId().toString().length() - 8);
        ItemBuilder request = new ItemBuilder(Material.NETHER_STAR).setName("§b§l签收按钮");
        request.addLoreLine("");
        request.addLoreLine("§e签收人代号: §b"+id);
        request.addLoreLine("");
        request.addLoreLine("§7签收在网易商城购买的物品.");
        request.addLoreLine("");
        request.addLoreLine("§e点击签收!");
        inventoryUI.setItem(13, new AbstractClickableItem(request.toItemStack()) {
            @Override
            public void onClick(InventoryClickEvent e) {
                StoreCommand.request(p);
                p.closeInventory();
            }
        });
    }
}
