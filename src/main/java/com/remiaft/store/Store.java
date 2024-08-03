package com.remiaft.store;

import cn.rmc.libs.api.LibsAPIProvider;
import cn.rmc.libs.common.database.DataBase;
import cn.rmc.libs.spigot.util.Config;
import cn.rmc.libs.spigot.util.ap.Plugin;
import cn.rmc.libs.spigot.util.ap.PluginDependency;
import com.remiaft.store.command.StoreCommand;
import com.remiaft.store.listener.NPCListener;
import com.remiaft.store.listener.PlayerListener;
import com.remiaft.store.object.StoreService;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;


@Plugin(name = "Store",authors = "Yeoc",depends = {
        @PluginDependency("Libs"),@PluginDependency("Points"),@PluginDependency("Citizens")
})
public final class Store extends JavaPlugin {
    @Getter
    private static String prefix;
    @Getter
    private static Config cfg;

    @Getter
    private static Store instance;

    @Getter
    private static HashMap<Player,Long> lastUse;


    @Getter
    private static StoreService storeService;
    @Getter
    private static DataBase dataBase;
    @Getter @Setter
    private static int NPCID;


//    private static

    @Override @SneakyThrows
    public void onEnable() {
        instance = this;
        lastUse = new HashMap<>();
        dataBase = LibsAPIProvider.get().getDatabaseManager().getDatabase("info",3);
        if (!getDataFolder().exists()) saveResource("config.yml",false);
        cfg = new Config(new File(getDataFolder(), "config.yml"));
        prefix = cfg.getString("prefix","§bStore §7» §f");
        String query_url = cfg.getString("query_url");
        String done_url = cfg.getString("done_url");
        String gameid = cfg.getString("gameid");
        String sign = cfg.getString("sign");
        NPCID = cfg.getInt("npcid",-1);
        cfg.save();
        storeService = new StoreService(query_url,done_url,gameid,sign);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(storeService),this);
        Bukkit.getPluginManager().registerEvents(new NPCListener(),this);
        // Plugin startup logic
//        storeService = new StoreService();
        LibsAPIProvider.get().getCommandManager().registerCommand(this, StoreCommand.class);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
