package com.sword.block.mixin;

import com.sword.block.SwordBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class MixinItem {

    @Inject(method = "getItemUseAction", at = @At("HEAD"), cancellable = true)
    public void getItemUseAction(ItemStack stack, CallbackInfoReturnable<EnumAction> cir) {
        if (canBlock(stack)) {
            cir.setReturnValue(SwordBlock.SWORD);
        }
    }

    @Inject(method = "getMaxItemUseDuration", at = @At("HEAD"), cancellable = true)
    public void getMaxItemUseDuration(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (canBlock(stack)) {
            cir.setReturnValue(72000);
        }
    }

    @Inject(method = "onItemRightClick", at = @At("HEAD"), cancellable = true)
    public void onItemRightClick(World world, EntityPlayer player, EnumHand hand, CallbackInfoReturnable<ActionResult<ItemStack>> cir) {
        ItemStack heldItem = player.getHeldItem(hand);
        
        if (canBlock(heldItem)) {
            ItemStack offHandStack = player.getHeldItem(EnumHand.OFF_HAND);
            boolean offHandInUse = offHandStack.getItem().getItemUseAction(offHandStack) != EnumAction.NONE;
            
            if (hand == EnumHand.MAIN_HAND && !offHandInUse) {
                player.setActiveHand(hand);
                cir.setReturnValue(new ActionResult<>(EnumActionResult.SUCCESS, heldItem));
            } else if (hand == EnumHand.OFF_HAND || offHandInUse) {
                cir.setReturnValue(new ActionResult<>(EnumActionResult.FAIL, heldItem));
            }
        }
    }

    /**
     * Super simple logic - only allow actual swords to block, block all tool parts
     */
    private static boolean canBlock(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        
        Item item = stack.getItem();
        
        // 1. All vanilla swords can block
        if (item instanceof ItemSword) {
            return true;
        }
        
        // 2. Get the registry name safely
        String registryName;
        try {
            registryName = item.getRegistryName() != null ? item.getRegistryName().toString() : "";
        } catch (Exception e) {
            return false;
        }
        
        // 3. FIRST - Block ALL tool parts explicitly (before checking for "sword")
        if (isToolPart(registryName)) {
            return false;
        }
        
        // 4. Allow items with "sword" in the name (but not tool parts)
        if (registryName.contains("sword")) {
            return true;
        }
        
        // 5. Only allow Tinkers' Construct COMPLETED weapons (with NBT data)
        if (registryName.startsWith("tconstruct:")) {
            // Must have NBT data to be a completed weapon, not a part
            if (stack.hasTagCompound()) {
                return (stack.getTagCompound().hasKey("TinkerData") || 
                        stack.getTagCompound().hasKey("InfiTool") ||
                        stack.getTagCompound().hasKey("Modifiers")) &&
                       isTinkersWeapon(registryName);
            }
        }
        
        return false;
    }
    
    /**
     * Check if an item is a tool part that should NEVER block
     */
    private static boolean isToolPart(String registryName) {
        // Block ALL Tinkers' tool parts
        return registryName.contains("sword_blade") ||
               registryName.contains("knife_blade") ||
               registryName.contains("wide_guard") ||
               registryName.contains("hand_guard") ||
               registryName.contains("cross_guard") ||
               registryName.contains("tool_rod") ||
               registryName.contains("binding") ||
               registryName.contains("tough_binding") ||
               registryName.contains("tough_tool_rod") ||
               registryName.contains("_head") ||
               registryName.contains("_plate") ||
               registryName.contains("bow_limb") ||
               registryName.contains("arrow_head") ||
               registryName.contains("arrow_shaft") ||
               registryName.contains("fletching") ||
               registryName.equals("minecraft:stick") ||
               registryName.equals("minecraft:blaze_rod");
    }
    
    /**
     * Check if a Tinkers' item is actually a weapon (not a tool part)
     */
    private static boolean isTinkersWeapon(String registryName) {
        // Only allow actual Tinkers' weapons, not tool parts
        return registryName.equals("tconstruct:longsword") ||
               registryName.equals("tconstruct:rapier") ||
               registryName.equals("tconstruct:dagger") ||
               registryName.equals("tconstruct:cutlass") ||
               registryName.equals("tconstruct:broadsword") ||
               registryName.equals("tconstruct:katana") ||
               registryName.equals("tconstruct:cleaver") ||
               registryName.equals("tconstruct:battlesign") ||
               registryName.equals("tconstruct:sword"); // Legacy 1.7.10
    }
}