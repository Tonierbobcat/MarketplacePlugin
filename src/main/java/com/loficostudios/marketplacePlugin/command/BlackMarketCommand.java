package com.loficostudios.marketplacePlugin.command;

import com.loficostudios.marketplacePlugin.MarketplacePlugin;
import com.loficostudios.marketplacePlugin.command.impl.Command;
import com.loficostudios.marketplacePlugin.gui.MarketPageGui;
import com.loficostudios.marketplacePlugin.market.BlackMarket;
import com.loficostudios.marketplacePlugin.utils.Common;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.LiteralArgument;

public class BlackMarketCommand implements Command {

    @Override
    public void register() {
        MarketplacePlugin plugin = MarketplacePlugin.getInstance();
        new CommandTree("blackmarket")
                .executesPlayer((sender, args) -> {
                    var blackMarket = plugin.getActiveBlackMarket();
                    if (blackMarket == null) {
                        Common.sendMessage(sender, "No active blackmarket!");
                        return;
                    }
                    new MarketPageGui(blackMarket, 0).open(sender);
                })
                .then(new LiteralArgument("generate").withPermission(CommandPermission.OP)
                        .executesPlayer((sender, args) -> {
                            generate();
                        })).register();
    }

    private void generate() {
        MarketplacePlugin plugin = MarketplacePlugin.getInstance();

        plugin.setBlackMarket((BlackMarket) new BlackMarket(plugin.getActiveMarket())
                .onUpdate(market -> MarketPageGui.getInstances().forEach(MarketPageGui::refresh)));
        MarketPageGui.getInstances().forEach(MarketPageGui::refresh);
    }
}
