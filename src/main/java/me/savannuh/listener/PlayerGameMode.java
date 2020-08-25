package me.savannuh.listener;

import me.savannuh.resources.StorageManager;
import org.bson.Document;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerGameMode implements Listener {

    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        String gameMode = event.getNewGameMode().toString();
        if (StorageManager.isMongoDBEnabled()) {
            StorageManager.update(uuid, new Document("gamemode", gameMode));
        } else {
            StorageManager.update(new HashMap<>(Map.of("playerdata." + uuid.toString() + ".gamemode", gameMode)));
        }
    }

}
