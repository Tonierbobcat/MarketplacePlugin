package com.loficostudios.marketplacePlugin.market;

import com.loficostudios.marketplacePlugin.listing.ItemListing;
import lombok.Getter;
import org.bukkit.OfflinePlayer;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class MarketProfile {
    private final HashMap<UUID, ItemListing> listings = new HashMap<>();

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
    public Collection<ItemListing> getAll() {
        return listings.values();
    }
}
