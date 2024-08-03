package com.remiaft.store.item;

import com.remiaft.store.item.impl.CmdAdapter;
import com.remiaft.store.item.impl.PointAdapter;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public interface ItemAdapter {
    List<ItemAdapter> adapters = Arrays.asList(new CmdAdapter(),new PointAdapter());

    boolean perform(Player player, JSONObject jsonObject);
}
