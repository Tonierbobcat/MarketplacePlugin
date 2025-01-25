package com.loficostudios.marketplacePlugin.gui;

import com.loficostudios.marketplacePlugin.config.MarketConfig;
import com.loficostudios.marketplacePlugin.MarketplacePlugin;
import com.loficostudios.marketplacePlugin.market.listing.ItemListing;
import com.loficostudios.marketplacePlugin.market.api.IMarket;
import com.loficostudios.marketplacePlugin.utils.Common;
import com.loficostudios.marketplacePlugin.utils.GuiUtils;
import com.loficostudios.melodyapi.gui.GuiIcon;
import com.loficostudios.melodyapi.gui.MelodyGui;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MarketPageGui extends MelodyGui {
    @Getter
    private static final List<MarketPageGui> instances = new ArrayList<>();

    private final Map<Integer, Integer> listingSlotMap = new HashMap<>();
    {
        //region FIRST ROW
        listingSlotMap.put(0, 10);
        listingSlotMap.put(1, 11);
        listingSlotMap.put(2, 12);
        listingSlotMap.put(3, 13);
        listingSlotMap.put(4, 14);
        listingSlotMap.put(5, 15);
        listingSlotMap.put(6, 16);
        //endregion


        //region SECOND ROW
        listingSlotMap.put(7, 19);
        listingSlotMap.put(8, 20);
        listingSlotMap.put(9, 21);
        listingSlotMap.put(10, 22);
        listingSlotMap.put(11, 23);
        listingSlotMap.put(12, 24);
        listingSlotMap.put(13, 25);
        //endregion

        //region THIRD
        listingSlotMap.put(14, 28);
        listingSlotMap.put(15, 29);
        listingSlotMap.put(16, 30);
        listingSlotMap.put(17, 31);
        listingSlotMap.put(18, 32);
        listingSlotMap.put(19, 33);
        listingSlotMap.put(20, 34);
        //endregion
    }

    private final int itemsPerPage = listingSlotMap.size();
    private final int pageIndex;

    @Getter
    private final IMarket market;

    public MarketPageGui(@NotNull IMarket market, int page) {
        super(53, MarketConfig.MARKET_TITLE.replace("{page}", "" + (page + 1)));
        this.pageIndex = page;
        this.market = market;
        displayListings(market);
        instances.add(this);
    }

    private void displayListings(IMarket market) {
        this.clear();

        for (Integer i : GuiUtils.getPerimeter(6, 9)) {
            setSlot(i, new GuiIcon(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), "fill"));
        }

        if (pageIndex > 0) {
            setSlot(38, new GuiIcon(GuiUtils.getButtonItem(
                    MarketConfig.MARKET_BACK_BUTTON_LABEL.replace("{previousPage}", "" + ((pageIndex + 1) - 1)),
                    Common.materialFromName(MarketConfig.MARKET_BACK_BUTTON_MATERIAL)),
                    "", this::lastPage));
        }
        setSlot(42, new GuiIcon(GuiUtils.getButtonItem(
                MarketConfig.MARKET_NEXT_BUTTON_LABEL.replace("{nextPage}", "" + ((pageIndex + 1) + 1)),
                Common.materialFromName(MarketConfig.MARKET_NEXT_BUTTON_MATERIAL)),
                "", this::nextPage));

        Bukkit.getScheduler().runTaskAsynchronously(MarketplacePlugin.getInstance(), () -> {
            var listings = market.getListings().values().stream().toList();

            int start = pageIndex * itemsPerPage;
            int end = Math.min(start + itemsPerPage, listings.size());

            if (start >= listings.size()) {
                start = listings.size();
            }

            var items = listings.stream().toList().subList(start, end);

            int index = 0;
            for (ItemListing item : items) {
                setSlot(listingSlotMap.get(index), item.getGuiIcon(this::onClick));
                index++;
            }
        });
    }

    private void nextPage(Player player, GuiIcon icon) {
        new MarketPageGui(this.market, (pageIndex + 1)).open(player);
    }

    private void lastPage(Player player, GuiIcon icon) {
        if (pageIndex > 0) {
            new MarketPageGui(this.market, pageIndex - 1).open(player);
        }
    }

    private void onClick(Player player, GuiIcon icon) {
        UUID uuid = UUID.fromString(icon.getId());
        var listing = market.getListing(uuid);

        close(player);
        new ConfirmationGui(this.market, this, listing).open(player);
    }

    @Override
    public void refresh() {
        displayListings(market);
    }

    public int getPage() {
        return this.pageIndex;
    }
}
