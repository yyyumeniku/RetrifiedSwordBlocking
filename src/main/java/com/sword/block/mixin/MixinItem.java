package com.sword.block.mixin;

import com.sword.block.SwordBlock;
import com.sword.block.util.SwordHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
        if (SwordHelper.canBlock(stack)) {
            cir.setReturnValue(SwordBlock.SWORD);
        }
    }

    @Inject(method = "getMaxItemUseDuration", at = @At("HEAD"), cancellable = true)
    public void getMaxItemUseDuration(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (SwordHelper.canBlock(stack)) {
            cir.setReturnValue(72000);
        }
    }

    @Inject(method = "onItemRightClick", at = @At("HEAD"), cancellable = true)
    public void onItemRightClick(World world, EntityPlayer player, EnumHand hand, CallbackInfoReturnable<ActionResult<ItemStack>> cir) {
        ItemStack heldItem = player.getHeldItem(hand);
        
        if (SwordHelper.canBlock(heldItem)) {
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
}