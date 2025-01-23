/**
 * @Author Tonierbobcat
 * @Github https://github.com/Tonierbobcat
 * @version MelodyApi
 */

package com.loficostudios.marketplacePlugin.gui.api.listeners;

import com.loficostudios.marketplacePlugin.gui.MarketPageGui;
import com.loficostudios.marketplacePlugin.gui.api.Gui;
import com.loficostudios.marketplacePlugin.gui.api.GuiIcon;
import com.loficostudios.marketplacePlugin.gui.api.GuiManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;


public class GuiListener implements Listener {


    protected final GuiManager guiManager;

    public GuiListener(GuiManager guiManager) {
        this.guiManager = guiManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
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

    @EventHandler
    private void onClose(InventoryCloseEvent e) {
        if (!(e.getInventory().getHolder() instanceof Gui))
            return;
        Gui gui = guiManager.getGui((Player) e.getPlayer());
        if (!(gui instanceof MarketPageGui))
            return;
        MarketPageGui.getInstances().remove(gui);
    }
}
