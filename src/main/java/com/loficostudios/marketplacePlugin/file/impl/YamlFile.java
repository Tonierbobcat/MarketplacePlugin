/**
 * @Author Tonierbobcat
 * @Github https://github.com/Tonierbobcat
 * @version MelodyApi
 */

package com.loficostudios.marketplacePlugin.file.impl;

import com.loficostudios.marketplacePlugin.file.IFlatFile;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class YamlFile implements IFlatFile {

    @Getter
    @NotNull
    private FileConfiguration config;

    @Getter
    private File file;

    private final String fileName;

    public YamlFile(final String fileName, final JavaPlugin plugin) {
        this.fileName = fileName;
        create(plugin);
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public boolean getBoolean(@NotNull String path) {
        return config.getBoolean(path);
    }

    @Override
    public int getInt(@NotNull String path) {
        return config.getInt(path);
    }

    @Override
    public double getDouble(@NotNull String path) {
        return config.getDouble(path);
    }

    @Override
    public long getLong(@NotNull String path) {
        return config.getLong(path);
    }

    @Override
    public void set(@NotNull String path, @NotNull Object newValue) {
        config.set(path, newValue);
    }

    @SneakyThrows
    @Override
    public void create(final JavaPlugin plugin) {
        this.file = new File(plugin.getDataFolder(), fileName);
        if(!file.exists()) {
            file.getParentFile().mkdirs();
            if(plugin.getResource(fileName) == null) {
                file.createNewFile();
            }else {
                plugin.saveResource(fileName, false);
            }
        }

        this.config = YamlConfiguration.loadConfiguration(file);

    }

    @SneakyThrows
    @Override
    public void save() {
        this.config.save(file);
        Bukkit.getLogger().info("File saved successfully.");
    }

    @Override
    public Object getBase() {
        return config;
    }
}
