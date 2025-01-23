package com.loficostudios.marketplacePlugin.command;

import com.loficostudios.marketplacePlugin.MarketplacePlugin;
import com.loficostudios.marketplacePlugin.Messages;
import com.loficostudios.marketplacePlugin.command.impl.Command;
import com.loficostudios.marketplacePlugin.gui.api.MarketPageGui;
import com.loficostudios.marketplacePlugin.market.ListItemResult;
import com.loficostudios.marketplacePlugin.market.Market;
import com.loficostudios.marketplacePlugin.utils.Common;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MarketCommand implements Command {

    private final Market market;

    public MarketCommand(Market market) {
        this.market = market;
    }

    @Override
    public void register() {
        new CommandTree("marketplace")
                .executesPlayer((sender, args) -> {
//                    Common.sendMessage(sender, new NotImplementedException("Not Implemented!").getMessage());
                    new MarketPageGui(MarketplacePlugin.getInstance(), 0).open(sender);
                })
                .then(new LiteralArgument("sell").withPermission(MarketplacePlugin.NAMESPACE + ".sell")
                        .then(new DoubleArgument("price")
                                .executesPlayer((sender, args) -> {
                                    Double price = ((Double) args.get("price"));
                                    if (price == null) {
                                        Common.sendMessage(sender, Messages.INVALID_PRICE);
                                        return;
                                    }
                                    ItemStack mainHand = sender.getInventory().getItemInMainHand();
                                    sell(sender, mainHand, price);
                                }))).register();
    }

    private void sell(Player player, ItemStack item, double price) {
        var result = market.listItem(player, new ItemStack(item), price);

        var meta = item.getItemMeta();

        String itemName = meta != null ? meta.getDisplayName() : item.getType().name();

        if (ListItemResult.isSuccess(result))
            item.setAmount(0);

        Common.sendMessage(player, result.getMessage()
                .replace("{price}", "" + price)
                .replace("{item}", itemName));


    }
}
