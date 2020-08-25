package me.savannuh.listener;

import me.savannuh.resources.StorageManager;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.HashMap;
import java.util.Map;

public class PlayerToggleFlight implements Listener {

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        boolean fly = event.isFlying();
        if (StorageManager.isMongoDBEnabled()) {
            StorageManager.update(player.getUniqueId(), new Document("fly", fly));
        } else {
            StorageManager.update(new HashMap<>(Map.of("playerdata." + player.getUniqueId().toString() + ".fly", fly)));
        }
    }

}
