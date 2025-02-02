package com.loficostudios.marketplacePlugin.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class MiniMessageUtils {

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
        var result = new StringBuilder();

        var hexPattern = Pattern.compile("§x(§[0-9a-fA-F]){6}");
        var hexMatcher = hexPattern.matcher(message);

        // First check for §x§R§R§G§G§B§B and replace it with <color:#RRGGBB>
        while (hexMatcher.find()) {
            StringBuilder hexColor = new StringBuilder("#");
            String match = hexMatcher.group(); // Get the full matched string
            for (int i = 2; i < match.length(); i += 2) { // Skip §x and extract hex digits
                hexColor.append(match.charAt(i + 1));
            }
            hexMatcher.appendReplacement(result, "<color:" + hexColor + ">");
        }
        hexMatcher.appendTail(result);

        // Check for rest
        for (Map.Entry<String, String> entry : formatMap.entrySet()) {
            result = new StringBuilder(result.toString().replace(entry.getKey(), entry.getValue()));
        }

        return result.toString();
    }
}
