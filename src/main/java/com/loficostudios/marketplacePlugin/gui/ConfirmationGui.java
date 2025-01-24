package com.loficostudios.marketplacePlugin.gui;

import com.loficostudios.marketplacePlugin.config.MarketConfig;
import com.loficostudios.marketplacePlugin.gui.api.Gui;
import com.loficostudios.marketplacePlugin.gui.api.GuiIcon;
import com.loficostudios.marketplacePlugin.listing.ItemListing;
import com.loficostudios.marketplacePlugin.market.api.IMarket;
import com.loficostudios.marketplacePlugin.utils.Common;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.loficostudios.marketplacePlugin.utils.GuiUtils.getButtonItem;

public class ConfirmationGui extends Gui {


    private final int lastPage;

    private final IMarket market;

    private final ItemListing listing;

    public ConfirmationGui(IMarket market, MarketPageGui gui, ItemListing listing) {
        super(27, MarketConfig.MARKET_CONFIRMATION_TITLE);

        this.market = market;
        this.lastPage = gui.getPage();
        this.listing = listing;

        setSlot(11, new GuiIcon(getButtonItem(
                MarketConfig.MARKET_CONFIRMATION_CONFIRM_BUTTON_LABEL,
                Common.materialFromName(MarketConfig.MARKET_CONFIRMATION_CONFIRM_BUTTON_MATERIAL)),
                "", this::confirm));

        setSlot(13, listing.getGuiIcon(null));

        setSlot(15, new GuiIcon(getButtonItem(
                MarketConfig.MARKET_CONFIRMATION_CANCEL_BUTTON_LABEL,
                Common.materialFromName(MarketConfig.MARKET_CONFIRMATION_CANCEL_BUTTON_MATERIAL)),
                "", this::cancel));
    }

    private void confirm(Player player, GuiIcon icon) {
        UUID uuid = listing.getUniqueId();

        var result =  market.buyItem(player, uuid);

        Common.sendMessage(player, result.type().getMessage()
                .replace("{item}", Common.getItemName(result.item()))
                .replace("{price}", "" + result.price()));
        close(player);
        new MarketPageGui(market, lastPage).open(player);
    }

    private void cancel(Player player, GuiIcon icon) {
        this.close(player);
        new MarketPageGui(market, lastPage).open(player);
    }
}
