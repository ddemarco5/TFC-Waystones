package com.example.TFCWaystones;

import com.example.TFCWaystones.gui.GuiHandler;
import com.example.TFCWaystones.item.ItemObsidianKnife;
import com.example.TFCWaystones.item.ItemObsidianRock;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipe;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipeSimple;
import net.dries007.tfc.api.recipes.knapping.KnappingType;
import net.dries007.tfc.objects.CreativeTabsTFC;
import net.dries007.tfc.objects.items.ItemMisc;
import net.dries007.tfc.objects.items.ItemTFC;
import net.dries007.tfc.objects.recipes.RecipeRegistryEvents;
import net.dries007.tfc.objects.recipes.RecipeUtils;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistryModifiable;
import org.apache.logging.log4j.Logger;

import net.dries007.tfc.TerraFirmaCraft;
import net.blay09.mods.waystones.Waystones;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// Import our stuff
import com.example.TFCWaystones.item.ItemWarpStone;


// Hacky shit
import javax.annotation.Nonnull;
import java.lang.reflect.*;

@Mod(modid = TFCWaystones.MOD_ID, name = TFCWaystones.NAME, version = TFCWaystones.VERSION, dependencies = "required-after:tfc@[1.5.2.152,);required-after:waystones@[4.1.0,);")
@Mod.EventBusSubscriber
public class TFCWaystones
{
    public static final String MOD_ID = "tfcwaystones";
    public static final String NAME = "TFC Waystones";
    public static final String VERSION = "0.0";

    @Mod.Instance
    private static TFCWaystones INSTANCE = null;

    //private static Logger logger;
    public static Logger logger;

    public static final ItemMisc KNIFE_HEAD = new ItemMisc(Size.SMALL, Weight.LIGHT);
    public static ItemObsidianRock OBSIDIAN_ROCK;
    //public static ItemMisc KNIFE_HEAD;

    /*
    @GameRegistry.ObjectHolder(ItemWarpStone.name)
    public static final Item itemWarpStone = Items.AIR;
    */

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        logger.info("We hooked registermodels!");

        // THIS IS IT! THIS IS HOW/WHERE I CAN FRONTEND MODELRESOURCES DESPITE THE MOD
        ModelResourceLocation changedIcon = new ModelResourceLocation("tfcwaystones:warp_stone_test");
        ResourceLocation targetResource = new ResourceLocation("waystones:warp_stone");

        //Item wayStone = ForgeRegistries.ITEMS.getValue(targetResource);
        if (ForgeRegistries.ITEMS.containsKey(targetResource)) {
            ModelLoader.setCustomModelResourceLocation(ForgeRegistries.ITEMS.getValue(targetResource), 0, changedIcon);
            logger.info("In registerModels, just ran setCustomModelResourceLocation on {}", targetResource);
        }
        else {
            logger.info("In registerModels, could not find the target resource: {}", targetResource);
        }

        ModelResourceLocation changedIcon2 = new ModelResourceLocation("tfcwaystones:obsidian_knife");
        ResourceLocation targetResource2 = new ResourceLocation("tfcwaystones:obsidian_knife");
        if (ForgeRegistries.ITEMS.containsKey(targetResource2)) {
            ModelLoader.setCustomModelResourceLocation(ForgeRegistries.ITEMS.getValue(targetResource2), 0, changedIcon2);
            logger.info("In registerModels, just ran setCustomModelResourceLocation on {}", targetResource2);
        }
        else {
            logger.info("In registerModels, could not find the target resource: {}", targetResource2);
        }

        ModelResourceLocation changedIcon3 = new ModelResourceLocation("tfcwaystones:obsidian_knife_head");
        ResourceLocation targetResource3 = new ResourceLocation("tfcwaystones:obsidian_knife_head");
        if (ForgeRegistries.ITEMS.containsKey(targetResource3)) {
            ModelLoader.setCustomModelResourceLocation(ForgeRegistries.ITEMS.getValue(targetResource3), 0, changedIcon3);
            logger.info("In registerModels, just ran setCustomModelResourceLocation on {}", targetResource3);
        }
        else {
            logger.info("In registerModels, could not find the target resource: {}", targetResource3);
        }

        ModelResourceLocation changedIcon4 = new ModelResourceLocation("tfcwaystones:obsidian_rock");
        ResourceLocation targetResource4 = new ResourceLocation("tfcwaystones:obsidian_rock");
        if (ForgeRegistries.ITEMS.containsKey(targetResource4)) {
            ModelLoader.setCustomModelResourceLocation(ForgeRegistries.ITEMS.getValue(targetResource4), 0, changedIcon4);
            logger.info("In registerModels, just ran setCustomModelResourceLocation on {}", targetResource4);
        }
        else {
            logger.info("In registerModels, could not find the target resource: {}", targetResource4);
        }


    }

    @SubscribeEvent
    public static void registerKnappRecipe(RegistryEvent.Register<KnappingRecipe> event) {
        IForgeRegistry registry = event.getRegistry();

        //KnappingRecipe oneBladeRecipe = new KnappingRecipeSimple(KnappingType.STONE, false, new ItemStack(KNIFE_HEAD), "XXXXX", "XXXXX", "XX XX", "XXXXX", "XXXXX").setRegistryName("test_knapp");
        //KnappingRecipe oneBladeRecipe = new KnappingRecipeSimple(GuiHandler.OBSIDIAN_KNAPPING, false, new ItemStack(KNIFE_HEAD), "X  X ", "XX XX", "XX XX", "XX XX", "XX XX").setRegistryName("obsidian_knife_head_1");
        //KnappingRecipe oneBladeRecipe = new KnappingRecipeSimple(GuiHandler.OBSIDIAN_KNAPPING, false, new ItemStack(KNIFE_HEAD), "X  X ", "XX XX", "XX XX", "XX XX", "XX XX").setRegistryName("obsidian_knife_head_1");
        //registry.register(oneBladeRecipe);
        registry.registerAll(
                new KnappingRecipeSimple(GuiHandler.OBSIDIAN_KNAPPING, false, new ItemStack(KNIFE_HEAD, 1), "X ", "XX", "XX", "XX", "XX").setRegistryName("obsidian_knife_head_1"),
                new KnappingRecipeSimple(GuiHandler.OBSIDIAN_KNAPPING, false, new ItemStack(KNIFE_HEAD, 2), "X  X ", "XX XX", "XX XX", "XX XX", "XX XX").setRegistryName("obsidian_knife_heads_1"),
                new KnappingRecipeSimple(GuiHandler.OBSIDIAN_KNAPPING, false, new ItemStack(KNIFE_HEAD, 2),  "X   X", "XX XX", "XX XX", "XX XX", "XX XX").setRegistryName("obsidian_knife_heads_2"),
                new KnappingRecipeSimple(GuiHandler.OBSIDIAN_KNAPPING, false, new ItemStack(KNIFE_HEAD, 2), " X X ", "XX XX", "XX XX", "XX XX", "XX XX").setRegistryName("obsidian_knife_heads_3")
        );
        logger.info("Registered our knapping recipes");

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

        logger.info("Trying to register the dagger in registerItems");
        ItemObsidianKnife obsidianKnife = new ItemObsidianKnife();
        modRegistry.register(obsidianKnife);
        obsidianKnife.registerOres();

        // Test head
        /*
        Item testHead = new ItemMisc(Size.SMALL, Weight.LIGHT);
        testHead.setRegistryName(TFCWaystones.MOD_ID, "obsidian_knife_head");
        testHead.setUnlocalizedName(TFCWaystones.MOD_ID.toLowerCase() + "." + "obsidian_knife_head");
        modRegistry.register(testHead);
        OreDictionary.registerOre("knifeHead", testHead);
        */
        KNIFE_HEAD.setRegistryName(TFCWaystones.MOD_ID, "obsidian_knife_head");
        KNIFE_HEAD.setUnlocalizedName(TFCWaystones.MOD_ID.toLowerCase() + "." + "obsidian_knife_head");
        KNIFE_HEAD.setCreativeTab(CreativeTabsTFC.CT_ROCK_ITEMS);
        modRegistry.register(KNIFE_HEAD);
        OreDictionary.registerOre("knifeHead", KNIFE_HEAD);
        OreDictionary.registerOre("knifeHeadObsidian", KNIFE_HEAD);

        //ItemObsidianRock obsidianRock = new ItemObsidianRock();
        //ItemObsidianRock obsidianRock;
        //obsidianRock.setRegistryName(TFCWaystones.MOD_ID, "obsidian_rock");
        //obsidianRock.setUnlocalizedName(TFCWaystones.MOD_ID.toLowerCase() + "." + "obsidian_rock");
        //modRegistry.register(obsidianRock);
        modRegistry.register(OBSIDIAN_ROCK = new ItemObsidianRock());
        OreDictionary.registerOre("rock", OBSIDIAN_ROCK);
        OreDictionary.registerOre("rockObsidian", OBSIDIAN_ROCK);


    }


    // Change vanilla obsidian blocks to drop our rocks instead of blocks
    @SubscribeEvent
    public static void harvestDropsEvent(BlockEvent.HarvestDropsEvent event) {
        if (event.getHarvester() != null) {
            if (event.getState() == Blocks.OBSIDIAN.getDefaultState()) {
                List<ItemStack> dropList = event.getDrops();
                dropList.clear();
                dropList.add(new ItemStack(OBSIDIAN_ROCK, 4));
            }
        }
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
            if (i.getResourceDomain().equals(MOD_ID)) {
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

    public static TFCWaystones getInstance() {
        return INSTANCE;
    }


    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }

}