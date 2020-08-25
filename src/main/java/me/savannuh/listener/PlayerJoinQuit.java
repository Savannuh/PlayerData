package me.savannuh.listener;

import me.savannuh.model.PlayerData;
import me.savannuh.resources.StorageManager;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerJoinQuit implements Listener {

    private HashMap<UUID, Long> loginMap = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        loginMap.put(player.getUniqueId(), new Date().getTime());
        StorageManager.insertIfDoesNotExist(new PlayerData(player));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (loginMap.containsKey(player.getUniqueId())) {
            int time = (int) (new Date().getTime() - loginMap.get(event.getPlayer().getUniqueId())) / 60000;
            if (StorageManager.isMongoDBEnabled()) {
                StorageManager.update(player.getUniqueId(), new Document("_id", player.getUniqueId().toString())
                        .append("online_time", time)
                        .append("x", (int) player.getLocation().getX())
                        .append("y", (int) player.getLocation().getY())
                        .append("z", (int) player.getLocation().getZ()));
            } else {
                String path = "playerdata." + player.getUniqueId().toString();
                StorageManager.update(new HashMap<>(Map.of(path + ".online_time", time,
                        path + ".x",
                        (int) player.getLocation().getX(),
                        path + ".y", (int) player.getLocation().getY(),
                        path + ".z", (int) player.getLocation().getZ())));
            }
        }
    }
}
