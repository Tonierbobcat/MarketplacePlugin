package com.loficostudios.marketplacePlugin.config;

import com.loficostudios.marketplacePlugin.MarketplacePlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MarketConfig {
    public static int BLACK_MARKET_INTERVAL_HOURS;

    public static String MARKET_TITLE;
    public static String MARKET_NEXT_BUTTON_LABEL;
    public static String MARKET_NEXT_BUTTON_MATERIAL;
    public static String MARKET_BACK_BUTTON_LABEL;
    public static String MARKET_BACK_BUTTON_MATERIAL;
    public static String MARKET_LISTING_PRICE;
    public static String MARKET_LISTING_PLAYER;

    public static String MARKET_CONFIRMATION_TITLE;
    public static String MARKET_CONFIRMATION_CONFIRM_BUTTON_LABEL;
    public static String MARKET_CONFIRMATION_CONFIRM_BUTTON_MATERIAL;
    public static String MARKET_CONFIRMATION_CANCEL_BUTTON_LABEL;
    public static String MARKET_CONFIRMATION_CANCEL_BUTTON_MATERIAL;

    public static String TRANSACTION_LOG_ENTRY_BOUGHT;
    public static String TRANSACTION_LOG_ENTRY_SOLD;


    public static void saveConfig() {
        FileConfiguration config = MarketplacePlugin.getInstance().getConfig();

        TRANSACTION_LOG_ENTRY_BOUGHT = getStringElseEmpty("transaction-log-entry-bought").replace("{symbol}", MarketplacePlugin.getEconomySymbol());
        TRANSACTION_LOG_ENTRY_SOLD = getStringElseEmpty("transaction-log-entry-sold").replace("{symbol}", MarketplacePlugin.getEconomySymbol());

        BLACK_MARKET_INTERVAL_HOURS = config.getInt("black-market-interval");

        MARKET_TITLE = getFormattedString("gui.main.title");
        MARKET_NEXT_BUTTON_LABEL = getFormattedString("gui.main.next-button.label");
        MARKET_NEXT_BUTTON_MATERIAL = getStringElseEmpty("gui.main.next-button.material");
        MARKET_BACK_BUTTON_LABEL = getFormattedString("gui.main.back-button.label");
        MARKET_BACK_BUTTON_MATERIAL = getStringElseEmpty("gui.main.back-button.material");
        MARKET_LISTING_PRICE = getFormattedString("gui.main.listing.price");
        MARKET_LISTING_PLAYER = getFormattedString("gui.main.listing.player");

        MARKET_CONFIRMATION_TITLE = getFormattedString("gui.confirmation.title");
        MARKET_CONFIRMATION_CONFIRM_BUTTON_LABEL = getFormattedString("gui.confirmation.confirm-button.label");
        MARKET_CONFIRMATION_CONFIRM_BUTTON_MATERIAL = getStringElseEmpty("gui.confirmation.confirm-button.material");
        MARKET_CONFIRMATION_CANCEL_BUTTON_LABEL = getFormattedString("gui.confirmation.cancel-button.label");
        MARKET_CONFIRMATION_CANCEL_BUTTON_MATERIAL = getStringElseEmpty("gui.confirmation.cancel-button.material");


    }

    private static @NotNull String getFormattedString(String path) {
        Component component = MiniMessage.miniMessage().deserialize("<reset/>" + getStringElseEmpty(path));
        return LegacyComponentSerializer.legacySection().serialize(component).replace("{symbol}", MarketplacePlugin.getEconomySymbol());
    }

    private static @NotNull String getStringElseEmpty(String path) {
        FileConfiguration config = MarketplacePlugin.getInstance().getConfig();
        return Objects.requireNonNullElse(config.getString(path), "");
    }

}
