package com.loficostudios.marketplacePlugin.gui.api;

import com.loficostudios.marketplacePlugin.MarketplacePlugin;
import com.loficostudios.marketplacePlugin.listing.ItemListing;
import com.loficostudios.marketplacePlugin.market.Market;
import com.loficostudios.marketplacePlugin.utils.ColorUtils;
import com.loficostudios.marketplacePlugin.utils.Common;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ConfirmationGui extends Gui {


    private final MarketPageGui lastPage;

    private final Market market;

    private final ItemListing listing;

    public ConfirmationGui(Market market, MarketPageGui page, ItemListing listing) {
        this.market = market;
        this.lastPage = page;
        this.listing = listing;
        setSlot(11, new GuiIcon(getButtonItem("&a&lAccept", Material.LIME_STAINED_GLASS_PANE), "confirm", this::confirm));
        setSlot(13, listing.getGuiIcon(null));
        setSlot(15, new GuiIcon(getButtonItem("&c&lCancel", Material.RED_STAINED_GLASS_PANE), "cancel", this::cancel));

    }

    private ItemStack getButtonItem(String name, Material type) {
        var item = new ItemStack(type);
        var meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ColorUtils.deserialize(name));
        }
        item.setItemMeta(meta);
        return item;
    }

    private void confirm(Player player, GuiIcon icon) {
        UUID uuid = listing.getUniqueId();

        var result =  market.buyItem(player, uuid);
        ItemStack item = result.item();

        String itemName = null;
        if (item != null) {
            ItemMeta meta = item.getItemMeta();
            itemName = meta != null ? meta.getDisplayName() : item.getType().name();
        }

        Common.sendMessage(player, result.type().getMessage()
                .replace("{item}", itemName != null ? itemName : "null")
                .replace("{price}", "" + result.price()));
        close(player);
    }

    private void cancel(Player player, GuiIcon icon) {
        this.close(player);
        this.lastPage.open(player);
    }

    @Override
    protected @NotNull Integer getSize() {
        return 27;
    }


    @Override
    protected @Nullable String getTitle() {
        return "";
    }
}
