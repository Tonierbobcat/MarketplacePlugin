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
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("LombokGetterMayBeUsed")
public class Market {

    public static final String SELL_PERMISSION = MarketplacePlugin.NAMESPACE + ".sell";

    @Getter
    private BlackMarket blackMarket;

    public Market() {
        load();
    }

    private final Map<UUID, MarketProfile> marketProfiles = new HashMap<>();
    private final HashMap<UUID, ItemListing> allListing = new HashMap<>();

    public ListItemResult listItem(Player player, ItemStack itemStack, double price) {
        if (!player.isOp() && !player.hasPermission(SELL_PERMISSION))
            return ListItemResult.NO_PERMISSION;
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

        var profile = marketProfiles.get(playerUUID);

        profile.add(listing);
        try {
            marketProfiles.put(playerUUID, profile);
            saveAsync();
            return ListItemResult.SUCCESS;
        } catch (Exception e) {
            return ListItemResult.FAILURE;
        }
    }

    public void buyItem(Player player, UUID itemUUID) {
        var listing = getListing(itemUUID);
        if (MarketplacePlugin.getInstance().getEconomy().withdrawPlayer(player, listing.getPrice()).transactionSuccess()) {
            if (removeListing(listing)) {
                player.getInventory().addItem(listing.getItem());
            }
        }
    }

    public boolean removeListing(ItemListing listing) {
        if (!allListing.containsKey(listing.getUniqueId()))
            return false;
        var profile = marketProfiles.get(listing.getSellerUUID());
        return profile.remove(listing);
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

    private void saveAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(MarketplacePlugin.getInstance(), this::save);
    }

    private void save() {
        Logger lgr = MarketplacePlugin.getInstance().getLogger();
        MongoCollection<Document> collection = MongoDBUtils.getServerCollection();

        for (Map.Entry<UUID, MarketProfile> profile : marketProfiles.entrySet()) {
            MarketplacePlugin.getInstance().getServer().broadcastMessage("Found entry");
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
        Logger lgr = MarketplacePlugin.getInstance().getLogger();
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

}
