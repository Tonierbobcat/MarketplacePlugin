package com.loficostudios.marketplacePlugin.market;

import com.loficostudios.marketplacePlugin.MarketplacePlugin;
import com.loficostudios.marketplacePlugin.Messages;
import com.loficostudios.marketplacePlugin.listing.ItemListing;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@SuppressWarnings("LombokGetterMayBeUsed")
public class Market {

    public static final String SELL_PERMISSION = MarketplacePlugin.NAMESPACE + ".sell";

    @Getter
    private BlackMarket blackMarket;

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

    private final Map<UUID, HashSet<ItemListing>> playerListings = new HashMap<>();
    private final HashMap<UUID, ItemListing> allListing = new HashMap<>();

    public ListItemResult listItem(Player player, ItemStack itemStack, double price) {
        if (!player.isOp() && !player.hasPermission(SELL_PERMISSION))
            return ListItemResult.NO_PERMISSION;
        if (price <= 0) {
            return ListItemResult.INVALID_PRICE;
        }
        UUID playerUUID = player.getUniqueId();
        var listing = new ItemListing(player, itemStack, price);
        if (!playerListings.containsKey(player.getUniqueId())) {
            playerListings.put(player.getUniqueId(), new HashSet<>(List.of(listing)));
            return ListItemResult.SUCCESS_NEW;
        }

        var listings = playerListings.get(playerUUID);
        listings.add(listing);
        try {
            playerListings.put(playerUUID, listings);
            save();
            return ListItemResult.SUCCESS;
        } catch (Exception e) {
            return ListItemResult.FAILURE;
        }
    }

    public Collection<ItemListing> getListings(Player player) {
        return playerListings.getOrDefault(player.getUniqueId(), new HashSet<>());
    }

    public Collection<ItemListing> getListings() {
        return allListing.values();
    }

    public ItemListing getListing(UUID uuid) {
        return allListing.get(uuid);
    }

//    public void sell()

    public void save() {
        //save async
//        Bukkit.getScheduler().runTaskAsynchronously()
    }

    public void load() {

    }

}
