/**
 * @Author Tonierbobcat
 * @Github https://github.com/Tonierbobcat
 * @version MelodyApi
 */



package com.loficostudios.marketplacePlugin.gui.api;

import com.loficostudios.marketplacePlugin.MarketplacePlugin;
import com.loficostudios.marketplacePlugin.utils.ColorUtils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Level;

import static com.loficostudios.marketplacePlugin.utils.Common.isNullOrEmpty;

public abstract class Gui implements InventoryHolder {

    protected static final String DEFAULT_MENU_TITLE = "&#C608FBM&#C322FBe&#C03CFBl&#BE56FCo&#BB70FCd&#B88BFCy &#B3BFFDG&#B0D9FDU&#ADF3FDI";


    private final MarketplacePlugin plugin = MarketplacePlugin.getInstance();

    private final GuiManager guiManager = plugin.getGuiManager();

    private final Inventory gui;

    @NotNull
    @Override
    public Inventory getInventory() {
        return this.gui;
    }

    @Getter
    private Map<Integer, GuiIcon> icons = new HashMap<>();

    public void refresh() {
    }

    protected void createIcons(Inventory inventory) {
        this.icons.forEach((slot, icon) -> {
            inventory.setItem(slot, icon.getItem());
            plugin.getLogger().log(Level.INFO, "created icon. material: " + icon.getItem().getType());
        });
    }

    @Getter
    private final int size;

    @Getter
    private String title;

    public Gui(int size, String title) {
        this.size = validateSize(size);
        this.title = title;
        this.gui = plugin.getServer().createInventory(this,
                this.size,
                ColorUtils.deserialize(!isNullOrEmpty(title) ? title : DEFAULT_MENU_TITLE));
    }

    public Gui(int size) {
        this.size = validateSize(size);
        this.title = DEFAULT_MENU_TITLE;
        this.gui = plugin.getServer().createInventory(this,
                this.size,
                ColorUtils.deserialize(this.title));
    }

    public final void fill(@NotNull GuiIcon icon, int start, int end, Boolean replaceExisting) {
        for(int i = start; i < end; ++i) {

            if (!replaceExisting && this.icons.containsKey(i)) {
                continue; // Skip this iteration if replaceExisting is false and key exists
            }

            setSlot(i, icon);
        }
    }

    public void open(@NotNull Player player) {
//        this.gui = create();
        plugin.getGuiManager().setGui(player, this);
        player.openInventory(this.gui);
    }

    protected void clear() {
        if (icons.isEmpty()) return;

        icons.forEach((index, icon) -> {
            this.getInventory().setItem(index, new ItemStack(Material.AIR));
        });

        icons.clear();
    }

    public void close(@NotNull Player player) {
        player.closeInventory();
    }

    public void setSlot(@NotNull Integer slot, @NotNull GuiIcon icon) {
        this.icons.put(slot, icon);
        this.gui.setItem(slot, icon.getItem());
    }

    public GuiIcon getIcon(@NotNull Integer slot) {
        return this.icons.get(slot);
    }

    private int validateSize(int size) {
        final Set<Integer> allowedInventorySize = new HashSet<>(Set.of(
            9, 18, 27, 36, 45, 54
        ));

        return allowedInventorySize.stream()
                .filter(allowedSize -> allowedSize >= size)
                .min(Integer::compareTo)
                .orElse(9);
    }


}



