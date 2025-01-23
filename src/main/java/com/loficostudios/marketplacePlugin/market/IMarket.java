package com.loficostudios.marketplacePlugin.market;

import com.loficostudios.marketplacePlugin.listing.ItemListing;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public interface IMarket {

    ListItemResult listItem(Player player, ItemStack item, double price);

    boolean removeListing(ItemListing listing);

    BuyItemResult buyItem(Player buyer, UUID itemUUID);

    double getPrice(OfflinePlayer buyer, OfflinePlayer seller, ItemListing listing);

    ItemListing getListing(UUID uuid);
    ConcurrentHashMap<UUID, ItemListing> getListings();

    IMarket onUpdate(Consumer<IMarket> onUpdate);

    MarketProfile getMarketProfile(UUID uuid);
    ConcurrentHashMap<UUID, MarketProfile> getMarketProfiles();
    default MarketProfile getMarketProfile(OfflinePlayer player) {
        return getMarketProfile(player.getUniqueId());
    }
}
