package com.loficostudios.marketplacePlugin.market;

import com.loficostudios.marketplacePlugin.MarketplacePlugin;
import com.loficostudios.marketplacePlugin.listing.ItemListing;
import com.loficostudios.marketplacePlugin.utils.FileUtils;
import com.loficostudios.marketplacePlugin.utils.MongoDBUtils;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.loficostudios.marketplacePlugin.market.BuyItemResult.Type.*;

@SuppressWarnings("LombokGetterMayBeUsed")
public class Market {

    public static final String SELL_PERMISSION = MarketplacePlugin.NAMESPACE + ".sell";

    private final MarketplacePlugin plugin;

    private final ConcurrentHashMap<UUID, MarketProfile> marketProfiles = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<UUID, ItemListing> allListing = new ConcurrentHashMap<>();

    @Getter
    private BlackMarket blackMarket;

    public Market(MarketplacePlugin plugin) {
        this.plugin = plugin;
        loadAsync();
    }

    public ListItemResult listItem(Player player, ItemStack itemStack, double price) {
        if (!player.isOp() && !player.hasPermission(SELL_PERMISSION))
            return ListItemResult.NO_PERMISSION;
        if (itemStack == null || itemStack.getType() == Material.AIR)
            return ListItemResult.INVALID_ITEM;
        if (price <= 0) {
            return ListItemResult.INVALID_PRICE;
        }
        UUID playerUUID = player.getUniqueId();
        var listing = new ItemListing(player, itemStack, price);


        if (!marketProfiles.containsKey(player.getUniqueId())) {
            marketProfiles.computeIfAbsent(playerUUID, k -> new MarketProfile(player))
                    .add(listing);
            saveAsync();
            return ListItemResult.SUCCESS_NEW;
        }
        else {
            var profile = marketProfiles.get(playerUUID);
            try {
                profile.add(listing);
                marketProfiles.put(playerUUID, profile);
                allListing.put(listing.getUniqueId(), listing);
                player.sendMessage("Added: " + listing.getItem().getType());
                saveAsync();
                return ListItemResult.SUCCESS;
            } catch (Exception e) {
                profile.remove(listing);
                marketProfiles.remove(playerUUID, profile);
                allListing.remove(listing.getUniqueId(), listing);
                return ListItemResult.FAILURE;
            }
        }
    }

    public BuyItemResult buyItem(Player player, UUID itemUUID) {
        var listing = getListing(itemUUID);
        double price = listing.getPrice();
        if (!MarketplacePlugin.getInstance().getEconomy().withdrawPlayer(player, price).transactionSuccess())
            return new BuyItemResult(0, null, NOT_ENOUGHT_MONEY);

        if (!removeListing(listing))
            return new BuyItemResult(0, null, FAILURE);

        player.getInventory().addItem(listing.getItem());
        return new BuyItemResult(price, listing.getItem(), SUCCESS);
    }

    public boolean removeListing(ItemListing listing) {
        UUID uuid = listing.getUniqueId();
        if (!allListing.containsKey(uuid))
            return false;
        var profile = marketProfiles.get(listing.getSellerUUID());
        var lgr = plugin.getLogger();

        if (profile.remove(listing)) {
            lgr.log(Level.INFO, "removed listing from profile");
        }
        if (allListing.remove(uuid, listing)) {
            lgr.log(Level.INFO, "removed listing from all map");
        }

        saveAsync();
        return true;
    }

    public Collection<ItemListing> getListings(Player player) {
        return marketProfiles.getOrDefault(player.getUniqueId(), new MarketProfile(player)).getAll();
    }

    public Collection<ItemListing> getListings() {
        return allListing.values();
    }

    public ItemListing getListing(UUID uuid) {
        return allListing.get(uuid);
    }

    private String saveItem(ItemStack item) {
        return FileUtils.serializeString(item);
    }

    private ItemStack loadItem(String string) {
        return (ItemStack) FileUtils.deserializeString(string, ItemStack.class);
    }

    private void save() {
        Logger lgr = plugin.getLogger();
        MongoCollection<Document> collection = MongoDBUtils.getServerCollection();

        for (Map.Entry<UUID, MarketProfile> profile : marketProfiles.entrySet()) {
            for (ItemListing listing : profile.getValue().getAll()) { //TODO change this to player profile
                Document doc = new Document()
                        .append("uuid", listing.getUniqueId().toString())
                        .append("sellerUUID", listing.getSeller().getUniqueId().toString())
                        .append("price", listing.getPrice())
                        .append("item", saveItem(listing.getItem()));
                collection.replaceOne(
                        new Document("uuid", listing.getUniqueId().toString()),
                        doc,
                        new ReplaceOptions().upsert(true)
                );
            }
        }

        lgr.log(Level.INFO, "Saved market!");
    }

    public void load() {
        Logger lgr = plugin.getLogger();
        MongoCollection<Document> collection = MongoDBUtils.getServerCollection();
        for (Document doc : collection.find()) {
            String s = doc.getString("uuid");
            lgr.log(Level.INFO, "ItemUUID: " + s);
            UUID uuid = UUID.fromString(s);
            String s1 = doc.getString("sellerUUID");
            lgr.log(Level.INFO, "PlayerUUID: " + s1);
            UUID sellerUUID = UUID.fromString(s1);
            double price = doc.getDouble("price");
            ItemStack item = loadItem(doc.getString("item"));

            OfflinePlayer player = Bukkit.getOfflinePlayer(sellerUUID);

            ItemListing listing = new ItemListing(player, uuid, item, price);

            marketProfiles
                    .computeIfAbsent(sellerUUID, k -> new MarketProfile(player))
                    .add(listing);
            allListing.put(uuid, listing);
            lgr.log(Level.INFO, "added entry");
        }
        lgr.log(Level.INFO, "Loaded market!");
    }

    private void saveAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, this::save);
    }

    private void loadAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, this::load);
    }
}
