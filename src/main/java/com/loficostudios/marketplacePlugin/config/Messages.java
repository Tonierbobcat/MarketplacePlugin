package com.loficostudios.marketplacePlugin.config;

import com.loficostudios.marketplacePlugin.MarketplacePlugin;
import com.loficostudios.marketplacePlugin.config.annotations.MessageField;
import com.loficostudios.melodyapi.file.impl.YamlFile;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Field;

public class Messages {
    @MessageField
    public static String MARKET_NO_PERMISSION_TO_LIST_ITEM = "<color:#FF7E6B>You don't have permission to list items on the market.</color>";
    @MessageField
    public static String MARKET_NO_PERMISSION_TO_BUY = "<color:#FF7E6B>You don't have permission to buy items from the market.</color>";

    @MessageField
    public static String INVALID_PRICE = "<color:#FF7E6B>Invalid price entered. Please try again.</color>";

    @MessageField
    public static String MARKET_LIST_ITEM_FAILURE = "<color:#FF7E6B>Unable to list the item on the market. Please check your input.</color>";

    @MessageField
    public static String MARKET_BUY_ITEM_FAILURE = "<color:#FF7E6B>Unable to purchase the item. Please try again later.</color>";

    @MessageField
    public static String MARKET_BUY = "<color:#A9F0D1>Successfully purchased <white>{item}</white><reset><color:#A9F0D1> for <white>{symbol}{price}</white><reset><color:#A9F0D1>.</color>";

    @MessageField
    public static String MARKET_LISTED_ITEM = "<color:#A9F0D1>Item listed successfully! <white>{item}</white><reset><color:#A9F0D1> for <white>{symbol}{price}</white><reset><color:#A9F0D1>.</color>";

    @MessageField
    public static String MARKET_FIRST_TIME_LISTED_ITEM = "<color:#A9F0D1>Item listed on the market for the first time! <white>{item}</white><reset><color:#A9F0D1> for <white>{symbol}{price}</white><reset><color:#A9F0D1>.</color>";

    @MessageField
    public static String MARKET_NOT_ENOUGH_TO_BUY = "<color:#FF7E6B>Insufficient funds to buy <white>{item}</white><reset><color:#FF7E6B>. You need <white>{symbol}{price}</white><reset><color:#FF7E6B>.</color>";

    @MessageField
    public static String INVALID_ITEM = "<color:#FF7E6B>The specified item is invalid.</color>";

    @MessageField
    public static String INVALID_LISTING = "<color:#FF7E6B>The listing you provided is invalid.</color>";

    @MessageField
    public static String INVALID_PAGE = "<color:#FF7E6B>The page number you entered is invalid.</color>";

    @MessageField
    public static String SELLER_TRANSACTION_NOT_SUCCESSFUL = "<color:#FF7E6B>An error occurred while processing <white>{seller}</white><reset><color:#FF7E6B>'s funds.</color>";

    @MessageField
    public static String BUYER_TRANSACTION_NOT_SUCCESSFUL = "<color:#FF7E6B>An error occurred while processing <white>{buyer}</white><reset><color:#FF7E6B>'s funds.</color>";

    @MessageField
    public static String BLACKMARKET_REGENERATED_CLOSING_MENU = "<color:#8C5E58>Closing the previous menu...</color>";

    @MessageField
    public static String BLACKMARKET_GENERATE = "<color:#8C5E58>A new black market has been generated.</color>";

    @MessageField
    public static String BLACKMARKET_GENERATE_BROADCAST_PLAYERS = "<color:#8C5E58>A new black market is available! Use <white><underlined>/blackmarket</underlined></white><reset><color:#8C5E58> to view the items.</color>";

    @MessageField
    public static String BLACKMARKET_NOT_ACTIVE = "<color:#FF7E6B>There is no active black market at the moment.</color>";

    public static void saveConfig() {
        YamlFile file = new YamlFile("messages.yml", MarketplacePlugin.getInstance());
        FileConfiguration config = file.getConfig();
        for (Field field : Messages.class.getDeclaredFields()) {
            if (field.isAnnotationPresent(MessageField.class)) {
                try {
                    String fieldName = field.getName();
                    String configPath = "messages." + fieldName;

                    if (config.contains(configPath)) {
                        String configValue = config.getString(configPath);

                        field.setAccessible(true);
                        field.set(null, configValue);
                    } else {
                        config.set(configPath, (String) field.get(null));
                    }
                } catch (IllegalAccessException ignore) {
                }
            }
        }
        file.save();
    }
}
