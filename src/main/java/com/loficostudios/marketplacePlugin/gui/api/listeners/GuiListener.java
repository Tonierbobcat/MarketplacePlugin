/**
 * @Author Tonierbobcat
 * @Github https://github.com/Tonierbobcat
 * @version MelodyApi
 */

package com.loficostudios.marketplacePlugin.gui.api.listeners;

import com.loficostudios.marketplacePlugin.gui.api.Gui;
import com.loficostudios.marketplacePlugin.gui.api.GuiIcon;
import com.loficostudios.marketplacePlugin.gui.api.GuiManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;


public class GuiListener implements Listener {


    private final GuiManager guiManager;

    public GuiListener(GuiManager guiManager) {
        this.guiManager = guiManager;
    }

    @EventHandler
    protected void inventoryHandler(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!(event.getInventory().getHolder() instanceof Gui)) return;

        event.setCancelled(true);

        Gui gui = guiManager.getGui(player);

        GuiIcon icon = gui.getIcon(event.getRawSlot());

        if (icon != null) {
            if (icon.getAction() != null) {
                icon.getAction().accept(event);
            }
        }

    }
}
