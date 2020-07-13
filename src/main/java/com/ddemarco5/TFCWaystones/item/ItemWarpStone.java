package com.ddemarco5.TFCWaystones.item;

import com.ddemarco5.TFCWaystones.TFCWaystones;
import net.blay09.mods.waystones.Waystones;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistryEntry;


import javax.annotation.Nonnull;
import java.lang.reflect.Field;

public class ItemWarpStone extends net.blay09.mods.waystones.item.ItemWarpStone implements IItemSize {

    private static final ResourceLocation resourceOverride = new ResourceLocation(Waystones.MOD_ID, "warp_stone");


    public void patchResource() {

        // Turbo hack. Forge won't let us set the registryName twice, so we have to use reflection to force a change
        // All because they use setRegistryName in the constructor
        try {
            Field resourcelocationField = IForgeRegistryEntry.Impl.class.getDeclaredField("registryName");
            resourcelocationField.setAccessible(true);
            resourcelocationField.set(this, resourceOverride);
            TFCWaystones.logger.info("Reflection patched resource {} to {}", TFCWaystones.WARP_STONE.toString(), resourceOverride.toString());

        } catch (Exception e) {
            TFCWaystones.logger.error("FOOK: {}", e);
        }

    }

    @Override
    public EnumAction getItemUseAction(ItemStack itemStack) {
        return EnumAction.DRINK;
    }

    @Override
    public Size getSize(ItemStack stack)
    {
        return Size.NORMAL; // Stored in large vessels
    }

    @Nonnull
    @Override
    public Weight getWeight(ItemStack stack)
    {
        return Weight.MEDIUM;
    }

    public void registerOres(){
        OreDictionary.registerOre("warpStone", this);
        OreDictionary.registerOre("warpStoneCharged", this);
    }

}
