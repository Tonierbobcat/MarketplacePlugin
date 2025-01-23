package com.loficostudios.marketplacePlugin.market;

import com.loficostudios.marketplacePlugin.listing.ItemListing;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class BlackMarket implements IMarket {
    public BlackMarket(IMarket base) {

    }

    @Override
    public double getPrice(OfflinePlayer buyer, OfflinePlayer seller, ItemListing listing) {
        return 0;
    }

    @Override
    public Map<UUID, ItemListing> getListings() {
        return Map.of();
    }

    @Override
    public ItemListing getListing(UUID uuid) {
        return null;
    }

    @Override
    public ListItemResult listItem(Player player, ItemStack item, double price) {
        return null;
    }

    @Override
    public BuyItemResult buyItem(Player buyer, UUID itemUUID) {
        return null;
    }
}
