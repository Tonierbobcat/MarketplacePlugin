package com.loficostudios.marketplacePlugin.market;

import com.loficostudios.marketplacePlugin.Messages;
import lombok.Getter;

public enum ListItemResult {
    NO_PERMISSION(Messages.MARKET_NO_PERMISSION_TO_LIST_ITEM),
    INVALID_PRICE(Messages.INVALID_PRICE),
    FAILURE(Messages.FAILURE),
    SUCCESS(Messages.MARKET_LISTED_ITEM),
    SUCCESS_NEW(Messages.MARKET_FIRST_TIME_LISTED_ITEM);

    @Getter
    private final String message;
    ListItemResult(String message) {
        this.message =message;
    }
}
