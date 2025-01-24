package com.loficostudios.marketplacePlugin.command;

import com.loficostudios.marketplacePlugin.MarketplacePlugin;
import com.loficostudios.marketplacePlugin.Messages;
import com.loficostudios.marketplacePlugin.command.impl.Command;
import com.loficostudios.marketplacePlugin.gui.MarketPageGui;
import com.loficostudios.marketplacePlugin.market.ListItemResult;
import com.loficostudios.marketplacePlugin.market.Market;
import com.loficostudios.marketplacePlugin.utils.Common;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MarketCommand implements Command {

    private final MarketplacePlugin plugin;
    private final Economy economy;
    public MarketCommand(MarketplacePlugin plugin) {
        this.plugin = plugin;
        this.economy = plugin.getEconomy();
    }

    @Override
    public void register() {
        new CommandTree("marketplace")
                .withPermission(MarketplacePlugin.NAMESPACE + ".view")
                .executesPlayer((sender, args) -> {
//                    Common.sendMessage(sender, new NotImplementedException("Not Implemented!").getMessage());

                    new MarketPageGui(plugin.getActiveMarket(), 0).open(sender);


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
        var result = plugin.getActiveMarket().listItem(player, new ItemStack(item), price);

        if (ListItemResult.isSuccess(result))
            item.setAmount(0);

        Common.sendMessage(player, result.getMessage()
                .replace("{price}", "" + price)
                .replace("{item}", Common.getItemName(item)));
    }
}
