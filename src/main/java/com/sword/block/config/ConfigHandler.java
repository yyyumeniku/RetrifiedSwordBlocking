package com.sword.block.config;

import com.sword.block.SwordBlock;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ConfigHandler {
    
    public static Configuration config;
    
    // Config options
    public static boolean enableVanillaSwords = true;
    public static boolean enableTinkersWeapons = true;
    public static boolean enableDebugLogging = false;
    public static boolean requireAttackDamage = true;
    public static String[] whitelistedItems = new String[0];
    public static String[] blacklistedItems = new String[0];
    
    // Processed sets for faster lookup
    public static Set<String> whitelistedItemsSet = new HashSet<>();
    public static Set<String> blacklistedItemsSet = new HashSet<>();
    
    public static void init(File configFile) {
        config = new Configuration(configFile);
        
        try {
            config.load();
            
            // General settings
            enableVanillaSwords = config.getBoolean("Enable Vanilla Swords", "General", true,
                    "Enable blocking for vanilla swords");
                    
            enableTinkersWeapons = config.getBoolean("Enable Tinkers Weapons", "General", true,
                    "Enable blocking for Tinkers' Construct weapons");
                    
            requireAttackDamage = config.getBoolean("Require Attack Damage", "General", true,
                    "Only allow items with attack damage to block (prevents sword parts from blocking)");
                    
            enableDebugLogging = config.getBoolean("Enable Debug Logging", "General", false,
                    "Enable debug logging to help identify items");
            
            // Item lists
            whitelistedItems = config.getStringList("Whitelist", "Item Lists",
                    new String[]{"examplemod:custom_sword", "anothermod:special_blade"},
                    "Items that should be able to block even if they don't meet other criteria");
                    
            blacklistedItems = config.getStringList("Blacklist", "Item Lists",
                    new String[]{"tconstruct:sword_blade", "tconstruct:wide_guard"},
                    "Items that should NOT be able to block even if they meet other criteria");
            
            // Process arrays into sets for faster lookup
            whitelistedItemsSet = new HashSet<>(Arrays.asList(whitelistedItems));
            blacklistedItemsSet = new HashSet<>(Arrays.asList(blacklistedItems));
            
        } catch (Exception e) {
            SwordBlock.LOGGER.error("Error loading configuration", e);
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }
    
    public static void save() {
        if (config != null && config.hasChanged()) {
            config.save();
        }
    }
}