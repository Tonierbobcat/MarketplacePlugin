package com.loficostudios.marketplacePlugin.listing;

import com.loficostudios.marketplacePlugin.MarketplacePlugin;
import com.loficostudios.marketplacePlugin.gui.api.GuiIcon;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.BiConsumer;

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

    public GuiIcon getGuiIcon(@Nullable BiConsumer<Player, GuiIcon> onClick) {

        return new GuiIcon(item, this.uniqueId.toString(), onClick);

    }
}
