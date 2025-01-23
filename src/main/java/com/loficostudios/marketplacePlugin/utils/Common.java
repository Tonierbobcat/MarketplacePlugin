package com.loficostudios.marketplacePlugin.utils;

import com.loficostudios.marketplacePlugin.MarketplacePlugin;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

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
}
