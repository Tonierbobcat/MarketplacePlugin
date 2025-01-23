package com.loficostudios.marketplacePlugin.listing;

import com.loficostudios.marketplacePlugin.MarketplacePlugin;
import com.loficostudios.marketplacePlugin.gui.api.GuiIcon;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
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
        MarketplacePlugin.getInstance().getServer().broadcastMessage("(ItemListing::new) UUID: " + this.uniqueId + ". Material: " + item.getType());
    }

    public ItemListing(OfflinePlayer seller, ItemStack item, double price) {
        this.sellerUUID = seller.getUniqueId();
        this.uniqueId = UUID.randomUUID();
        this.item = item;
        this.price = price;
        MarketplacePlugin.getInstance().getServer().broadcastMessage("(ItemListing::new) UUID: " + this.uniqueId + ". Material: " + item.getType());
    }

    public @NotNull OfflinePlayer getSeller() {
        return Bukkit.getOfflinePlayer(sellerUUID);
    }

    public GuiIcon getGuiIcon(@Nullable BiConsumer<Player, GuiIcon> onClick) {
        var newItem = new ItemStack(this.item);
        var meta = newItem.getItemMeta();
        if (meta != null) {
            var lore = meta.getLore();
            if (lore == null) {
                lore = Arrays.asList(
                    "Price: " + price,
                    "Seller: " + getSeller().getName()
                );
            }
            else {
                lore.addFirst("Price: " + price);
                lore.addFirst("Seller: " + getSeller().getName());
            }

            meta.setLore(lore);
        }
        newItem.setItemMeta(meta);

        var icon = new GuiIcon(newItem, this.uniqueId.toString(), onClick);

        MarketplacePlugin.getInstance().getServer().broadcastMessage("(ItemListing#getIcon) GUI Icon for listing " + this.uniqueId + ". Material: " + icon.getItem().getType() + " Original Material: " + item.getType());

        return icon;

    }
}
