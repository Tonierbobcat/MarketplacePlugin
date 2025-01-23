package com.loficostudios.marketplacePlugin.command;

import com.loficostudios.marketplacePlugin.MarketplacePlugin;
import com.loficostudios.marketplacePlugin.Messages;
import com.loficostudios.marketplacePlugin.command.impl.Command;
import com.loficostudios.marketplacePlugin.market.Market;
import com.loficostudios.marketplacePlugin.utils.Common;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.DoubleArgument;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SellCommand implements Command {

    private final Market market;

    public SellCommand(Market market) {
        this.market = market;
    }

    @Override
    public void register() {
        new CommandAPICommand("sell")
                .withPermission(MarketplacePlugin.NAMESPACE + ".sell")
                .withArguments(new DoubleArgument("price"))
                .executesPlayer((sender, args) -> {
                    Double price = ((Double) args.get("price"));
                    if (price == null) {
                        Common.sendMessage(sender, Messages.INVALID_PRICE);
                        return;
                    }
                    ItemStack mainHand = sender.getInventory().getItemInMainHand();
                    sell(sender, mainHand, price);
                }).register();
    }

    private void sell(Player player, ItemStack item, double price) {
        var result = market.listItem(player, item, price);

        var meta = item.getItemMeta();

        String itemName = meta != null ? meta.getDisplayName() : item.getType().name();

        Common.sendMessage(player, result.getMessage()
                .replace("{price}", "" + price)
                .replace("{item}", itemName));
    }
}
