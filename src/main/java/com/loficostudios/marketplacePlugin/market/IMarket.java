package com.loficostudios.marketplacePlugin.market;

import com.loficostudios.marketplacePlugin.listing.ItemListing;
import org.bukkit.OfflinePlayer;

public interface IMarket {
    double getPrice(OfflinePlayer buyer, OfflinePlayer seller, ItemListing listing);
}
