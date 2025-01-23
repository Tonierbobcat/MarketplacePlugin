package com.loficostudios.marketplacePlugin.market;

import com.loficostudios.marketplacePlugin.listing.ItemListing;

import java.util.HashMap;
import java.util.HashSet;

@Deprecated
public class MarketProfile {

    private final HashSet<ItemListing> listings = new HashSet<>();
    public void addListing(ItemListing listing) {
        listings.add(listing);
    }
}
