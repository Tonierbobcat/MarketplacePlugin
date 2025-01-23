package com.loficostudios.marketplacePlugin.listing;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ItemListing {
    @Getter
    private final UUID itemUUID;
    @Getter
    private final ItemStack item;

    @Getter
    private final UUID sellerUUID;

    public ItemListing(OfflinePlayer seller, UUID uuid, ItemStack item) {
        this.sellerUUID = seller.getUniqueId();
        this.itemUUID = uuid;
        this.item = item;
    }

    public ItemListing(OfflinePlayer seller, ItemStack item) {
        this.sellerUUID = seller.getUniqueId();
        this.itemUUID = UUID.randomUUID();
        this.item = item;
    }

    public @NotNull OfflinePlayer getSeller() {
        return Bukkit.getOfflinePlayer(sellerUUID);
    }
}
