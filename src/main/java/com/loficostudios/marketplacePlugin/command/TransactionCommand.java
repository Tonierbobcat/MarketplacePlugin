package com.loficostudios.marketplacePlugin.command;

import com.loficostudios.marketplacePlugin.config.MarketConfig;
import com.loficostudios.marketplacePlugin.MarketplacePlugin;
import com.loficostudios.marketplacePlugin.config.Messages;
import com.loficostudios.marketplacePlugin.command.impl.Command;
import com.loficostudios.marketplacePlugin.market.transactionlog.TransactionEntry;
import com.loficostudios.marketplacePlugin.utils.Common;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TransactionCommand implements Command {
    private static final int AMOUNT_OF_ENTRIES_TO_DISPLAY = 7;

    private final MarketplacePlugin plugin;
    private final Economy economy;
    public TransactionCommand(MarketplacePlugin plugin) {
        this.plugin = plugin;
        this.economy = plugin.getEconomy();
    }

    @Override
    public void register() {
        new CommandAPICommand("transactions")
                .withPermission(MarketplacePlugin.NAMESPACE + ".history")
                .withOptionalArguments(new IntegerArgument("page"))
                .executesPlayer((sender, args )-> {
                    Integer page = (Integer) args.get("page");
                    if (page != null && page <= 0) {
                        Common.sendMessage(sender, Messages.INVALID_PAGE);
                        return;
                    }
                    listTransactions(sender, page != null
                            ? page - 1
                            : 0);
                }).register();
    }

    private void listTransactions(Player player, int page) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            int perPage = AMOUNT_OF_ENTRIES_TO_DISPLAY;

            var logs = plugin.getActiveMarket().getTransactionLog().getLogs(player);

            int start = page * perPage;
            int end = Math.min(start + perPage, logs.size());

            if (start >= logs.size()) {
                start = logs.size();
            }

            var entries = new TransactionEntry[perPage];

            var availableEntries = logs.stream().toList().subList(start, end).toArray(TransactionEntry[]::new);
            for (int i = 0; i < availableEntries.length; i++) {
                entries[i] = availableEntries[i];
            }

            String[] messages = new String[] {
                    "Transactions: [Page {page}]".replace("{page}", "" + (page + 1)),
                    "*--------------------------------------------------*",
                    getMessage(player, entries[0]),
                    getMessage(player, entries[1]),
                    getMessage(player, entries[2]),
                    getMessage(player, entries[3]),
                    getMessage(player, entries[4]),
                    getMessage(player, entries[5]),
                    getMessage(player, entries[6]),
                    "*---------------------------------------------------*"
            };

            Bukkit.getScheduler().runTask(plugin, () -> {
                for (String message : messages) {
                    Common.sendMessage(player, message);
                }
            });
        });
    }

    private String getMessage(Player player, TransactionEntry entry) {
        if (entry == null) {
            return "-";
        }
        if (player.getUniqueId().equals(entry.seller())) {

            return MarketConfig.TRANSACTION_LOG_ENTRY_SOLD
                    .replace("{amount}", "" + entry.item().getAmount())
                    .replace("{item}", Common.getItemName(entry.item()))
                    .replace("{price}", "" + entry.sellPrice())
                    .replace("{player}", entry.getBuyerName());
        }

        return MarketConfig.TRANSACTION_LOG_ENTRY_BOUGHT
                .replace("{amount}", ""+entry.item().getAmount())
                .replace("{item}", Common.getItemName(entry.item()))
                .replace("{price}", "" + entry.buyPrice())
                .replace("{player}", entry.getSellerName());
    }
}
