package com.sword.block;

import com.sword.block.config.ConfigHandler;
import com.sword.block.proxy.CommonProxy;
import net.minecraft.item.EnumAction;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = SwordBlock.MODID, name = SwordBlock.NAME, version = SwordBlock.VERSION)
public class SwordBlock {

    public static final String MODID = "sword_block";

    public static final String NAME = "Sword Block";

    public static final String VERSION = "1.1.0";

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @SidedProxy(clientSide = "com.sword.block.proxy.ClientProxy",
            serverSide = "com.sword.block.proxy.CommonProxy")
    public static CommonProxy proxy;

    public static final EnumAction SWORD = EnumHelper.addAction("SWORD:BLOCK");

    @EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        ConfigHandler.init(event.getSuggestedConfigurationFile());
        proxy.preInit(event);
    }

    @EventHandler
    public void init(final FMLInitializationEvent event) {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(final FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

}