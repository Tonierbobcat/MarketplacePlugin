package com.loficostudios.marketplacePlugin;

import com.loficostudios.marketplacePlugin.command.BlackMarketCommand;
import com.loficostudios.marketplacePlugin.command.MarketCommand;
import com.loficostudios.marketplacePlugin.command.impl.Command;
import com.loficostudios.marketplacePlugin.gui.MarketPageGui;
import com.loficostudios.marketplacePlugin.gui.api.GuiManager;
import com.loficostudios.marketplacePlugin.gui.api.listeners.GuiListener;
import com.loficostudios.marketplacePlugin.market.BlackMarket;
import com.loficostudios.marketplacePlugin.market.Market;
import com.loficostudios.marketplacePlugin.utils.Common;
import com.loficostudios.marketplacePlugin.utils.MongoDBUtils;
import dev.jorel.commandapi.*;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public final class MarketplacePlugin extends JavaPlugin {
    private static final String VAULT_NOT_INSTALLED = "Vault is not installed!";
    public static final String NAMESPACE = "marketplace";

    @Getter
    private static MarketplacePlugin instance;

    @Getter
    private GuiManager guiManager;
    @Getter
    private Market activeMarket;
    @Getter
    private BlackMarket activeBlackMarket;
    @Getter
    private Economy economy;

    private BukkitTask blackMarketTimer;
    private boolean blackMarketTaskCancelled;

    public MarketplacePlugin() {
        instance = this;
    }

    @Override
    public void onLoad() {
        var conf = new CommandAPIBukkitConfig(this);
        conf.setNamespace(NAMESPACE);
        CommandAPI.onLoad(conf);
    }

    @Override
    public void onEnable() {
        CommandAPI.onEnable();

        saveDefaultConfig();
        MarketConfig.saveConfig();
        Messages.saveConfig();
        setupEconomy();

        guiManager = new GuiManager();

        MongoDBUtils.initialize(this.getConfig());
        if (!MongoDBUtils.isInited()) {
            getLogger().log(Level.SEVERE, "Failed initialize database");
            getServer().getPluginManager().disablePlugin(this);
        }
        //instantiate new market after dbutils.init
        activeMarket = (Market) new Market(this)
                .onUpdate(market -> {
                    MarketPageGui.getInstances().forEach(MarketPageGui::refresh);
                });

        activeMarket.loadAsync().whenComplete((loaded, ex) -> {
            if (loaded) {
                generateBlackMarket(false);
                startBlackMarketTimer();
            }
        });

        registerCommands();
        registerListeners();
    }

    @Override
    public void onDisable() {
    }

    private void registerCommands() {
        Arrays.asList(
                new MarketCommand(activeMarket),
                new BlackMarketCommand()
        ).forEach(Command::register);
    }

    public void reload() {
    }

    private void registerListeners() {
        Arrays.asList(
                new GuiListener(guiManager)
        ).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
    }

    private void setupEconomy() {
        try {
            try {
                Class.forName("net.milkbowl.vault.economy.Economy");
            } catch (ClassNotFoundException e) {
                throw new VaultNotFoundException(VAULT_NOT_INSTALLED);
            }

            var rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                throw new VaultNotFoundException(VAULT_NOT_INSTALLED);
            }
            this.economy = rsp.getProvider();
        } catch (VaultNotFoundException e) {
            getLogger().log(Level.SEVERE, e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    //region BLACKMARKET
    public boolean generateBlackMarket(boolean cancelTimer) {
        blackMarketTaskCancelled = !cancelTimer;
        try {
            this.activeBlackMarket =  ((BlackMarket) new BlackMarket(this.activeMarket)
                    .onUpdate(market -> MarketPageGui.getInstances().forEach(MarketPageGui::refresh)));
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Error generating blackmarket. " + e.getMessage());
            return false;
        }

        var marketGuis = new ArrayList<>(MarketPageGui.getInstances());

        marketGuis.forEach(gui -> {
            if (gui.getMarket() instanceof BlackMarket) {
                Inventory inventory =  gui.getInventory();

                List<Player> players = inventory.getViewers()
                        .stream()
                        .map(human -> (Player) human)
                        .toList();

                players.forEach(player -> {
                    if (player == null)
                        return;
                    try {
                        gui.close(player);
                        Common.sendMessage(player, Messages.BLACKMARKET_REGENERATED_CLOSING_MENU);
                    } catch (Exception e) {
                        getLogger().log(Level.SEVERE, "Error GUI. " + e.getMessage());
                    }
                });

                return;
            }

            gui.refresh();
        });
        return true;
    }

    private void startBlackMarketTimer() {
        if (blackMarketTimer != null && !blackMarketTimer.isCancelled()) {
            blackMarketTimer.cancel();
            blackMarketTimer = null;
        }

        blackMarketTimer = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
            final int maxTime = (MarketConfig.BLACK_MARKET_INTERVAL_HOURS * 60) * 20;
            int timer;

            @Override
            public void run() {
//                instance.getLogger().log(Level.INFO,"Current timer: " + (timer / 20.0) + "s , MaxTime: " + (maxTime / 20.0) + "s");
                if (blackMarketTaskCancelled) {
                    blackMarketTaskCancelled = false;
                    timer = 0;
                    return;
                }

                timer++;
                if (timer >= maxTime) {
                    timer = 0;
                    Bukkit.getScheduler().runTask(instance, () -> {
                        if (generateBlackMarket(false)) {
                            Common.broadcast(Messages.BLACKMARKET_GENERATE_BROADCAST_PLAYERS);
                        }
                    });
                }
            }
        }, 0, 1);
    }

    private boolean stopBlackMarketTimer() {
        if (blackMarketTimer == null) {
            return false;
        }
        blackMarketTimer.cancel();
        var cancelled = blackMarketTimer.isCancelled();
        blackMarketTimer = null;
        return cancelled;
    }
    //endregion
}
