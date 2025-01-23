package com.loficostudios.marketplacePlugin;

import com.loficostudios.marketplacePlugin.file.impl.YamlFile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

@Deprecated(forRemoval = true)
public class WorthConfig implements IConfig {

    private static double DEFAULT_SELL_VALUE = 1.0;

    private YamlFile file;
    private FileConfiguration config;

    private final MarketplacePlugin plugin;

    Map<String, Double> priceMap = new LinkedHashMap<>();



    public WorthConfig(MarketplacePlugin plugin) {
        this.plugin = plugin;
        try {
            reload();
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Could not load configs " + e.getMessage());
        }
    }

    @Override
    public void reload() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            this.file = new YamlFile("worth.yml", plugin);
            this.config = file.getConfig();
            if (!priceMap.isEmpty())
                priceMap.clear();

            for (String materialName : Arrays.stream(Material.values()).map(Enum::name).toList()) {
                if (config.contains(materialName)) {
                    Double worth = config.getDouble(materialName);
                    priceMap.put(materialName, worth);
                } else {
                    config.set(materialName, DEFAULT_SELL_VALUE);
                }
            }
            file.save();
        });
    }

    public double getSellPrice(Material material) {

        return priceMap.getOrDefault(material.name(), 0.0);
    }
}
