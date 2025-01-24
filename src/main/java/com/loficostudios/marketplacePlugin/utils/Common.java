package com.loficostudios.marketplacePlugin.utils;

import com.loficostudios.marketplacePlugin.MarketplacePlugin;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Common {
    public static void sendMessage(Player player, String message) {
        var mm = MiniMessage.miniMessage();
        Component component =  mm.deserialize("<reset/>" + message);
        ((Audience) player).sendMessage(component);
    }

    public static boolean isNullOrEmpty(@Nullable String string) {
        return string == null || string.isEmpty();
    }

    public static Material materialFromName(String name) {
        Logger lgr = MarketplacePlugin.getInstance().getLogger();
        try {
            return Material.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            lgr.log(Level.SEVERE, "Invalid material name!");
            return Material.BARRIER;
        }
    }

    public static void broadcast(String message) {
        var mm = MiniMessage.miniMessage();
        Component component =  mm.deserialize("<reset/>" + message);
        for (Player player : Bukkit.getOnlinePlayers()) {
            ((Audience) player).sendMessage(component);
        }
    }
    public static void broadcast(String message, Player... excluded) {
        var mm = MiniMessage.miniMessage();
        Component component =  mm.deserialize("<reset/>" + message);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (Arrays.stream(excluded).anyMatch(p -> player.getUniqueId().equals(p.getUniqueId())))
                continue;
            ((Audience) player).sendMessage(component);
        }
    }

    public static String getItemName(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta != null && !meta.getDisplayName().isEmpty()) {
            return meta.getDisplayName();
        }
        else {
            return formatMaterial(item.getType());
        }
    }
    public static String formatMaterial(@NotNull Material material) {
        String rawName = material.toString();

        String[] words = rawName.split("_");

        for (int i = 0; i < words.length; i++) {
            String raw = words[i];

            if (!raw.isEmpty()) {
                char firstLetter = raw.charAt(0);
                words[i] =  Character.toUpperCase(firstLetter) + raw.substring(1).toLowerCase();
            }
        }

        return String.join(" ", words);
    }
}
