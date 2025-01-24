package com.loficostudios.marketplacePlugin.market;

import com.loficostudios.marketplacePlugin.utils.FileUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.UUID;

public record TransactionEntry(ItemStack item, double buyPrice, double sellPrice, UUID buyer, UUID seller, long time) {
    public TransactionEntry(
            ItemStack item,
            double buyPrice,
            double sellPrice,
            UUID buyer,
            UUID seller
    ) {
        this(item, buyPrice, sellPrice, buyer, seller, System.currentTimeMillis());
    }
    public String getSellerName() {
        return Objects.requireNonNullElse(Bukkit.getOfflinePlayer(seller).getName(), seller.toString());
    }
    public String getBuyerName() {
        return Objects.requireNonNullElse(Bukkit.getOfflinePlayer(buyer).getName(), buyer.toString());
    }

    public static TransactionEntry deserialize(Document document) {
        try {
            var item = (ItemStack) FileUtils.deserializeString(document.getString("item"), ItemStack.class);
            double buyPrice = document.getDouble("buyPrice");
            double sellPrice = document.getDouble("sellPrice");
            UUID buyer = UUID.fromString(document.getString("buyer"));
            UUID seller = UUID.fromString(document.getString("seller"));
            long time = document.getLong("time");

            return new TransactionEntry(item, buyPrice, sellPrice, buyer, seller, time);
        } catch (Exception e) {
            return null;
        }
    }


}
