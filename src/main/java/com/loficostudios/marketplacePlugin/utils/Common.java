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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Common {

    private static final Map<String, String> formatMap = new HashMap<>();
    static {
        formatMap.put("§0", "<black>");
        formatMap.put("§1", "<dark_blue>");
        formatMap.put("§2", "<dark_green>");
        formatMap.put("§3", "<dark_aqua>");
        formatMap.put("§4", "<dark_red>");
        formatMap.put("§5", "<dark_purple>");
        formatMap.put("§6", "<gold>");
        formatMap.put("§7", "<gray>");
        formatMap.put("§8", "<dark_gray>");
        formatMap.put("§9", "<blue>");
        formatMap.put("§a", "<green>");
        formatMap.put("§b", "<aqua>");
        formatMap.put("§c", "<red>");
        formatMap.put("§d", "<light_purple>");
        formatMap.put("§e", "<yellow>");
        formatMap.put("§f", "<white>");
        formatMap.put("§l", "<bold>");
        formatMap.put("§o", "<italic>");
        formatMap.put("§n", "<underline>");
        formatMap.put("§m", "<strikethrough>");
        formatMap.put("§k", "<obfuscated>");
        formatMap.put("§r", "<reset>");
    }

    public static String legacyToMiniMessages(String message) {
        for (Map.Entry<String, String> entry : formatMap.entrySet()) {
            message = message.replace(entry.getKey(), entry.getValue());
        }
        return message.replaceAll("§#([0-9a-fA-F]{6})", "<color:#$1>");
    }

    public static void sendMessage(Player player, String message) {
        var mm = MiniMessage.miniMessage();

        Component component = null;
        try {
            component = mm.deserialize("<reset/>" + legacyToMiniMessages(message.replace("{symbol}", MarketplacePlugin.getEconomySymbol())));
        } catch (Exception e) {
            MarketplacePlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to deserialize message " + e.getMessage());
            return;
        }

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
    public static void sendMessageLegacy(Player player, String message) {
        player.sendMessage(ColorUtils.deserialize(message));
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
//        mm.des
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
