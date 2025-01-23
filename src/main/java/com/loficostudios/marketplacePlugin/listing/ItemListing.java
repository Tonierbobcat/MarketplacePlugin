package com.loficostudios.marketplacePlugin.listing;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ItemListing {
    @Getter
    private final UUID uniqueId;
    @Getter
    private final ItemStack item;

    @Getter
    private final UUID sellerUUID;


    @Getter
    private final double price;

    public ItemListing(OfflinePlayer seller, UUID uuid, ItemStack item, double price) {
        this.sellerUUID = seller.getUniqueId();
        this.uniqueId = uuid;
        this.item = item;
        this.price = price;
    }

    public ItemListing(OfflinePlayer seller, ItemStack item, double price) {
        this.sellerUUID = seller.getUniqueId();
        this.uniqueId = UUID.randomUUID();
        this.item = item;
        this.price = price;
    }

    public @NotNull OfflinePlayer getSeller() {
        return Bukkit.getOfflinePlayer(sellerUUID);
    }
}
