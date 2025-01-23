package com.loficostudios.marketplacePlugin.utils;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import net.kyori.adventure.platform.AudienceProvider;


public class Common {
    public static void sendMessage(Player player, String message) {
        var mm = MiniMessage.miniMessage();
        Component component =  mm.deserialize(message);
        ((Audience) player).sendMessage(component);
    }
}
