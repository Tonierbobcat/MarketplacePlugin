package com.loficostudios.marketplacePlugin.market;

import com.loficostudios.marketplacePlugin.MarketplacePlugin;
import com.loficostudios.marketplacePlugin.market.listing.ItemListing;
import com.loficostudios.marketplacePlugin.market.api.AbstractMarket;
import com.loficostudios.marketplacePlugin.market.interfaces.ISavableLoadable;
import com.loficostudios.marketplacePlugin.market.transactionlog.TransactionLog;
import com.loficostudios.marketplacePlugin.utils.FileUtils;
import com.loficostudios.marketplacePlugin.utils.MongoDBUtils;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@SuppressWarnings("LombokGetterMayBeUsed")
public class Market extends AbstractMarket implements ISavableLoadable {

    public static final String SELL_PERMISSION = MarketplacePlugin.NAMESPACE + ".sell";

    private final MarketplacePlugin plugin;

    private final ConcurrentHashMap<UUID, MarketProfile> marketProfiles = new ConcurrentHashMap<>();

    private final Economy economy;

    @Getter
    private TransactionLog transactionLog = new TransactionLog();

    public Market(MarketplacePlugin plugin) {
        this.economy = plugin.getEconomy();
        this.plugin = plugin;
    }

    public ListItemResult listItem(Player player, ItemStack item, double price) {
        if (!player.isOp() && !player.hasPermission(SELL_PERMISSION))
            return ListItemResult.NO_PERMISSION;
        if (item == null || item.getType() == Material.AIR)
            return ListItemResult.INVALID_ITEM;
        if (price <= 0) {
            return ListItemResult.INVALID_PRICE;
        }
        UUID playerUUID = player.getUniqueId();

        var listing = new ItemListing(player, new ItemStack(item), price);


        if (!marketProfiles.containsKey(player.getUniqueId())) {
            marketProfiles.computeIfAbsent(playerUUID, k -> new MarketProfile(player))
                    .add(listing);
            asyncSave();
            return ListItemResult.SUCCESS_NEW;
        }
        else {
            var profile = marketProfiles.get(playerUUID);
            try {
                profile.add(listing);
                marketProfiles.put(playerUUID, profile);
//                player.sendMessage("Added: " + listing.getItem().getType());
                asyncSave();
//                MarketPageGui.getInstances().forEach(MarketPageGui::refresh); //todo add to onUpdate
                onUpdate.accept(this);
                return ListItemResult.SUCCESS;
            } catch (Exception e) {
                profile.remove(listing);
                marketProfiles.remove(playerUUID, profile);
                return ListItemResult.FAILURE;
            }
        }
    }

    @Override
    public BuyItemResult buyItem(Player buyer, UUID itemUUID) {
        var listing = getListing(itemUUID);
        if (listing == null) {
            return new BuyItemResult(0, null, BuyItemResult.Type.INVALID_LISTING);
        }
        var seller = listing.getSeller();

        double buyPrice = getPrice(buyer, seller, listing);

        double sellPrice = buyPrice;

        if (!economy.has(buyer, buyPrice))
            return new BuyItemResult(0, null, BuyItemResult.Type.NOT_ENOUGHT_MONEY);
        if (!removeListing(listing))
            return new BuyItemResult(0, null, BuyItemResult.Type.FAILURE);
        if (!economy.depositPlayer(seller, sellPrice).transactionSuccess())
            return new BuyItemResult(0, null, BuyItemResult.Type.SELLER_TRANSACTION_FAILURE);
        if (!economy.withdrawPlayer(buyer, buyPrice).transactionSuccess())
            return new BuyItemResult(0, null, BuyItemResult.Type.BUYER_TRANSACTION_FAILURE);

        buyer.getInventory().addItem(listing.getItem());
        transactionLog.log(buyPrice, sellPrice, listing.getSeller(), buyer, listing);
        return new BuyItemResult(buyPrice, listing.getItem(), BuyItemResult.Type.SUCCESS);
    }

    @Override
    public boolean removeListing(ItemListing listing) {
        UUID uuid = listing.getUniqueId();
        if (!getListings().containsKey(uuid))
            return false;
        var profile = marketProfiles.get(listing.getSellerUUID());
        var lgr = plugin.getLogger();

        if (profile.remove(listing)) {
            lgr.log(Level.INFO, "removed listing from profile");
        }

        onUpdate.accept(this);

//        MarketPageGui.getInstances().forEach(MarketPageGui::refresh); //todo move to onUpdate

        try {
            MongoDBUtils.getCollection(MarketplacePlugin.MARKET_COLLECTION)
                    .deleteOne(new Document("uuid", uuid.toString()));
            lgr.log(Level.INFO, "removed listing from database");
        } catch (Exception e) {
            lgr.log(Level.SEVERE, "failed to remove listing from database", e);
            return false;
        }

        asyncSave();
        return true;
    }

//    public Collection<ItemListing> getListings(Player player) {
//        return marketProfiles.getOrDefault(player.getUniqueId(), new MarketProfile(player)).getAll();
//    }

    @Override
    public ConcurrentHashMap<UUID, ItemListing> getListings() {
        return marketProfiles.values().stream()
                .map(MarketProfile::getMap)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toConcurrentMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existing, replacement) -> replacement,
                        ConcurrentHashMap::new
                ));
    }

    @Override
    public MarketProfile getMarketProfile(UUID uuid) {
        return marketProfiles.get(uuid);
    }

    @Override
    public ConcurrentHashMap<UUID, MarketProfile> getMarketProfiles() {
        return this.marketProfiles;
    }

    @Override
    public ItemListing getListing(UUID uuid) {
        // The reason why I am not just calling getListings().get is because I want to minimize overhead
        return marketProfiles.values().stream()
                .map(MarketProfile::getMap)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existing, replacement) -> replacement
                )).get(uuid);
    }

    private String saveItem(ItemStack item) {
        return FileUtils.serializeString(item);
    }

    private ItemStack loadItem(String string) {
        return (ItemStack) FileUtils.deserializeString(string, ItemStack.class);
    }

    @Override
    public boolean save() {
        if (!MongoDBUtils.isInited()) {
            return false;
        }
        Logger lgr = plugin.getLogger();

        try {
            MongoCollection<Document> collection = MongoDBUtils.getCollection(MarketplacePlugin.MARKET_COLLECTION);
            for (Map.Entry<UUID, MarketProfile> profile : marketProfiles.entrySet()) {
                for (ItemListing listing : profile.getValue().getAll()) {
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
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean load() {
        Logger lgr = plugin.getLogger();
        try {
            MongoCollection<Document> collection = MongoDBUtils.getCollection(MarketplacePlugin.MARKET_COLLECTION);
            for (Document doc : collection.find()) {
                String s = doc.getString("uuid");
                //lgr.log(Level.INFO, "ItemUUID: " + s);
                UUID uuid = UUID.fromString(s);
                String s1 = doc.getString("sellerUUID");
                //lgr.log(Level.INFO, "PlayerUUID: " + s1);
                UUID sellerUUID = UUID.fromString(s1);
                double price = doc.getDouble("price");
                ItemStack item = loadItem(doc.getString("item"));

                OfflinePlayer player = Bukkit.getOfflinePlayer(sellerUUID);

                ItemListing listing = new ItemListing(player, uuid, item, price);

                marketProfiles
                        .computeIfAbsent(sellerUUID, k -> new MarketProfile(player))
                        .add(listing);
            }
            lgr.log(Level.INFO, "Loaded market!");
            var loaded = transactionLog.load();
            if (loaded) {
                lgr.log(Level.INFO, "Loaded transaction logs!");
            }
            return loaded;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public CompletableFuture<Boolean> asyncSave() {
        return CompletableFuture.supplyAsync(this::save, task -> Bukkit.getScheduler().runTaskAsynchronously(plugin, this::save));
    }
    @Override
    public CompletableFuture<Boolean> asyncLoad() {
        return CompletableFuture.supplyAsync(this::load, task ->
                Bukkit.getScheduler().runTaskAsynchronously(plugin, task)
        );
    }

    @Override
    public double getPrice(OfflinePlayer buyer, OfflinePlayer seller, ItemListing listing) {
        return listing.getPrice();
    }
}
