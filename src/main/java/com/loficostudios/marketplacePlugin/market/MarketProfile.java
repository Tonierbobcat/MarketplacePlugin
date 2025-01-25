package com.loficostudios.marketplacePlugin.market;

import com.loficostudios.marketplacePlugin.market.listing.ItemListing;
import lombok.Getter;
import org.bukkit.OfflinePlayer;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MarketProfile {
    private final ConcurrentHashMap<UUID, ItemListing> listings = new ConcurrentHashMap<>();

    @Getter
    private final UUID playerUUID;

    public MarketProfile(OfflinePlayer player) {
        this.playerUUID = player.getUniqueId();
    }

    public void add(ItemListing listing) {
        listings.put(listing.getUniqueId(), listing);
    }
    public boolean remove(ItemListing listing) {
        if (!listings.containsKey(listing.getUniqueId()))
            return false;
        return listings.remove(listing.getUniqueId(), listings.get(listing.getUniqueId()));
    }
    public ItemListing get(UUID uuid) {
        return listings.get(uuid);
    }
    public ConcurrentHashMap<UUID, ItemListing> getMap() {
        return new ConcurrentHashMap<>(listings);
    }
    public Collection<ItemListing> getAll() {
        return listings.values();
    }
}
