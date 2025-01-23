package com.loficostudios.marketplacePlugin.command;

import com.loficostudios.marketplacePlugin.MarketplacePlugin;
import com.loficostudios.marketplacePlugin.Messages;
import com.loficostudios.marketplacePlugin.command.impl.Command;
import com.loficostudios.marketplacePlugin.gui.MarketPageGui;
import com.loficostudios.marketplacePlugin.utils.Common;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;

public class BlackMarketCommand implements Command {

    @Override
    public void register() {
        MarketplacePlugin plugin = MarketplacePlugin.getInstance();
        new CommandTree("blackmarket")
                .withPermission(MarketplacePlugin.NAMESPACE + ".blackmarket")
                .executesPlayer((sender, args) -> {
                    var blackMarket = plugin.getActiveBlackMarket();
                    if (blackMarket == null) {
                        Common.sendMessage(sender, Messages.BLACKMARKET_NOT_ACTIVE);
                        return;
                    }
                    new MarketPageGui(blackMarket, 0).open(sender);
                })
                .then(new LiteralArgument("generate").withPermission(CommandPermission.OP)
                        .then(new BooleanArgument("silent").setOptional(true)
                        .executesPlayer((sender, args) -> {
                            if (plugin.generateBlackMarket(true)) {
                                Common.sendMessage(sender, Messages.BLACKMARKET_GENERATE);
                                Common.broadcast(Messages.BLACKMARKET_GENERATE_BROADCAST_PLAYERS, sender);
                            }
                        }))).register();
    }


}
