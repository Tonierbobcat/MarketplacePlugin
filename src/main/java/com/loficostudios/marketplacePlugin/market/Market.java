package com.loficostudios.marketplacePlugin.market;

import com.loficostudios.marketplacePlugin.listing.ItemListing;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.ItemList;

import java.util.*;

public class Market {

    Map<UUID, HashSet<ItemListing>> listings  = new HashMap<>();



    public void listItem(Player player, ItemStack itemStack) {
//        var listing = new ItemListing(pl)
    }

    public void save() {

    }

    public void load() {

    }

}
