package me.savannuh.model;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerData {

    private UUID uuid;
    private Location location;
    private boolean fly;
    private GameMode gameMode;

    public PlayerData(Player player) {
        this.uuid = player.getUniqueId();
        this.location = player.getLocation();
        this.fly = player.isFlying();
        this.gameMode = player.getGameMode();
    }

    public UUID getUuid() {
        return uuid;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isFlying() {
        return fly;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

}
