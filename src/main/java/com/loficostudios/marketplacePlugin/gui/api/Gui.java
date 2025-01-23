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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Gui implements InventoryHolder {

    protected static final String DEFAULT_MENU_TITLE = "&#C608FBM&#C322FBe&#C03CFBl&#BE56FCo&#BB70FCd&#B88BFCy &#B3BFFDG&#B0D9FDU&#ADF3FDI";


    private final MarketplacePlugin plugin = MarketplacePlugin.getInstance();
    private final GuiManager guiManager = plugin.getGuiManager();

    private Inventory gui;

    @NotNull
    @Override
    public Inventory getInventory() {
        return this.gui;
    }

    //@Getter
    //private int size;

    protected abstract @NotNull Integer getSize();

    protected abstract @Nullable String getTitle();

    @Getter
    private Map<Integer, GuiIcon> icons = new HashMap<>();

    private void create() {
        String title = getTitle();
        int size = validateSize(getSize());

        /*try {
            size = validateSize(getSize());
        } catch (Exception e) {
            String pluginName = "[" + plugin.getName() + "]";
            String msg = pluginName + " Error Validating Size: " + Arrays.toString(e.getStackTrace());
            Bukkit.getLogger().log(Level.SEVERE, msg);

            size = 9;

        }*/

        this.gui = plugin.getServer().createInventory(this,
                size,
                ColorUtils.deserialize(title != null && !title.isEmpty() ? title : DEFAULT_MENU_TITLE));



        this.icons.forEach((slot, icon) -> {

            //Bukkit.getLogger().log(Level.SEVERE, "setting slot " + slot);

            this.gui.setItem(slot, icon.getItem());
        });
    }

    public final void fill(@NotNull GuiIcon icon, int start, int end, Boolean replaceExisting) {
        for(int i = start; i < end; ++i) {

            if (!replaceExisting && this.icons.containsKey(i)) {
                continue; // Skip this iteration if replaceExisting is false and key exists
            }

            this.icons.put(i, icon);
        }
    }

    public void open(@NotNull Player player) {
        create();

        plugin.getGuiManager().setGui(player, this);
        player.openInventory(this.gui);
    }

    protected void clear() {
        if (icons.isEmpty())
            return;
        icons.forEach((index, icon) -> {
            this.getInventory().setItem(index, new ItemStack(Material.AIR));
        });
    }

    public void close(@NotNull Player player) {
        player.closeInventory();
    }

    public void refreshGui(@NotNull Player player, @NotNull Gui gui) {
        gui.open(player);
    }

    public void setSlot(@NotNull Integer slot, @NotNull GuiIcon icon) {

        /*if ()

        try {
            this.gui.setItem(slot, icon.getItem());
        } catch (Exception e) {
            String pluginName = "[" + plugin.getName() + "]";
            String msg = pluginName + " Error adding icon to gui GUICON_ID: " + icon.getId() + " " + e.getMessage();
            Bukkit.getLogger().log(Level.SEVERE, msg);
            return;
        }*/



        this.icons.put(slot, icon);
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



