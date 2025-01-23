package com.loficostudios.marketplacePlugin;

import com.loficostudios.marketplacePlugin.file.impl.YamlFile;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Field;

public class Messages {
    @MessageField
    public static final String MARKET_NO_PERMISSION_TO_LIST_ITEM = "<gradient:#34F6F2:#7D53DE>You do not have permission to list on the market";
    @MessageField
    public static final String MARKET_NO_PERMISSION_TO_BUY = "You do not have permission to buy";

    @MessageField
    public static final String INVALID_PRICE = "<gradient:#34F6F2:#7D53DE>Invalid price!";

    @MessageField
    public static final String MARKET_LIST_ITEM_FAILURE = "<gradient:#34F6F2:#7D53DE>Could not list item on the market";

    @MessageField
    public static final String MARKET_BUY_ITEM_FAILURE = "Could not buy item";

    @MessageField
    public static final String MARKET_BUY = "Bought item <gray>{item} <yellow>{price}";

    @MessageField
    public static final String MARKET_LISTED_ITEM = "<gradient:#34F6F2:#7D53DE>Successfully listed item on the market. <gray>{item} <yellow>{price}";

    @MessageField
    public static final String MARKET_FIRST_TIME_LISTED_ITEM = "<gradient:#34F6F2:#7D53DE>Successfully listed item on the market for the first time. <gray>{item} <yellow>{price}";
    @MessageField
    public static final String MARKET_NOT_ENOUGH_TO_BUY = "Not enough money to buy this item you need {price} to buy {item}";
    @MessageField
    public static final String INVALID_ITEM = "Invalid Item!";
    public static final String INVALID_LISTING = "Invalid Listing!";
    public static final String SELLER_TRANSACTION_NOT_SUCCESSFUL = "Error occured when depositing funds to seller";
    public static final String BUYER_TRANSACTION_NOT_SUCCESSFUL = "Error occured when depositing funds to buyer" ;

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
