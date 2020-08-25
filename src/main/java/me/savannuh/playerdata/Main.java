package me.savannuh.playerdata;

import me.savannuh.listener.PlayerGameMode;
import me.savannuh.listener.PlayerJoinQuit;
import me.savannuh.listener.PlayerToggleFlight;
import me.savannuh.resources.StorageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getConfig().options().copyDefaults(true);
        this.getConfig().addDefault("mongodb.use", true);
        this.getConfig().addDefault("mongodb.connection", "mongodb://127.0.0.1:27017");
        this.saveConfig();
        Bukkit.getPluginManager().registerEvents(new PlayerGameMode(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinQuit(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerToggleFlight(), this);
        System.out.println("[PlayerData] successfully  enabled");
        StorageManager.initialize(this);
    }

    @Override
    public void onDisable() {
        System.out.println(ChatColor.GREEN + "[PlayerData] successfully  disabled");
    }

}
