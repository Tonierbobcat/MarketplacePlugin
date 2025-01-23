package com.loficostudios.marketplacePlugin.utils;

import com.loficostudios.marketplacePlugin.MarketplacePlugin;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.logging.Level;

public class MongoDBUtils {

    @Getter
    private static boolean inited;
    @Getter
    private static MongoClient client;
    @Getter
    private static MongoDatabase database;
    @Getter
    private static MongoCollection<Document> collection;
    public static void initialize(FileConfiguration conf) {
        if (inited)
            return;
        final MarketplacePlugin plugin = MarketplacePlugin.getInstance();
        var lgr = plugin.getLogger();
        final String path = "mongodb.";

        String collection = conf.getString(path + "collection");
        String database = conf.getString(path + "database");
        String username = conf.getString(path + "username");
        String password = conf.getString(path + "password");
        String hostname = conf.getString(path + "hostname");
        int port = conf.getInt(path + "port");

        if (isNullOrEmpty(database) || isNullOrEmpty(username) || isNullOrEmpty(password)) {
            lgr.log(Level.SEVERE, "Invalid database credentials!");
            return;
        }
        if (isNullOrEmpty(hostname)) {
            lgr.log(Level.SEVERE, "Invalid hostname!");
            return;
        }

        ConnectionString connectionString = port > 0
                ? new ConnectionString("mongodb://" + username + ":" + password + "@" + hostname + ":" + port + "/" + database)
                : new ConnectionString("mongodb://" + username + ":" + password + "@" + hostname + "/" + database) ;
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();

        client = MongoClients.create(settings);
        MongoDBUtils.database = client.getDatabase(database);

        if (isNullOrEmpty(collection))  {
            lgr.log(Level.SEVERE, "Invalid collection!");
            return;
        }

        MongoDBUtils.collection = MongoDBUtils.database.getCollection(collection);
        inited = isConnected();
    }

    private static boolean isNullOrEmpty(@Nullable String string) {
        return string == null || string.isEmpty();
    }

    @Deprecated(forRemoval = true)
    private static boolean isConnected() {
        try {
            database.runCommand(new Document("ping", 1));
            return true;
        } catch (Exception ignore) {
            return false;
        }
    }
}
