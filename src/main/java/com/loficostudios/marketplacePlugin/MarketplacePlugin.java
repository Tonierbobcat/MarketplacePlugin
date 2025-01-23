package com.loficostudios.marketplacePlugin;

import com.loficostudios.marketplacePlugin.command.BlackMarketCommand;
import com.loficostudios.marketplacePlugin.command.MarketCommand;
import com.loficostudios.marketplacePlugin.command.impl.Command;
import com.loficostudios.marketplacePlugin.gui.MarketPageGui;
import com.loficostudios.marketplacePlugin.gui.api.GuiManager;
import com.loficostudios.marketplacePlugin.gui.api.listeners.GuiListener;
import com.loficostudios.marketplacePlugin.market.BlackMarket;
import com.loficostudios.marketplacePlugin.market.IMarket;
import com.loficostudios.marketplacePlugin.market.Market;
import com.loficostudios.marketplacePlugin.utils.MongoDBUtils;
import dev.jorel.commandapi.*;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
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

            var rsp = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (rsp == null) {
                throw new VaultNotFoundException(VAULT_NOT_INSTALLED);
            }
            this.economy = rsp.getProvider();
        } catch (VaultNotFoundException e) {
            getLogger().log(Level.SEVERE, e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    public void setBlackMarket(BlackMarket market) {
        this.activeBlackMarket = market;
    }
}
