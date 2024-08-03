package com.remiaft.store.item.impl;

import com.remiaft.store.item.ItemAdapter;
import org.bukkit.entity.Player;
import org.json.JSONObject;

public class PointAdapter implements ItemAdapter {
    @Override
    public boolean perform(Player player, JSONObject jsonObject) {
        if (jsonObject.isNull("point")) return false;
        int point = jsonObject.getInt("point");

        //Points.getPointsService().addPoint(player.getUniqueId(), point,"网易商城购买");
        return true;
    }
}
