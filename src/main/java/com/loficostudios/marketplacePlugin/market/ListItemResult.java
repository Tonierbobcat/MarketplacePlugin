package com.loficostudios.marketplacePlugin.market;

import com.loficostudios.marketplacePlugin.Messages;
import lombok.Getter;

public enum ListItemResult {
    NO_PERMISSION(Messages.MARKET_NO_PERMISSION_TO_LIST_ITEM),
    INVALID_PRICE(Messages.INVALID_PRICE),
    INVALID_ITEM(Messages.INVALID_ITEM),
    FAILURE(Messages.MARKET_LIST_ITEM_FAILURE),
    SUCCESS(Messages.MARKET_LISTED_ITEM),
    SUCCESS_NEW(Messages.MARKET_FIRST_TIME_LISTED_ITEM);

    @Getter
    private final String message;
    ListItemResult(String message) {
        this.message =message;
    }

    public static boolean isSuccess(ListItemResult result) {
        return result.equals(SUCCESS) || result.equals(SUCCESS_NEW);
    }
}
