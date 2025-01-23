package com.loficostudios.marketplacePlugin.market;

import com.loficostudios.marketplacePlugin.listing.ItemListing;
import com.loficostudios.marketplacePlugin.utils.MongoDBUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Market {

    Map<UUID, HashSet<ItemListing>> playerListings = new HashMap<>();



    public void listItem(Player player, ItemStack itemStack) {
        UUID uuid = player.getUniqueId();
        var listing = new ItemListing(player, itemStack);
        if (!playerListings.containsKey(player.getUniqueId())) {
            playerListings.put(player.getUniqueId(), new HashSet<>(List.of(listing)));
            return;
        }

        var itemList = playerListings.get(uuid);
        itemList.add(listing);
        save();
    }

    public void save() {
        //save async
//        Bukkit.getScheduler().runTaskAsynchronously()
    }

    public void load() {

    }

}
