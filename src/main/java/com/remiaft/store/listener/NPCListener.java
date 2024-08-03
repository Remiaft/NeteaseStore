package com.remiaft.store.listener;

import com.remiaft.store.Store;
import com.remiaft.store.inventory.RequestMenu;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NPCListener implements Listener {

    @EventHandler
    public void onRightClick(NPCRightClickEvent e){
        if (e.getNPC().getId() == Store.getNPCID()) {
            //打开菜单
            new RequestMenu(e.getClicker()).open();
        }
    }
}
