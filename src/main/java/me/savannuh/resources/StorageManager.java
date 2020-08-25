package me.savannuh.resources;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.UpdateOptions;
import me.savannuh.model.PlayerData;
import me.savannuh.playerdata.Main;
import org.bson.Document;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class StorageManager {

    private static Main main;
    private static boolean isMongoDBEnabled;
    private static MongoCollection<Document> collection;

    public static void initialize(Main main) {
        CompletableFuture.runAsync(() -> {
            try {
                StorageManager.main = main;
                isMongoDBEnabled = main.getConfig().getBoolean("mongodb.use");
            } catch (Exception exception) {
                isMongoDBEnabled = true;
                main.getConfig().set("mongodb.use", true);
                main.saveConfig();
            }
            if (isMongoDBEnabled) {
                try {
                    MongoClient mongoClient = new MongoClient(new MongoClientURI(main.getConfig().getString("mongodb.connection")));
                    collection = mongoClient.getDatabase("playerdata").getCollection("users");
                } catch (Exception exception) {
                    isMongoDBEnabled = false;
                    System.out.println("Connection to MongoDB failed. Data will be saved in config.yml. Exception: " + exception.toString());
                }
            }
        });
    }

    public static void insertIfDoesNotExist(PlayerData playerData) {
        CompletableFuture.runAsync(() -> {
            try {
                if (isMongoDBEnabled) {
                    collection.updateOne(new Document("_id", playerData.getUuid().toString()), new Document("$setOnInsert", new Document("x", (int) playerData.getLocation().getX())
                            .append("y", (int) playerData.getLocation().getY())
                            .append("z", (int) playerData.getLocation().getZ())
                            .append("fly", playerData.isFlying())
                            .append("gamemode", playerData.getGameMode().toString())
                            .append("online_time", 0)), new UpdateOptions().upsert(true));
                } else {
                    String path = "playerdata." + playerData.getUuid().toString();
                    if (main.getConfig().getConfigurationSection(path) == null) {
                        main.getConfig().set(path + ".x", (int) playerData.getLocation().getX());
                        main.getConfig().set(path + ".y", (int) playerData.getLocation().getY());
                        main.getConfig().set(path + ".z", (int) playerData.getLocation().getX());
                        main.getConfig().set(path + ".fly", playerData.isFlying());
                        main.getConfig().set(path + ".gamemode", playerData.getGameMode().toString());
                        main.getConfig().set(path + ".online_time", 0);
                        main.saveConfig();
                    }
                }
            } catch (Exception exception) {
                System.out.println("[PlayerData] Data insertion failed at player log in with uuid " + playerData.getUuid().toString() + ". Exception: " + exception.toString());
            }
        });
    }

    public static void update(UUID uuid, Document document) {
        CompletableFuture.runAsync(() -> {
            try {
                if (document.containsKey("online_time")) {
                        MongoCursor<Document> cursor = collection.find(new Document("_id", uuid.toString())).projection(new Document("online_time", 1).append("_id", 0)).iterator();
                        int currentTime = (int) cursor.next().get("online_time");
                        cursor.close();
                        document.put("online_time", currentTime + (int) document.get("online_time"));
                }
                collection.updateOne(new Document("_id", uuid.toString()), new Document("$set", document), new UpdateOptions().upsert(true));
            } catch (Exception exception) {
                System.out.println("[PlayerData] failed to update data with MongoDB. Exception: " + exception.toString());
            }
        });
    }

    public static void update(HashMap<String, Object> hashMap) {
        CompletableFuture.runAsync(() -> {
            try {
                for (String path : hashMap.keySet()) {
                    if (path.contains("online_time")) {
                        hashMap.put(path, main.getConfig().getInt(path) + (int) hashMap.get(path));
                    }
                    main.getConfig().set(path, hashMap.get(path));
                }
                main.saveConfig();
            } catch (Exception exception) {
                System.out.println("[PlayerData] Failed to update data with YAML. Exception: " + exception.toString());
            }
        });
    }

    public static boolean isMongoDBEnabled() {
        return isMongoDBEnabled;
    }

}
