package com.sword.block.util;

import com.sword.block.SwordBlock;
import com.sword.block.config.ConfigHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SwordHelper {
    
    // Set of known sword items from Tinkers' Construct and other mods
    private static final Set<String> SWORD_ITEM_NAMES = new HashSet<>(Arrays.asList(
        // Vanilla swords
        "minecraft:wooden_sword", "minecraft:stone_sword", "minecraft:iron_sword",
        "minecraft:golden_sword", "minecraft:diamond_sword",
        
        // Tinkers' Construct 1.12.2 weapons (melee weapons that can block)
        "tconstruct:longsword", "tconstruct:rapier", "tconstruct:dagger",
        "tconstruct:cutlass", "tconstruct:broadsword", "tconstruct:katana",
        "tconstruct:cleaver", "tconstruct:battlesign",
        
        // Tinkers' Construct 1.7.10 legacy weapons
        "tconstruct:sword", "tconstruct:longsword", "tconstruct:rapier", 
        "tconstruct:dagger", "tconstruct:cutlass", "tconstruct:broadsword",
        
        // Common modded swords patterns
        "_sword", "sword_", "blade", "_blade", "blade_",
        "_katana", "katana_", "_rapier", "rapier_", "_dagger", "dagger_",
        "_cutlass", "cutlass_", "_saber", "saber_", "_scimitar", "scimitar_"
    ));
    
    // Set of known tool registry names from Tinkers' Construct (both 1.7.10 and 1.12.2)
    private static final Set<String> TINKERS_WEAPONS = new HashSet<>(Arrays.asList(
        // 1.12.2 Tinkers' Construct weapons
        "tconstruct:longsword", "tconstruct:rapier", "tconstruct:dagger",
        "tconstruct:cutlass", "tconstruct:broadsword", "tconstruct:katana",
        "tconstruct:cleaver", "tconstruct:battlesign",
        
        // 1.7.10 Tinkers' Construct weapons (legacy support)
        "tconstruct:sword", "tconstruct:longsword", "tconstruct:rapier", 
        "tconstruct:dagger", "tconstruct:cutlass", "tconstruct:broadsword"
    ));
    
    /**
     * Determines if an item should be able to block like a sword
     */
    public static boolean canBlock(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        
        Item item = stack.getItem();
        String registryName = getRegistryName(item);
        if (registryName == null) {
            return false;
        }
        
        // Check blacklisted items first
        if (ConfigHandler.blacklistedItemsSet.contains(registryName)) {
            if (ConfigHandler.enableDebugLogging) {
                SwordBlock.LOGGER.debug("Item {} is in blacklist, denying blocking", registryName);
            }
            return false;
        }
        
        // Check whitelisted items
        if (ConfigHandler.whitelistedItemsSet.contains(registryName)) {
            if (ConfigHandler.enableDebugLogging) {
                SwordBlock.LOGGER.debug("Item {} is in whitelist, allowing blocking", registryName);
            }
            return true;
        }
        
        // Check if it's a vanilla sword
        if (ConfigHandler.enableVanillaSwords && item instanceof ItemSword) {
            if (ConfigHandler.requireAttackDamage && !hasAttackDamage(item, stack)) {
                if (ConfigHandler.enableDebugLogging) {
                    SwordBlock.LOGGER.debug("Item {} is a vanilla sword but has no attack damage, denying blocking", registryName);
                }
                return false;
            }
            if (ConfigHandler.enableDebugLogging) {
                SwordBlock.LOGGER.debug("Item {} is a vanilla sword, allowing blocking", registryName);
            }
            return true;
        }
        
        // Check if it's a Tinkers' Construct tool with weapon capabilities
        if (ConfigHandler.enableTinkersWeapons && isTinkersWeapon(item, stack)) {
            if (ConfigHandler.requireAttackDamage && !hasAttackDamage(item, stack)) {
                if (ConfigHandler.enableDebugLogging) {
                    SwordBlock.LOGGER.debug("Item {} is a Tinkers' item but has no attack damage, denying blocking", registryName);
                }
                return false;
            }
            if (ConfigHandler.enableDebugLogging) {
                SwordBlock.LOGGER.debug("Item {} identified as Tinkers' weapon, allowing blocking", registryName);
            }
            return true;
        }
        
        return false;
    }
    
    /**
     * Check if item is a Tinkers' Construct weapon
     */
    private static boolean isTinkersWeapon(Item item, ItemStack stack) {
        String registryName = getRegistryName(item);
        if (registryName == null) {
            return false;
        }
        
        // Direct registry name check
        if (TINKERS_WEAPONS.contains(registryName)) {
            return true;
        }
        
        // Check for Tinkers' Construct mod prefix and weapon-like names
        if (registryName.startsWith("tconstruct:")) {
            String itemName = registryName.substring(11); // Remove "tconstruct:" prefix
            return itemName.contains("sword") || itemName.contains("rapier") || 
                   itemName.contains("dagger") || itemName.contains("cutlass") ||
                   itemName.contains("broadsword") || itemName.contains("katana") ||
                   itemName.contains("cleaver") || itemName.contains("battlesign");
        }
        
        // Check NBT data for Tinkers' Construct tools
        if (isTinkersToolByNBT(stack)) {
            return isTinkersWeaponByStats(stack);
        }
        
        // Check for Tinkers' tools by class name (reflection-based check)
        try {
            Class<?> itemClass = item.getClass();
            String className = itemClass.getName();
            
            // Check if it's a Tinkers' tool
            if (className.contains("slimeknights.tconstruct") || className.contains("tconstruct")) {
                // Check if it has weapon-like properties
                return hasWeaponProperties(item, stack);
            }
        } catch (Exception e) {
            // Ignore reflection errors
        }
        
        return false;
    }
    
    /**
     * Check if an ItemStack is a Tinkers' Construct tool by its NBT data
     */
    private static boolean isTinkersToolByNBT(ItemStack stack) {
        try {
            if (!stack.hasTagCompound()) {
                return false;
            }
            
            // Tinkers' tools have specific NBT structure
            return stack.getTagCompound().hasKey("TinkerData") || 
                   stack.getTagCompound().hasKey("InfiTool") ||
                   stack.getTagCompound().hasKey("Modifiers");
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if a Tinkers' tool is weapon-like by checking its stats
     */
    private static boolean isTinkersWeaponByStats(ItemStack stack) {
        try {
            if (!stack.hasTagCompound()) {
                return false;
            }
            
            // Check for attack damage stat which indicates it's a weapon
            if (stack.getTagCompound().hasKey("Stats")) {
                // Most Tinkers' weapons have attack damage > 0
                return hasWeaponProperties(stack.getItem(), stack);
            }
            
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if an item has weapon-like properties
     */
    private static boolean hasWeaponProperties(Item item, ItemStack stack) {
        return hasAttackDamage(item, stack);
    }
    
    /**
     * Check if an item has attack damage
     */
    private static boolean hasAttackDamage(Item item, ItemStack stack) {
        try {
            // Check if the item has attack damage attribute
            return stack.getAttributeModifiers(net.minecraft.inventory.EntityEquipmentSlot.MAINHAND)
                    .containsKey(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get registry name of an item safely
     */
    private static String getRegistryName(Item item) {
        try {
            return item.getRegistryName() != null ? item.getRegistryName().toString() : null;
        } catch (Exception e) {
            return null;
        }
    }
}