package com.loficostudios.marketplacePlugin.market;

import com.loficostudios.marketplacePlugin.listing.ItemListing;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public interface IMarket {
    double getPrice(OfflinePlayer buyer, OfflinePlayer seller, ItemListing listing);
    Map<UUID, ItemListing> getListings();

    ItemListing getListing(UUID uuid);

    ListItemResult listItem(Player player, ItemStack item, double price);
    BuyItemResult buyItem(Player buyer, UUID itemUUID);
}
