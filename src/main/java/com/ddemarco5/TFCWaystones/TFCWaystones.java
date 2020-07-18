package com.ddemarco5.TFCWaystones;

import com.ddemarco5.TFCWaystones.gui.GuiHandler;
import com.ddemarco5.TFCWaystones.item.*;
import com.mojang.realmsclient.util.Pair;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import net.minecraftforge.registries.IForgeRegistryModifiable;
import org.apache.logging.log4j.Logger;

import net.dries007.tfc.TerraFirmaCraft;

import java.util.ArrayList;
import java.util.Set;

// Import our stuff


@Mod(modid = TFCWaystones.MOD_ID, name = TFCWaystones.NAME, version = TFCWaystones.VERSION, dependencies = "required-after:tfc@[1.5.2.152,);required-after:waystones@[4.1.0,);")
@Mod.EventBusSubscriber
public class TFCWaystones
{
    public static final String MOD_ID = "tfcwaystones";
    public static final String NAME = "TFC Waystones";
    public static final String VERSION = "0.0";

    @Mod.Instance
    private static TFCWaystones INSTANCE = null;

    public static Logger logger;

    // Declares later to be set from items created in registration
    public static ItemObsidianKnifeHead OBSIDIAN_KNIFE_HEAD;
    public static ItemObsidianKnife OBSIDIAN_KNIFE;
    public static ItemObsidianRock OBSIDIAN_ROCK;
    public static ItemWarpStone WARP_STONE;
    public static ItemEmptyWarpStone EMPTY_WARP_STONE;

    // Resource, Model
    public static ArrayList<Pair<String, String>> MODEL_OVERRIDES = new ArrayList<Pair<String, String>>();


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();

        logger.info("We're in preInit");


        // Define our overrides
        MODEL_OVERRIDES.add(Pair.of("waystones:warp_stone", "tfcwaystones:warp_stone"));
        MODEL_OVERRIDES.add(Pair.of("tfcwaystones:obsidian_knife", "tfcwaystones:obsidian_knife"));
        MODEL_OVERRIDES.add(Pair.of("tfcwaystones:obsidian_knife_head", "tfcwaystones:obsidian_knife_head"));
        MODEL_OVERRIDES.add(Pair.of("tfcwaystones:obsidian_rock", "tfcwaystones:obsidian_rock"));
        MODEL_OVERRIDES.add(Pair.of("tfcwaystones:empty_warp_stone", "tfcwaystones:empty_warp_stone"));

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {

        logger.info("We're in Init");

        ArrayList<ResourceLocation> shitlist = new ArrayList<ResourceLocation>(0);

        IForgeRegistryModifiable modRegistry = (IForgeRegistryModifiable) ForgeRegistries.RECIPES;

        // Find the recipes that we are going to be overriding
        Set<ResourceLocation> resourceList = modRegistry.getKeys();
        for (ResourceLocation i: resourceList) {
            if (i.getResourceDomain().equals(TFCWaystones.MOD_ID)) {
                logger.info("Found our recipe: {}", i.toString());
                ResourceLocation removeTarget = new ResourceLocation(TerraFirmaCraft.MOD_ID, i.getResourcePath());
                // Check to make sure TFC has its own version we want to wipe
                if (modRegistry.containsKey(removeTarget)) {
                    //modRegistry.remove(removeTarget);
                    logger.info("Found TFC's version, {}", removeTarget.toString());
                    shitlist.add(removeTarget);
                }

            }
        }

        // Delete the originals
        for (ResourceLocation i: shitlist) {
            logger.info("Deleting TFC's version, {}", i.toString());
            modRegistry.remove(i);
        }

        // Test register gui handler
        NetworkRegistry.INSTANCE.registerGuiHandler(TFCWaystones.getInstance(), new GuiHandler());
        logger.info("Registered our gui handler");

    }


    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }


    // This is for the GUI handler
    public static TFCWaystones getInstance() {
        return INSTANCE;
    }


}