package com.loficostudios.marketplacePlugin.market;

import com.loficostudios.marketplacePlugin.Messages;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

public record BuyItemResult(double price, ItemStack item, Type type) {
    public enum Type {
        NO_PERMISSION(Messages.MARKET_NO_PERMISSION_TO_BUY),
        INVALID_LISTING(Messages.INVALID_LISTING),
        SELLER_TRANSACTION_FAILURE(Messages.SELLER_TRANSACTION_NOT_SUCCESSFUL),
        BUYER_TRANSACTION_FAILURE(Messages.BUYER_TRANSACTION_NOT_SUCCESSFUL),
        NOT_ENOUGHT_MONEY(Messages.MARKET_NOT_ENOUGH_TO_BUY),
        FAILURE(Messages.MARKET_BUY_ITEM_FAILURE),
        SUCCESS(Messages.MARKET_BUY);

        @Getter
        private final String message;
        Type(String message) {
            this.message = message;
        }
    }
}
