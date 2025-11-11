package com.sword.block.mixin;

import com.sword.block.SwordBlock;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends EntityLivingBase {

    public MixinEntityPlayer(World world) {
        super(world);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/ISpecialArmor$ArmorProperties;applyArmor(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/NonNullList;Lnet/minecraft/util/DamageSource;D)F"), method = "damageEntity(Lnet/minecraft/util/DamageSource;F)V")
    private float onSwordBlocking(EntityLivingBase entity, NonNullList<ItemStack> inventory, DamageSource source, double damage) {
        if (!source.isUnblockable() && isBlocking() && damage > 0.0D) {
            // Block 50% of damage (player takes only 50% of original damage)
            damage = damage * 0.5D;
        }
        return ISpecialArmor.ArmorProperties.applyArmor(entity, inventory, source, damage);
    }

    @Unique
    private boolean isBlocking() {
        if (this.isHandActive() && !this.activeItemStack.isEmpty()) {
            Item item = this.activeItemStack.getItem();
            return item.getItemUseAction(this.activeItemStack) == SwordBlock.SWORD;
        }
        return false;
    }

}