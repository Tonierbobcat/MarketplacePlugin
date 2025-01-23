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

import java.util.logging.Level;

import static com.loficostudios.marketplacePlugin.utils.Common.isNullOrEmpty;

public class MongoDBUtils {

    @Getter
    private static boolean inited;
    @Getter
    private static MongoClient client;
    @Getter
    private static MongoDatabase database;
    @Getter
    private static MongoCollection<Document> serverCollection;
    public static void initialize(FileConfiguration conf) {
        if (inited)
            return;
        final MarketplacePlugin plugin = MarketplacePlugin.getInstance();
        var lgr = plugin.getLogger();
        final String path = "mongodb.";

        String collection = "market";

        String database = conf.getString(path + "database");
        String username = conf.getString(path + "username");
        String password = conf.getString(path + "password");
        String hostname = conf.getString(path + "hostname");
        int port = conf.getInt(path + "port");

        lgr.log(Level.INFO, "MongoDB Database: " + database);
        lgr.log(Level.INFO, "MongoDB Hostname: " + hostname);
        lgr.log(Level.INFO, "MongoDB Port: " + port);

        String pass = isNullOrEmpty(password) ? "" : "****";
        lgr.log(Level.INFO, "MongoDB Username: " + username);
        lgr.log(Level.INFO, "MongoDB Password: " + pass);

        if (isNullOrEmpty(database)) {
            lgr.log(Level.SEVERE, "Invalid database credentials!");
            return;
        }
        if (isNullOrEmpty(hostname)) {
            lgr.log(Level.SEVERE, "Invalid hostname!");
            return;
        }

        String usernameAndPassword = "";
        if (!isNullOrEmpty(username)) {
            if (!isNullOrEmpty(password)) {
                usernameAndPassword = username + ":" + password + "@";
            } else {
                usernameAndPassword = username + "@";
            }
        }
        var base = !isNullOrEmpty(database)
                ? "/" + database
                : "";
        String string = port > 0
                ? "mongodb://" + usernameAndPassword + hostname + ":" + port + base
                : "mongodb://" + usernameAndPassword + hostname + base;
        lgr.log(Level.INFO, "Connection string: " + string);
        ConnectionString connectionString = new ConnectionString(string);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();

        client = MongoClients.create(settings);
        MongoDBUtils.database = client.getDatabase(database);

        if (isNullOrEmpty(collection))  {
            lgr.log(Level.SEVERE, "Invalid serverCollection!");
            return;
        }

        MongoDBUtils.serverCollection = MongoDBUtils.database.getCollection(collection);
        var connected = isConnected();
        lgr.log(Level.INFO, "Is Connected: " + connected);
        inited = connected;
    }


//    @Deprecated(forRemoval = true)
    private static boolean isConnected() {
        final MarketplacePlugin plugin = MarketplacePlugin.getInstance();
        var lgr = plugin.getLogger();
        try {
            database.runCommand(new Document("ping", 1));
            return true;
        } catch (Exception e) {
            lgr.log(Level.SEVERE, "Connection failed", e);
            return false;
        }
    }
}
