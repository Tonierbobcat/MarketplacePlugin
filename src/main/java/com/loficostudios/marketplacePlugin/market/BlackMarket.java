package com.loficostudios.marketplacePlugin.market;

import com.loficostudios.marketplacePlugin.MarketplacePlugin;
import com.loficostudios.marketplacePlugin.listing.ItemListing;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.joml.Random;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class BlackMarket extends AbstractMarket {

    private static final int blackMarketSize = 10;

    @Getter
    private final IMarket base;

    private final Economy economy;

    private final HashMap<UUID, MarketProfile> marketProfiles = new HashMap<>();

    public BlackMarket(IMarket base) {
        this.base = base;
        this.economy = MarketplacePlugin.getInstance().getEconomy();

        generateListings();
    }

    private void generateListings() {
        if (!marketProfiles.isEmpty())
            marketProfiles.clear();

        List<ItemListing> listingsFromBaseMarket = new ArrayList<>(base.getListings().values());

        if (listingsFromBaseMarket.isEmpty())
            return;

        for (int i = 0; i < blackMarketSize; i++) {
            var randomIndex = new Random().nextInt(listingsFromBaseMarket.size());
            var listing = listingsFromBaseMarket.get(randomIndex);

            var sellerUUID = listing.getSellerUUID();

            if (!marketProfiles.containsKey(sellerUUID)) {
                marketProfiles.computeIfAbsent(sellerUUID, k -> new MarketProfile(listing.getSeller()))
                        .add(listing);
            }
            var profile = marketProfiles.get(sellerUUID);
            profile.add(listing);
        }
    }

    @Override
    public double getPrice(OfflinePlayer buyer, OfflinePlayer seller, ItemListing listing) {
        return listing.getPrice() * 0.5;
    }

    @Override
    public Map<UUID, ItemListing> getListings() {
        return marketProfiles.values().stream()
                .map(MarketProfile::getMap)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existing, replacement) -> replacement
                ));
    }



    @Override
    public ItemListing getListing(UUID uuid) {
        return marketProfiles.values().stream()
                .map(MarketProfile::getMap)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existing, replacement) -> replacement
                )).get(uuid);
    }

    @Override
    public MarketProfile getMarketProfile(UUID uuid) {
        return marketProfiles.get(uuid);
    }

    @Override
    public Map<UUID, MarketProfile> getMarketProfiles() {
        return marketProfiles;
    }

    @Override
    public boolean removeListing(ItemListing listing) {
        var sellerUUID = listing.getSellerUUID();
        var profile = marketProfiles.get(sellerUUID);

        profile.remove(listing);

        return base.removeListing(listing);
    }

    @Override
    public ListItemResult listItem(Player player, ItemStack item, double price) {
        return ListItemResult.FAILURE;
    }

    @Override
    public BuyItemResult buyItem(Player buyer, UUID itemUUID) {
        var listing = base.getListing(itemUUID);
        if (listing == null) {
            return new BuyItemResult(0, null, BuyItemResult.Type.INVALID_LISTING);
        }
        var seller = listing.getSeller();

        double buyPrice = getPrice(buyer, seller, listing);
        double sellPrice = listing.getPrice() * 1.5; //150% of original sell price

        if (!economy.has(buyer, buyPrice))
            return new BuyItemResult(0, null, BuyItemResult.Type.NOT_ENOUGHT_MONEY);
        if (!removeListing(listing))
            return new BuyItemResult(0, null, BuyItemResult.Type.FAILURE);
        if (!economy.depositPlayer(seller, sellPrice).transactionSuccess())
            return new BuyItemResult(0, null, BuyItemResult.Type.SELLER_TRANSACTION_FAILURE);
        if (!economy.withdrawPlayer(buyer, buyPrice).transactionSuccess())
            return new BuyItemResult(0, null, BuyItemResult.Type.BUYER_TRANSACTION_FAILURE);

        buyer.getInventory().addItem(listing.getItem());
        return new BuyItemResult(buyPrice, listing.getItem(), BuyItemResult.Type.SUCCESS);
    }
}
