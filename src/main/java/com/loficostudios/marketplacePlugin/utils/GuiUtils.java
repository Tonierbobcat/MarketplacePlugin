package com.loficostudios.marketplacePlugin.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class GuiUtils {
    public static ItemStack getButtonItem(String name, Material type) {
        var item = new ItemStack(type);
        var meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ColorUtils.deserialize(name));
        }
        item.setItemMeta(meta);
        return item;
    }
    public static List<Integer> getPerimeter(int rows, int columns) {
        Set<Integer> perimeterIndexes = new LinkedHashSet<>();

        for (int i = 0; i < columns; i++) {
            perimeterIndexes.add(i);
        }

        for (int i = (rows * columns) - columns; i < rows * columns; i++) {
            perimeterIndexes.add(i);
        }

        for (int i = 0; i < rows; i++) {
            perimeterIndexes.add(i * columns);
        }

        for (int i = 0; i < rows; i++) {
            perimeterIndexes.add((i * columns) + (columns - 1));
        }

        return new ArrayList<>(perimeterIndexes);
    }
}
