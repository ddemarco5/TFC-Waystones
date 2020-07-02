package com.example.TFCWaystones;

import net.blay09.mods.waystones.WaystoneConfig;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.util.ITabCompleter;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.model.Models;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistryModifiable;
import org.apache.logging.log4j.Logger;

import net.dries007.tfc.TerraFirmaCraft;
import net.blay09.mods.waystones.Waystones;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Set;

// Import our stuff
import com.example.TFCWaystones.item.ItemWarpStone;


// Hacky shit
import java.lang.reflect.*;

@Mod(modid = TFCWaystones.MODID, name = TFCWaystones.NAME, version = TFCWaystones.VERSION, dependencies = "required-after:tfc@[1.5.2.152,);required-after:waystones@[4.1.0,);")
@Mod.EventBusSubscriber
public class TFCWaystones
{
    public static final String MODID = "tfcwaystones";
    public static final String NAME = "TFC Waystones";
    public static final String VERSION = "0.0";

    //private static Logger logger;
    public static Logger logger;

    @GameRegistry.ObjectHolder(ItemWarpStone.name)
    public static final Item itemWarpStone = Items.AIR;

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        logger.info("We hooked registermodels!");

        // THIS IS IT! THIS IS HOW/WHERE I CAN FRONTEND MODELRESOURCES DESPITE THE MOD
        ModelResourceLocation changedIcon = new ModelResourceLocation("tfcwaystones:warp_stone_test");
        //ResourceLocation targetResource = new ResourceLocation("minecraft:stick");
        ResourceLocation targetResource = new ResourceLocation("waystones:warp_stone");
        //Item wayStone = ForgeRegistries.ITEMS.getValue(targetResource);
        if (ForgeRegistries.ITEMS.containsKey(targetResource)) {
            ModelLoader.setCustomModelResourceLocation(ForgeRegistries.ITEMS.getValue(targetResource), 0, changedIcon);
            logger.info("In registerModels, just ran setCustomModelResourceLocation on {}", targetResource);
        }
        else {
            logger.info("In registerModels, could not find the target resource");
        }
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        logger.info("Hooked registration!");


        // Wipe warpstone to make sure we can do it correctly
        IForgeRegistryModifiable modRegistry = (IForgeRegistryModifiable) event.getRegistry();

        Item testItem = new ItemWarpStone();

        ResourceLocation targetLocation = new ResourceLocation(Waystones.MOD_ID, "warp_stone");

        // Turbo hack. Forge won't let us set the registryName twice, so we have to use reflection to force a change
        // All because they use setRegistryName in the constructor
        try {
            Field resourcelocationField = IForgeRegistryEntry.Impl.class.getDeclaredField("registryName");
            resourcelocationField.setAccessible(true);
            resourcelocationField.set(testItem, targetLocation);
            //IForgeRegistryEntry.Impl.class.getDeclaredField("registryName").set(testItem, GameData.checkPrefix(new ResourceLocation(Waystones.MOD_ID, "warp_stone").toString(), true));
            logger.info("Forced resource {} to {}", testItem.toString(), targetLocation);

        } catch (Exception e) {
            //System.out.println("FOOK");
            logger.info("FOOK: {}", e);
        }
        logger.info("Registering Item: {}", testItem);

        modRegistry.register(testItem);


    }


    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();

        logger.info("We're in preInit");

        // We don't need to register our events because our class is an Event bus subscriber and they are public static void
        // Test this event handler
        //TestEventHandler testHandler = new TestEventHandler();
        //TestRegHandler testRegHandler = new TestRegHandler();
        //MinecraftForge.EVENT_BUS.register(testHandler);
        //MinecraftForge.EVENT_BUS.register(testRegHandler);

    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {

        logger.info("We're in Init");

        ArrayList<ResourceLocation> shitlist = new ArrayList<ResourceLocation>(0);

        IForgeRegistryModifiable modRegistry = (IForgeRegistryModifiable) ForgeRegistries.RECIPES;

        // Find the recipes that we are going to be overriding
        Set<ResourceLocation> resourceList = modRegistry.getKeys();
        for (ResourceLocation i: resourceList) {
            if (i.getResourceDomain().equals(MODID)) {
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

        /*
        // Wipe warpstone to make sure we can do it correctly
        IForgeRegistryModifiable testRegistry = (IForgeRegistryModifiable) ForgeRegistries.ITEMS;

        ResourceLocation targetResource = new ResourceLocation(Waystones.MOD_ID, "warp_stone");
        if (modRegistry.containsKey(targetResource)) {
            logger.info("zonking warp stone");

            //testRegistry.remove(targetResource);
            //IForgeRegistryEntry<Item> replacement = new IF

            IForgeRegistryEntry<Item> replacement = new ItemWarpStone();
            ResourceLocation newLoc = new ResourceLocation(Waystones.MOD_ID, "warp_stone");
            replacement.setRegistryName(newLoc);
            logger.info("Item domain is: {}", replacement.getRegistryName());

            //testRegistry.register(new ItemWarpStone());
            //modRegistry.register(new ItemWarpStone());
        }
        */


    }


    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }


}
