package com.ddemarco5.TFCWaystones.registry;

import com.ddemarco5.TFCWaystones.TFCWaystones;
import com.ddemarco5.TFCWaystones.gui.GuiHandler;
import com.ddemarco5.TFCWaystones.item.*;
import com.mojang.realmsclient.util.Pair;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipe;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipeSimple;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.Logger;


@Mod.EventBusSubscriber
public class RegistrationListeners {

    private static Logger logger = TFCWaystones.logger;

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        // THIS IS IT! THIS IS HOW/WHERE I CAN FRONTEND MODELRESOURCES DESPITE THE MOD
        logger.info("We hooked registermodels!");

        for (Pair<String, String> p : TFCWaystones.MODEL_OVERRIDES) {
            ResourceLocation targetResource = new ResourceLocation(p.first());
            ModelResourceLocation modelResource = new ModelResourceLocation(p.second());

            if (ForgeRegistries.ITEMS.containsKey(targetResource)) {
                ModelLoader.setCustomModelResourceLocation(ForgeRegistries.ITEMS.getValue(targetResource), 0, modelResource);
                logger.info("Registered model {} to item {}", modelResource, targetResource);
            }
            else {
                logger.info("Could not find the target resource: {}", targetResource);
            }
        }
    }

    @SubscribeEvent
    public static void registerKnappRecipe(RegistryEvent.Register<KnappingRecipe> event) {
        logger.info("We hooked registerknapping!");
        IForgeRegistry registry = event.getRegistry();

        registry.registerAll(
                //new KnappingRecipeSimple(GuiHandler.OBSIDIAN_KNAPPING, false, new ItemStack(TFCWaystones.OBSIDIAN_KNIFE_HEAD, 1), "X ", "XX", "XX", "XX", "XX").setRegistryName("obsidian_knife_head_1"),
                new KnappingRecipeSimple(GuiHandler.OBSIDIAN_KNAPPING, false, new ItemStack(TFCWaystones.OBSIDIAN_KNIFE_HEAD, 1), "X ", "XX", "XX", "XX", "XX").setRegistryName("obsidian_knife_head_1"),
                new KnappingRecipeSimple(GuiHandler.OBSIDIAN_KNAPPING, false, new ItemStack(TFCWaystones.OBSIDIAN_KNIFE_HEAD, 2), "X  X ", "XX XX", "XX XX", "XX XX", "XX XX").setRegistryName("obsidian_knife_heads_1"),
                new KnappingRecipeSimple(GuiHandler.OBSIDIAN_KNAPPING, false, new ItemStack(TFCWaystones.OBSIDIAN_KNIFE_HEAD, 2),  "X   X", "XX XX", "XX XX", "XX XX", "XX XX").setRegistryName("obsidian_knife_heads_2"),
                new KnappingRecipeSimple(GuiHandler.OBSIDIAN_KNAPPING, false, new ItemStack(TFCWaystones.OBSIDIAN_KNIFE_HEAD, 2), " X X ", "XX XX", "XX XX", "XX XX", "XX XX").setRegistryName("obsidian_knife_heads_3")
        );
        logger.info("Registered our knapping recipes");

    }

    // Items should be created and registered here
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        logger.info("We hooked registeritems!");


        // Wipe warpstone to make sure we can do it correctly
        IForgeRegistry modRegistry = event.getRegistry();



        ItemWarpStone warpStone = new ItemWarpStone();
        warpStone.patchResource();
        modRegistry.register(TFCWaystones.WARP_STONE = warpStone);
        warpStone.registerOres();

        ItemObsidianKnifeHead obsidianKnifeHead = new ItemObsidianKnifeHead();
        modRegistry.register(TFCWaystones.OBSIDIAN_KNIFE_HEAD = obsidianKnifeHead);
        obsidianKnifeHead.registerOres();

        ItemObsidianKnife obsidianKnife = new ItemObsidianKnife();
        modRegistry.register(TFCWaystones.OBSIDIAN_KNIFE = obsidianKnife);
        obsidianKnife.registerOres();

        ItemObsidianRock obsidianRock = new ItemObsidianRock();
        modRegistry.register(TFCWaystones.OBSIDIAN_ROCK = obsidianRock);

        ItemEmptyWarpStone emptyWarpStone = new ItemEmptyWarpStone();
        modRegistry.register(TFCWaystones.EMPTY_WARP_STONE = emptyWarpStone);
        emptyWarpStone.registerOres();

    }

}
