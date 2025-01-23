package com.loficostudios.marketplacePlugin.gui.api;

import com.loficostudios.marketplacePlugin.MarketplacePlugin;
import com.loficostudios.marketplacePlugin.listing.ItemListing;
import com.loficostudios.marketplacePlugin.market.Market;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MarketPageGui extends Gui {
    @Getter
    private static List<MarketPageGui> instances = new ArrayList<>();

    private static final int itemsPerPage = 20;
    private Market market;
    private int size;
    private final int page;
    public MarketPageGui(MarketplacePlugin plugin, int page) {
        this.market = plugin.getActiveMarket();
        this.page = page;
        displayListings(market);
        instances.add(this);
    }



    private void displayListings(Market market) {
        clear();
        var listings = market.getListings().values().stream().toList();

        int start = page * itemsPerPage;
        int end = Math.min(start + itemsPerPage, listings.size());

        if (start >= listings.size()) {
            start = listings.size();
        }

        var items = listings.stream().toList().subList(start, end);
        this.size = items.size();

        int index = 0;
        for (ItemListing item : items) {
            setSlot(index, item.getGuiIcon(this::onClick));
            index++;
        }
    }


    private void onClick(Player player, GuiIcon icon)
    {
        UUID uuid = UUID.fromString(icon.getId());
        var listing = market.getListing(uuid);

        close(player);
        new ConfirmationGui(this.market, this, listing).open(player);
    }

    @Override
    protected @NotNull Integer getSize() {
        return size;
    }

    @Override
    protected @Nullable String getTitle() {
        return "";
    }

    @Override
    public void refresh() {
        displayListings(market);
        MarketplacePlugin.getInstance().getServer().broadcastMessage("refreshed gui");
    }
}
