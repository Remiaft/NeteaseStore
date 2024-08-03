package com.remiaft.store.item.impl;

import com.remiaft.store.item.ItemAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.JSONObject;

public class CmdAdapter implements ItemAdapter {
    @Override
    public boolean perform(Player player, JSONObject jsonObject) {
        if (jsonObject.isNull("cmd")) return false;
        String cmd = (String) jsonObject.get("cmd");
        cmd = cmd.replace("%player%",player.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),cmd);
        return true;
    }
}
