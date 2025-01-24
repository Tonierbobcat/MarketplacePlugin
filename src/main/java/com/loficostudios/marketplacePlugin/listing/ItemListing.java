package com.loficostudios.marketplacePlugin.listing;

import com.loficostudios.marketplacePlugin.config.MarketConfig;
import com.loficostudios.melodyapi.gui.GuiIcon;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
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
//        MarketplacePlugin.getInstance().getServer().broadcastMessage("(ItemListing::new) UUID: " + this.uniqueId + ". Material: " + item.getType());
    }

    public ItemListing(OfflinePlayer seller, ItemStack item, double price) {
        this.sellerUUID = seller.getUniqueId();
        this.uniqueId = UUID.randomUUID();
        this.item = item;
        this.price = price;
//        MarketplacePlugin.getInstance().getServer().broadcastMessage("(ItemListing::new) UUID: " + this.uniqueId + ". Material: " + item.getType());
    }

    public @NotNull OfflinePlayer getSeller() {
        return Bukkit.getOfflinePlayer(sellerUUID);
    }

    public GuiIcon getGuiIcon(@Nullable BiConsumer<Player, GuiIcon> onClick) {
        var newItem = new ItemStack(this.item);
        var meta = newItem.getItemMeta();
        OfflinePlayer seller = getSeller();
        String name = Objects.requireNonNullElse(seller.getName(), "" + seller.getUniqueId());
        if (meta != null) {
            var lore = getDescription(meta, name);
            meta.setLore(lore);
        }
        newItem.setItemMeta(meta);

        var icon = new GuiIcon(newItem, this.uniqueId.toString(), onClick);

        return icon;

    }

    private @NotNull List<String> getDescription(ItemMeta meta, String name) {

        var lore = meta.getLore();
        if (lore == null) {
            lore = Arrays.asList(
                    MarketConfig.MARKET_LISTING_PRICE.replace("{price}", "" + price),
                    MarketConfig.MARKET_LISTING_PLAYER.replace("{player}", name)
            );
        }
        else {
            lore.addFirst(MarketConfig.MARKET_LISTING_PRICE.replace("{price}", "" + price));
            lore.addFirst(MarketConfig.MARKET_LISTING_PLAYER.replace("{player}", name));
        }
        return lore;
    }
}
