package com.loficostudios.marketplacePlugin;

import dev.jorel.commandapi.*;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.Generated;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;

public final class MarketplacePlugin extends JavaPlugin {
    private static final String VAULT_NOT_INSTALLED = "Vault is not installed!";
    private static final String NAMESPACE = "market";
    @Getter
    private static MarketplacePlugin instance;

    @Getter
    private Economy economy;

    @Override
    public void onLoad() {
        var conf = new CommandAPIBukkitConfig(this);
        conf.setNamespace(NAMESPACE);
        CommandAPI.onLoad(conf);
    }

    @Override
    public void onEnable() {
        CommandAPI.onEnable();
        setupEconomy();


        registerCommands();
        registerListeners();
    }

    @Override
    public void onDisable() {

    }

    private void registerCommands() {
    }

    public void reload() {
    }

    private void registerListeners() {

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
}
