package com.loficostudios.marketplacePlugin.market;

import com.loficostudios.marketplacePlugin.MarketplacePlugin;
import com.loficostudios.marketplacePlugin.listing.ItemListing;
import com.loficostudios.marketplacePlugin.utils.FileUtils;
import com.loficostudios.marketplacePlugin.utils.MongoDBUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class TransactionLog implements ISavableLoadable {
    private final HashSet<TransactionEntry> logs = new LinkedHashSet<>();

    public void log(double buyPrice, double sellPrice, OfflinePlayer seller, OfflinePlayer buyer, ItemListing listing) {
        var buyerUUID = buyer.getUniqueId();
        var sellerUUID = seller.getUniqueId();

        logs.add(new TransactionEntry(listing.getItem(),
                buyPrice,
                sellPrice,
                buyerUUID,
                sellerUUID));
        asyncSave().whenComplete((saved, ex) -> {
            MarketplacePlugin.getInstance().getLogger().log(Level.INFO, "Logged new transaction entry!");
        });
    }

    public Collection<TransactionEntry> getLogs(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        return logs.stream()
                .filter(Objects::nonNull)
                .filter(log -> log.buyer().equals(uuid) || log.seller().equals(uuid))
                .toList();
    }

    public Collection<TransactionEntry> getLogs() {
        return logs.stream()
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public boolean save() {
        if (!MongoDBUtils.isInited()) {
            return false;
        }
        try {
            var collection = MongoDBUtils.getCollection(MarketplacePlugin.TRANSACTION_COLLECTION);

            for (TransactionEntry entry : logs) {
                Document doc = new Document()
                        .append("item", FileUtils.serializeString(entry.item()))
                        .append("buyPrice", entry.buyPrice())
                        .append("sellPrice", entry.sellPrice())
                        .append("buyer", entry.buyer().toString())
                        .append("seller", entry.seller().toString())
                        .append("time", entry.time());

                collection.insertOne(doc);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public CompletableFuture<Boolean> asyncSave() {
        return CompletableFuture.supplyAsync(this::save,
                task -> Bukkit.getScheduler().runTaskAsynchronously(MarketplacePlugin.getInstance(),this::save));

    }

    @Override
    public boolean load() {
        if (!MongoDBUtils.isInited()) {
            return false;
        }
        try {
            var collection = MongoDBUtils.getDatabase().getCollection(MarketplacePlugin.TRANSACTION_COLLECTION);
            var cursor = collection.find();

            logs.clear();

            for (Document doc : cursor) {
                TransactionEntry entry = TransactionEntry.deserialize(doc);
                if (entry != null) {
                    logs.add(entry);
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public CompletableFuture<Boolean> asyncLoad() {
        return CompletableFuture.supplyAsync(this::load,
                task -> Bukkit.getScheduler().runTaskAsynchronously(MarketplacePlugin.getInstance(),this::load));

    }
}